import java.util.LinkedList;
import java.util.ListIterator;

public class CPUScheduler {

	/**
	 * VARIABLES***************************************************************
	 */
	LinkedList<Integer> queue;
	LinkedList<Integer> blocked;

	/**
	 * CONSTRUCTOR*************************************************************
	 */
	CPUScheduler () {
		queue = new LinkedList<Integer>();
		blocked = new LinkedList<Integer>();
	}
	
	/**
	 * PRIVATE METHODS*********************************************************
	 */

	/**
	 * PUBLIC METHODS**********************************************************
	 */
	
	/**
	 * Prints details of CPU Queues
	 */
	public void print () {
		System.out.println("-CPU Report");
		System.out.print("--Ready Jobs: ");
		for (int i = 0; i < queue.size(); i++) {
			int jobID = queue.remove();
			System.out.print(jobID + ", ");
			queue.add(jobID);
		}
		System.out.print("\n--Blocked Jobs: ");
		for (int i = 0; i < blocked.size(); i++) {
			int jobID = blocked.remove();
			System.out.print(jobID + ", ");
			blocked.add(jobID);
		}
		System.out.println("");
	}

	/**
	 * Adds job to ready queue (will remove from blocked if not
	 * a new job)
	 * @param jobID unique identifier for jobs
	 */
	public void ready (int jobID) {
		// If the job is blocked, remove from blocked list
		if(blocked.contains(jobID)) {
			blocked.remove((Integer)(jobID - 1));
		}
		// Then add to ready queue
		queue.add(jobID);
		System.out.println("-CPUScheduler readies job " + jobID);
	}

	/**
	 * Terminates currently running job by removing from queue
	 * @return jobID of terminated job
	 */
	public int terminate () {
		// Removes from ready queue
		int termJob = queue.remove();
		// Sets to terminated in jobTable
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
		// Returns next queue element if queue not empty
		if (!queue.isEmpty()){
			return queue.peek();
		}
		else {
			return -1;
		}
	}

	/**
	 * Tells if job is on blocked list
	 * @param  jobID job to query
	 * @return       if the job is blocked
	 */
	public boolean isBlocked (int jobID) {
		return blocked.contains(jobID);
	}

	/**
	 * Blocks currently running job by removing from queue and
	 * adding to the blocked list
	 */
	public void block () {
		System.out.println("-CPUScheduler blocks current job");
		blocked.add(queue.remove());
	}

	/**
	 * Sends next process to cpu by moving head element to end of queue
	 */
	public void next () {
		System.out.println("-CPUScheduler moves to next job");
		queue.add(queue.remove());
	}

	/**
	 * Provides the size of the ready queue
	 * @return the size of the ready queue
	 */
	public int queueSize() {
		return queue.size();
	}
 }