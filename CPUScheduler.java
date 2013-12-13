import java.util.LinkedList;
import java.util.ListIterator;

public class CPUScheduler {

	/**
	 * VARIABLES
	 */
	LinkedList<Integer> queue;
	LinkedList<Integer> blocked;

	/**
	 * CONSTRUCTOR
	 */
	CPUScheduler () {
		queue = new LinkedList<Integer>();
		blocked = new LinkedList<Integer>();
	}
	

	/**
	 * PRIVATE METHODS
	 */
	

	/**
	 * PUBLIC METHODS
	 */
	public void status (int[] a, int[] p) {
		// System.out.println("-CPUScheduler setting status");
		// if (queue.isEmpty()) {
		// 	a[0] = 1; // No jobs to run
		// 	System.out.println("--Idle");
		// }
		// else {
		// 	int runID = queue.peek().idNum;
		// 	Job run = JobTable.returnJob(runID);
		// 	System.out.println("--Running with:");
		// 	System.out.println("--Job     : " + run.idNum);
		// 	System.out.println("--Address : " + run.address);
		// 	System.out.println("--Size    : " + run.size);
		// }
	}

	/**
	 * Adds job to ready queue (will remove from blocked if not
	 * a new job)
	 * @param jobID unique identifier for jobs
	 */
	public void ready (int jobID) {
		if(blocked.contains(jobID)) {
			blocked.remove((Integer)(jobID - 1));
		}
		queue.add(jobID);
		System.out.println("-CPUScheduler readies job " + jobID);
	}

	/**
	 * Terminates currently running job by removing from queue
	 * @return jobID of terminated job
	 */
	public int terminate () {
		
		int termJob = queue.remove();
		JobTable.terminate(termJob);

		System.out.println("-CPUScheduler terminates job " + termJob);

		return termJob;
	}

	/**
	 * Gets the currently running (head of queue) jobID and returns
	 * If the queue is empty returns -1
	 * @return int jobID or -1 if no job running
	 */
	public int current () {
		if (!queue.isEmpty()){
			System.out.println("--Queue is not empty" + queue.size());
			for (int i = 0; i < queue.size(); i++) {
				System.out.println(queue.get(i));
			}
			return queue.peek();
		}
		else {
			System.out.println("--Queue is empty");
			return -1;
		}
	}

	public boolean isBlocked (int jobID) {
		return blocked.contains(jobID);
	}

	/**
	 * Blocks currently running job by removing from queue and
	 * adding to the blocked list
	 */
	public void block () {
		System.out.println("-CPUScheduler blocks current job");
		int block = queue.remove();
		blocked.add(block);
	}


	/**
	 * Sends next process to cpu by moving head element to end of queue
	 */
	public void next () {
		System.out.println("-CPUScheduler moves to next job");
		queue.add(queue.remove());
	}

	public void print () {
		for (int i = 0; i < queue.size(); i++) {
			System.out.println("Will print queue");
		}
	}
}