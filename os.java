import java.util.LinkedList;

public class os {

	// This is used to update sos and report the current 
	// state of the system
	static class Dispatcher {

		final int TIMESLICE = 20;
		int lastTime;
		int currentTime;
		int sliceTime;
		int sliceJob;

		/**
		 * CONSTRUCTOR
		 */
		Dispatcher () {
			lastTime = 0;
			currentTime = 0;
			sliceTime = TIMESLICE;
			sliceJob = -1;
		}

		/** 
		 * changes parameters for a, p to return back to sos
		 * @param a cpu status
		 * @param p process details
		 */
		public void report (int[] a, int[] p) {

			// Get time details
			lastTime = currentTime;
			currentTime = p[5];
			int timeElapsed = currentTime - lastTime;

			// Increment jobs current time
			if (sliceJob != -1) {
				jobTable.incrementTime(sliceJob, timeElapsed);
			}

			// Gets job to be run from CPU
			int jobToRun = cpuScheduler.current();

			// If job exists, set the sliceTime & sliceJob
			if (jobToRun != -1) {
				// Checks to see if we need to start a new slice
				// If it is the same job then, we need to decrement the slice time
				if (sliceJob == jobToRun) {
					sliceTime =- timeElapsed;
					if (sliceTime <= 0) {
						sliceTime = TIMESLICE;
					}
				}
				// If a different job is to be run, then we need to reset the sliceTime
				// and change the sliceJob
				else {
					sliceTime = TIMESLICE;
				}
				// Assigns next job;
				sliceJob = jobToRun;

				// Checks to make sure maxTime not exceeded, corrects sliceTime if necessary
				int timeLeft = jobTable.returnTimeLeft(sliceJob);
				if (timeLeft < sliceTime) {
					sliceTime = jobTable.returnTimeLeft(sliceJob);
					if (timeLeft == 0) {
						cpuScheduler.terminate();
						ioScheduler.clear(sliceJob);
						int nextSwapJob = memoryManager.free(sliceJob);
						if (nextSwapJob != -1) {
							swapper.swapIn(nextSwapJob);
						}
						sliceJob = cpuScheduler.current();
						sliceTime = TIMESLICE;
					}
				}
			}
			else {
				sliceJob = jobToRun;
			}

			System.out.println("\n*****REPORTS******");
			// Setting the sos's a, p values
			// If there is no job, set to idle
			if (sliceJob == -1) {
				System.out.println("-Dispatcher has no job to run");
				a[0] = 1;
			}
			// If there is a job, set to run,
			// Update p with address, size
			else {
				Job job = jobTable.returnJob(sliceJob);
				a[0] = 2;
				p[1] = job.idNum;
				p[2] = job.address;
				p[3] = job.size;
				p[4] = sliceTime;

				System.out.println("-Dispatcher job report");
				System.out.println("--Job ID      : " + sliceJob);
				System.out.println("--Job Address : " + job.address);
				System.out.println("--Job Size    : " + job.size);
				System.out.println("--Slice Time  : " + sliceTime);
				System.out.println("--CPU Time    : " + job.currentTime);
				System.out.println("--Max CPU Time: " + job.maxTime);
				System.out.println("--Time Left   : " + jobTable.returnTimeLeft(sliceJob));
			}
			cpuScheduler.print();
			jobTable.print();
			memoryManager.print();
			swapper.print();
			ioScheduler.print();
			
		}
	}

	/**
	 * VARIABLES***************************************************************
	 */
	public final static int MEMSIZE = 100;

	public static JobTable jobTable;
	public static Dispatcher dispatcher;
	public static CPUScheduler cpuScheduler;
	public static IOScheduler ioScheduler;
	public static MemoryManager memoryManager;
	public static Swapper swapper;

	/**
	 * PUBLIC METHODS**********************************************************
	 */
	// Used to separate events in OS
	public static void div() {
		System.out.println("---------------------------------------------------");
	}

	// Called by SOS, used to initialize variables
	public static void startup () {

		jobTable = new JobTable();
		dispatcher = new Dispatcher();
		cpuScheduler = new CPUScheduler();
		ioScheduler = new IOScheduler();
		memoryManager = new MemoryManager();
		swapper = new Swapper();
		div();
	}

