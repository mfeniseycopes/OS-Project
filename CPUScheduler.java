import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

public class CPUScheduler {

	/**
	 * VARIABLES***************************************************************
	 */

	class PriorityQueue {
		LinkedList<Integer> q1;
		LinkedList<Integer> q2;
		LinkedList<Integer> q3;
		LinkedList<Integer> q4;
		LinkedList<Integer> q5;

		PriorityQueue () {
			q1 = new LinkedList<Integer>();
			q2 = new LinkedList<Integer>();
			q3 = new LinkedList<Integer>();
			q4 = new LinkedList<Integer>();
			q5 = new LinkedList<Integer>();
		}


		void add (int jobID) {
			if (jobID != -1) {
				switch (JobTable.getPriority(jobID)) {
					case 1:  q1.add(jobID);
							 System.out.println("Added to q1");

		                     break;
		            case 2:  q2.add(jobID);
		            		 System.out.println("Added to q2");
		                     break;
		            case 3:  q3.add(jobID);
		            		 System.out.println("Added to q3");
		                     break;
		            case 4:  q4.add(jobID);
		            		 System.out.println("Added to q4");
		                     break;
		            case 5:  q5.add(jobID);
		            		 System.out.println("Added to q5");
		                     break;
				}

				print();
			}
			else {
				System.out.println("FUCK!");
			}
		}

		int remove () {
			int i = -1;
			switch (JobTable.getPriority(runningJob)) {
				case 1: i = q1.remove();
						System.out.println("Removed from q1");
	                    break;
	            case 2: i = q2.remove();
						System.out.println("Removed from q2");
	                    break;
	            case 3: i = q3.remove();
			            System.out.println("Removed from q3");
	                    break;
	            case 4: i = q4.remove();
			            System.out.println("Removed from q4");
	                    break;
	            case 5: i = q5.remove();
			            System.out.println("Removed from q5");
	                    break;
			}
			return i;
		}

		void raisePriority () {

			
			JobTable.raisePriority(runningJob);
			add(runningJob);
		}

		void lowerPriority () {
			
			JobTable.lowerPriority(runningJob);
			add(runningJob);
		}

		int getNext () {
			int nextJob = -1;
			if (!q1.isEmpty()) {
				nextJob = q1.remove();
			}
			else if (!q2.isEmpty()) {
				nextJob = q2.remove();
			}
			else if (!q3.isEmpty()) {
				nextJob = q3.remove();
			}
			else if (!q4.isEmpty()) {
				nextJob = q4.remove();
			}
			else if (!q5.isEmpty()) {
				nextJob = q5.remove();
			}

			return nextJob;
		}

		void print () {
			System.out.print("--Pri 1 size : " + q1.size() + " with ");
			for (int i = 0; i < q1.size(); i++) {
				System.out.print(q1.get(i) + ", ");
			}
			
			System.out.print("\n--Pri 2 size : " + q2.size() + " with ");
			for (int i = 0; i < q2.size(); i++) {
				System.out.print(q2.get(i) + ", ");
			}
			System.out.print("\n--Pri 3 size : " + q3.size() + " with ");
			for (int i = 0; i < q3.size(); i++) {
				System.out.print(q3.get(i) + ", ");
			}
			System.out.print("\n--Pri 4 size : " + q4.size() + " with ");
			for (int i = 0; i < q4.size(); i++) {
				System.out.print(q4.get(i) + ", ");
			}
			System.out.print("\n--Pri 5 size : " + q5.size() + " with ");
			for (int i = 0; i < q5.size(); i++) {
				System.out.print(q5.get(i) + ", ");
			}
			System.out.println("");
		}

		int size () {
			int count = 0;
			count = count + q1.size();
			count = count + q2.size();
			count = count + q3.size();
			count = count + q4.size();
			count = count + q5.size();
			return count;
		}
	}


	PriorityQueue queue;
	
	//LinkedList<Integer> blocked;

	// ADDED
	final static int TIMESLICE =100;
	// ADDED
	int runningJob;
	int slice;


	final int RUN_WAIT = 1000;

	/**
	 * CONSTRUCTOR*************************************************************
	 */
	CPUScheduler () {
		
		queue 	= new PriorityQueue();
		//blocked = new LinkedList<Integer>();

		// ADDED
		runningJob = -1;
		slice 	= TIMESLICE;
	}
	
	/**
	 * PRIVATE METHODS*********************************************************
	 */
	int getSlice(int jobID, int currentSlice) {
		if (JobTable.getTimeLeft(jobID) < currentSlice) {
			return JobTable.getTimeLeft(jobID);
		}
		else {
			return currentSlice;
		}
	}

	/**
	 * PUBLIC METHODS**********************************************************
	 */
	
