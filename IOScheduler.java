import java.util.Queue;
import java.util.ArrayDeque;

public class IOScheduler {
	

	/**
	 * VARIABLES***************************************************************
	 */
	Queue<Integer> ioQueue;
	Queue<Integer> priQueue;
	int inIO;

	/**
	 * CONSTRUCTOR*************************************************************
	 */
	IOScheduler () {
		ioQueue = new ArrayDeque<Integer>();
		priQueue = new ArrayDeque<Integer>();
		inIO = -1;
	}

	/**
	 * PRIVATE METHODS*********************************************************
	 */
	

	/**
	 * PUBLIC METHODS**********************************************************
	 */
	
	/**
	 * Prints status of I/O
	 */
	public void print () {
		System.out.println("-I/O Report:");
		System.out.println("--In I/O: " + inIO);
		System.out.print("--Next In Queue: ");
		if (!ioQueue.isEmpty()) {
			System.out.println(ioQueue.peek());
		}
		else {
			System.out.println("Nothing");
		}
	}

	/**
	 * Checks to see if given job is doing I/O
	 * @param  jobID job to query
	 * @return       boolean true if job in I/O, false otherwise
	 */
	public boolean doingIO (int jobID) {
		if (jobID == inIO) {
			System.out.println("--Job " + jobID + " is doing I/O");
			return true;
		}
		else {
			System.out.println("--Job " + jobID + " is not doing I/O");
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
		JobTable.incrementIO(jobID);
		//ioQueue.add(jobID);
		if (inIO == -1 && ioQueue.isEmpty()) {
			System.out.println("--Job is sent to do I/O");
			inIO = jobID;
			JobTable.setDoingIO(inIO);
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

		// If job which finished I/O is valid
		if (inIO != -1) {
			// Decrement & get it's I/O pending
			JobTable.decrementIO(inIO);
			JobTable.unsetDoingIO(inIO);
			// If the job is ready to be unblocked
			if (JobTable.getIO(inIO) == 0 && JobTable.isBlocked(inIO)) {
				JobTable.unsetBlocked(inIO);
			}
		}

		int jobID = inIO;
		int memJob = -1;
		boolean jobAcquired = false;

		// Checks priority queue, handles the olded blocked job 
		// which is waiting to complete I/O
		if (!priQueue.isEmpty()) {
			// If I/O is still valid (job not terminated)
			if (JobTable.getAddress(priQueue.peek()) != -1) {
				inIO = priQueue.remove();
				JobTable.setDoingIO(inIO);
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
					JobTable.setDoingIO(inIO);
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
					inIO = -1;
				}
			}
		}
		// Returns the job which finished and the next job to try in memory
		int[] returnFields = {jobID, memJob};
		return returnFields;
	}

	/**
	 * Clears given jobs pending I/O and invalidates any current I/O
	 * @param jobID job to query
	 */
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
		// if (inIO == jobID) {
		// 	inIO = -1;
		// 	System.out.println("--Current I/O marked as invalid");
		// }
	}

	

}