	/**
	 * Accepts new job into system
	 * @param []a to be modified for sos
	 * @param []p p[1]: job number, p[2]: job priority,
	 *            p[3]: job size (in kb) p[4]: maximum CPU time,
	 *            p[5]: current time
	 *            to be modified for sos
	 */
	public static void Crint (int []a, int []p) {
		System.out.println("CRINT START");

		int jobID = p[1];
		// Adds to JobTable
		jobTable.add(p);

		// If room found in memory, begin swapping
		boolean swappable = memoryManager.add(jobID);
		if (swappable) {
			swapper.swapIn(jobID);
		}

		// Report
		dispatcher.report (a, p);

		System.out.println("CRINT FINISH");
		div();
	}

	/**
	 * An I/O operation has finished
	 * @param []a to be modified for sos
	 * @param []p to be modified for sos
	 */
	public static void Dskint (int []a, int []p) {
		System.out.println("DSKINT START");

		// Gets jobID of completed I/O
		// and (if there is a job in I/O which is not in memory)
		// the jobID of the job which need to be brought in
		int[] jobID_jobNeedsMemory = ioScheduler.ioDone();
		// More understandable variable names
		int jobID = jobID_jobNeedsMemory[0];
		int jobNeedsMemory = jobID_jobNeedsMemory[1];

		// If job which finished I/O is valid
		if (jobID != -1) {
			// Decrement & get it's I/O pending
			int ioPending = jobTable.decrementIO(jobID);
			// Check to see if it was blocked and is ready
			// to continue processing
			if (ioPending == 0 && cpuScheduler.isBlocked(jobID)){
				cpuScheduler.ready(jobID);
			}
		}
		// If there is a job that needs memory
		if (jobNeedsMemory != -1) {
			// Place it in the memory queue
			memoryManager.add(jobNeedsMemory);
		}

		// Report
		dispatcher.report (a, p);

		System.out.println("DSKINT FINISH");
		div();
	}

	/**
	 * Memory swap complete
	 * @param []a to be modified for sos
	 * @param []p to be modified for sos
	 */
	public static void Drmint (int []a, int []p) {
		System.out.println("DRMINT START");

		// Gets completed memory swap from swapper
		int jobID = swapper.swapDone();
		// Gets completed swap Direction
		int direction = jobTable.getDirection(jobID);
		// If swapped into memory, ready job
		if (direction == 0) {
			cpuScheduler.ready(jobID);
		}
		// Otherwise, free the space
		else if (direction == 1) {
			int newSwapID = memoryManager.free(jobID);
			if (newSwapID != -1) {
				swapper.swapIn(newSwapID);
			}
		}

		// Report
		dispatcher.report (a, p);

		System.out.println("DRMINT FINISH");
		div();
	}

	/**
	 * Timeslice ended
	 * @param []a to be modified for sos
	 * @param []p to be modified for sos
	 */
	public static void Tro (int []a, int []p) {
		System.out.println("TRO START");

		// Moves CPU to next job in queue
		cpuScheduler.next();

		// Report
		dispatcher.report (a, p);

		System.out.println("TRO FINISH");
		div();
	}

	/**
	 * Running Job is requesting service
	 * @param []a to be modified for sos
	 * @param []p to be modified for sos
	 */
	public static void Svc (int []a, int []p) {
		System.out.println("SVC START");

		// The job is requesting termination
		if (a[0] == 5) {
			System.out.println("Requesting termination");
			int jobID = cpuScheduler.terminate();
			jobTable.setDirection(jobID, -1); // Job swaps out of memory automatically
			memoryManager.free(jobID);
			ioScheduler.clear(jobID);
		}
		// The job is requesting another I/O operation
		else if (a[0] == 6) {
			System.out.println("Requesting another i/o operation");

			int jobID = cpuScheduler.current();
			jobTable.incrementIO(jobID);
			ioScheduler.add(jobID);

		}
		// The job is requesting to be blocked until all pending
		// I/O requests are completed
		else if (a[0] == 7) {
			System.out.println("Block until all pending I/O requests are completed");
			int jobID = cpuScheduler.current();
			// If job is using I/O, block, but don't free
			if (ioScheduler.doingIO(jobID)) {
				System.out.println("-I/O: Job is doing I/O");
				cpuScheduler.block();
			}
			// If jobs are pending, block and free
			else if (jobTable.returnIO(jobID) > 0) {
				System.out.println("-I/O: Job has pending I/O");
				cpuScheduler.block();
				//memoryManager.free(jobID);
				swapper.swapOut(jobID);
			}
			// If job not using I/O and no pending I/O, ignore
			else {
				System.out.println("-I/O: Job has no pending I/O");
			}
		}

		// Report
		dispatcher.report (a, p);

		System.out.println("SVC FINISH");
		div();
	}
}