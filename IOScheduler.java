import java.util.Queue;
import java.util.ArrayDeque;

public class IOScheduler {
	

	/**
	 * VARIABLES
	 */
	Queue<Integer> ioQueue;
	Queue<Integer> priQueue;
	int inIO;

	/**
	 * CONSTRUCTOR
	 */
	IOScheduler () {
		ioQueue = new ArrayDeque<Integer>();
		priQueue = new ArrayDeque<Integer>();
		inIO = -1;
	}

	/**
	 * PRIVATE METHODS
	 */
	

	/**
	 * PUBLIC METHODS
	 */
	

	public boolean doingIO (int jobID) {
		if (jobID == inIO) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Accepts new job for I/O
	 * Checks to see if I/O available, if so calls siodisk with new job
	 * Else adds new job to I/O queue
	 * @param idNum [description]
	 */
	public void add (int jobID) {
		System.out.println("-IOScheduler accepts new job");
		if (inIO == -1 && ioQueue.isEmpty()) {
			System.out.println("--Job is sent to do I/O");
			inIO = jobID;
			sos.siodisk(inIO);
		}
		else {
			System.out.println("--Job is added to I/O Queue");
			ioQueue.add(jobID);
		}
	}

	/**
	 * When I/O has finished, removes current job from inIO
	 * Tries to start next job if available
	 * Updates inIO
	 * @return jobID of process that just finished I/O and jobID of 
	 *               process that needs to be brought into memory
	 */
	public int[] ioDone () {
		int jobID = inIO;
		int memJob = -1;
		boolean jobAcquired = false;

		// Checks priority queue, handles the olded blocked job 
		// which is waiting to complete I/O
		if (!priQueue.isEmpty()) {
			if (JobTable.getAddress(priQueue.peek()) != -1) {
				inIO = priQueue.remove();
				sos.siodisk(inIO);
				jobAcquired = true;
			}
		}
		if (jobAcquired == false) {
			// Checks regular queue
			if (ioQueue.isEmpty()){
				inIO = -1;
			}
			else {
				// If the next I/O requester is in memory, start it
				if (JobTable.getAddress(ioQueue.peek()) != -1) {
					inIO = ioQueue.remove();
					sos.siodisk(inIO);
				}
				// If the next I/O is not in memory,
				// push to priority queue, request for job to move into memory
				else {
					memJob = ioQueue.remove();
					// Only moves to priority queue if its empty or the same job exists there
					if (priQueue.isEmpty() || priQueue.peek() == memJob) {
						priQueue.add(memJob);
					}
					else {
						ioQueue.add(memJob);
					}
				}
			}
		}

		int[] returnFields = {jobID, memJob};
		return returnFields;
	}

	public void clear (int jobID) {
		System.out.println("-IOScheduler clearing job's I/O");
		// Clears pendingIO count in jobTable
		JobTable.clearIO (jobID);
		// Removes I/O tasks in queue for given job
		int count = 0;
		for (int i = 0; i < ioQueue.size(); i++) {
			if (ioQueue.contains((Integer) jobID)) {
				ioQueue.remove((Integer) jobID);
				i--;
				count++;
			}
			System.out.println("--Removed " + count + " from queue");
		}
		if (inIO == jobID) {
			inIO = -1;
			System.out.println("--Current I/O marked as invalid");
		}
	}

	public void print () {
		System.out.println("-I//O Report:");
		System.out.println("--In I//O: " + inIO);
		System.out.print("--Next In Queue: ");
		if (!ioQueue.isEmpty()) {
			System.out.println(ioQueue.peek());
		}
		else {
			System.out.println("Nothing");
		}
	}

}