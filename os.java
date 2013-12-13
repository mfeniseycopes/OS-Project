import java.util.LinkedList;

public class os {


	static class Dispatcher {

		final int TIMESLICE = 8;
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
			jobTable.print();
			swapper.print();
			ioScheduler.print();
			
		}
	}

	public final static int MEMSIZE = 100;

	public static JobTable jobTable;
	public static Dispatcher dispatcher;
	public static CPUScheduler cpuScheduler;
	public static IOScheduler ioScheduler;
	public static MemoryManager memoryManager;
	public static Swapper swapper;

	public static void div() {
		System.out.println("---------------------------------------------------");
	}

	public static void startup () {

		jobTable = new JobTable();
		dispatcher = new Dispatcher();
		cpuScheduler = new CPUScheduler();
		ioScheduler = new IOScheduler();
		memoryManager = new MemoryManager();
		swapper = new Swapper();
		div();
	}

	public static void Crint (int []a, int []p) {
		System.out.println("CRINT START");

		int jobID = p[1];

		jobTable.add(p);

		boolean swappable = memoryManager.add(jobID);
		if (swappable) {
			swapper.swapIn(jobID);
		}

		dispatcher.report (a, p);
		//dispatcher.update();

		System.out.println("CRINT FINISH");
		div();
	}

	public static void Dskint (int []a, int []p) {
		System.out.println("DSKINT START");

		int[] jobID_jobNeedsMemory = ioScheduler.ioDone();
		int jobID = jobID_jobNeedsMemory[0];
		int jobNeedsMemory = jobID_jobNeedsMemory[1];
		if (jobID != -1) {
			int ioPending = jobTable.decrementIO(jobID);
			if (ioPending == 0 && cpuScheduler.isBlocked(jobID)){
				cpuScheduler.ready(jobID);
			}
		}
		if (jobNeedsMemory != -1) {
			memoryManager.add(jobNeedsMemory);
		}

		dispatcher.report (a, p);
		//dispatcher.update();

		System.out.println("DSKINT FINISH");
		div();
	}

	public static void Drmint (int []a, int []p) {
		System.out.println("DRMINT START");

		int jobID = swapper.swapDone(); // Gets job from swapper
		int direction = jobTable.getDirection(jobID);
		if (direction == 0) {
			cpuScheduler.ready(jobID);
		}
		else if (direction == 1) {
			int newSwapID = memoryManager.free(jobID);
			if (newSwapID != -1) {
				swapper.swapIn(newSwapID);
			}
		}
		
		dispatcher.report (a, p);
		//dispatcher.update();

		System.out.println("DRMINT FINISH");
		div();
	}

	public static void Tro (int []a, int []p) {
		System.out.println("TRO START");

		cpuScheduler.next();

		dispatcher.report (a, p);

		System.out.println("TRO FINISH");
		div();


	}

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
				System.out.println("1");
				cpuScheduler.block();
			}
			// If jobs are pending, block and free
			else if (jobTable.returnIO(jobID) > 0) {
				System.out.println("2");
				cpuScheduler.block();
				memoryManager.free(jobID);
			}
			// If job not using I/O and no pending I/O, ignore
			else {
				System.out.println("3");
				System.out.println("No pending IO");
			}
		}

		dispatcher.report (a, p);
		//dispatcher.update();

		System.out.println("SVC FINISH");
		div();
	}
}