	/**
	 * Prints details of CPU Queues
	 */
	public void print () {
		System.out.println("-CPU Report");
		System.out.println("--In CPU  : " + runningJob);
		// System.out.print("--Ready Jobs: ");
		// for (int i = 0; i < queue.size(); i++) {
		// 	int jobID = queue.remove();
		// 	System.out.print(jobID + ", ");
		// 	queue.add(jobID);
		// }
		// System.out.print("\n--Blocked Jobs: ");
		// for (int i = 0; i < blocked.size(); i++) {
		// 	int jobID = blocked.remove();
		// 	System.out.print(jobID + ", ");
		// 	blocked.add(jobID);
		// }
		System.out.println("");
	}

	public void update() {
		int timeElapsed = os.currentTime - os.lastTime;
		// Increments interrupted job's time time & current slice
		if (runningJob != -1) {
			JobTable.incrementTime(runningJob, timeElapsed);
			slice = slice - timeElapsed;
		}
	}

	/**
	 * Adds job to appropriate ready queue (will remove from blocked if not
	 * a new job)
	 * @param jobID unique identifier for jobs
	 */
	public void ready (int jobID) {
		// If the job is not blocked
		if(!JobTable.isBlocked(jobID) && !JobTable.isReady(jobID)) {
			// Then add to appropriate ready queue
			queue.add(jobID);
			JobTable.setReady(jobID);
			System.out.println("-CPUScheduler readies job " + jobID + " with priority " + JobTable.getPriority(jobID));
		}
		
		print();
	}

	/**
	 * Terminates currently running job
	 * @return jobID of terminated job
	 */
	public int terminate () {								
		
		int killedJob = runningJob;
		runningJob = -1;
		// Sets to terminated in jobTable
		JobTable.terminate(killedJob);
		JobTable.unsetReady(killedJob);

		System.out.println("-CPUScheduler terminates job " + killedJob);

		return killedJob;
	}

	/**
	 * Gets the currently running (head of queue) jobID and returns
	 * If the queue is empty returns -1
	 * @return int jobID or -1 if no job running
	 */
	public int current () {
		return runningJob;
	}

	// /**
	//  * Tells if job is on blocked list
	//  * @param  jobID job to query
	//  * @return       if the job is blocked
	//  */
	// public boolean isBlocked (int jobID) {
	// 	return blocked.contains(jobID);
	// }

	/**
	 * Blocks currently running job by removing from queue and
	 * adding to the blocked list
	 */
	public void block () {
		System.out.println("-CPUScheduler blocks current job");
		JobTable.setBlocked(runningJob);
		JobTable.unsetReady(runningJob);
		runningJob = -1;
	}

	/**
	 * Sends next process to cpu by moving head element to end of queue
	 */
	public int[] next (int[] a, int[] p) {
		// Will 
		int[] returnVars = {-1, -1}; // {freeMemory, swapOut}

		if (runningJob != -1) {

			// If time remains in slice, continue
			if (slice > 0) {
				slice = getSlice(runningJob, slice);
				// Running stays the same
				System.out.println("-CPUScheduler resumes Job " + runningJob + 
					" with " + slice + " remaining");
			}
			// Check to see if job has exceeded its max CPU time
			// If it has, then need to free it's memory and terminate it
			else if (JobTable.getTimeLeft(runningJob) <= 0) {
				System.out.println("-CPUScheduler stops Job " + runningJob + 
					" (exceeds max CPU time)");
				returnVars[0] = runningJob;
				JobTable.terminate(runningJob);
				JobTable.unsetReady(runningJob);
				runningJob = -1; 
			}
			// If no time remains, check if job has exceeded max time in memory
			// If it has, then need to lower its priority and return to memManager for 
			// potential swapout
			else if ((os.currentTime - JobTable.getPriorityTime(runningJob)) >= RUN_WAIT) {
				System.out.println("-CPUScheduler reduces priority of Job " + runningJob);
				if (queue.size() > 3 && !JobTable.doingIO(runningJob)) {
					returnVars[1] = runningJob;
					JobTable.lowerPriority(runningJob);
					JobTable.unsetReady(runningJob);
				}
				else {
					queue.lowerPriority();
					JobTable.lowerPriority(runningJob);
				}
				runningJob = -1;
			}
			// The job has no slice remaining and needs to be put at back of queue
			else {
				queue.add(runningJob);
				runningJob = -1;
			}
		}
		// If there is no running job yet
		if (runningJob == -1) {
			runningJob = queue.getNext();
			System.out.println("Next job = " + runningJob);
			slice = getSlice(runningJob, TIMESLICE);
		}
		// If there is absolutely nothing in the queues
		if (runningJob == -1) {
			// Set CPU to idle
			a[0] = 1;
		}
		else {
			a[0] = 2;
			p[1] = runningJob;
			p[2] = JobTable.getAddress(runningJob);
			p[3] = JobTable.getSize(runningJob);
			p[4] = slice;
		}

		queue.print();
		
		return returnVars;
	}

	/**
	 * Provides the size of the ready queue
	 * @return the size of the ready queue
	 */
	public int queueSize() {
		return queue.size();
	}
 }