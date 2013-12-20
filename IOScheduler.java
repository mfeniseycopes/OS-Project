import java.util.LinkedList;

public class IOScheduler {
	

	/**
	 * VARIABLES***************************************************************
	 */
	LinkedList<Integer> defaultQueue;
	LinkedList<Integer> blockedInQueue;
	LinkedList<Integer> blockedOutQueue;
	LinkedList<Integer> terminatedQueue;
	int inIO;

	/**
	 * CONSTRUCTOR*************************************************************
	 */
	IOScheduler () {
		defaultQueue = new LinkedList<Integer>();
		blockedInQueue = new LinkedList<Integer>();
		blockedOutQueue = new LinkedList<Integer>();
		terminatedQueue = new LinkedList<Integer>();
		inIO = -1;
	}

	/**
	 * PRIVATE METHODS*********************************************************
	 */
	void ioCheck () {
		System.out.println("--BlockedIn Queue has " + blockedInQueue.size());
		System.out.println("--Terminated Queue has " + terminatedQueue.size());
		System.out.println("--Default Queue has " + defaultQueue.size());
		if (inIO == -1) {
			if (!terminatedQueue.isEmpty()) {
				inIO = terminatedQueue.remove();
				//System.out.println("--Terminated Queue has " + terminatedQueue.size());
			}
			else if (!blockedInQueue.isEmpty()) {
				inIO = blockedInQueue.remove();
				//System.out.println("--BlockedIn Queue has " + blockedInQueue.size());
			}
			else if (!defaultQueue.isEmpty()) {
				inIO = defaultQueue.remove();
				
			}
			if (inIO != -1) {
				System.out.println("--Job is sent to do I/O");
				JobTable.setDoingIO(inIO);
				sos.siodisk(inIO);
			}
		}
	}

	/**
	 * PUBLIC METHODS**********************************************************
	 */
	
	public int ioMemCheck() {
		if (!blockedOutQueue.isEmpty() && blockedInQueue.isEmpty()) {
			return blockedOutQueue.peek();
		}
		else {
			return -1;
		}
	}
	
	/**
	 * Prints status of I/O
	 */
	public void print () {
		System.out.println("-I/O Report:");
		System.out.println("--In I/O: " + inIO);
		System.out.print("--Next In Queue: ");
		// if (!ioQueue.isEmpty()) {
		// 	System.out.println(ioQueue.peek());
		// }
		// else {
		// 	System.out.println("Nothing");
		// }
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

		defaultQueue.add(jobID);
		ioCheck();
	}

	/**
	 * When I/O has finished, removes current job from inIO
	 * Tries to start next job if available
	 * Updates inIO
	 * @return jobID of process that just finished I/O and jobID of 
	 *               process that needs to be brought into memory
	 */
	public int ioDone () {
		// If job which finished I/O is valid
		int jobID = inIO;
		inIO = -1;
		if (jobID != -1) {
			// Decrement & get it's I/O pending
			JobTable.decrementIO(jobID);
			JobTable.unsetDoingIO(jobID);
			// If the job is ready to be unblocked
			if (JobTable.getIO(jobID) == 0 && JobTable.isBlocked(jobID)) {
				JobTable.unsetBlocked(jobID);
			}
		}

		ioCheck(); 
		return jobID;
	}

	// Moves a job's I/O to the correct queue
	public void moveIO (int jobID) {
		int ioCount = JobTable.getIO(jobID);
		if (ioCount > 0) {
			// Query the jobTable to determine which list I/O should move to
			// Move to terminated 
			if (JobTable.isTerminated(jobID)) {
				// Remove from default, blockedIn
				System.out.println("--Moving to terminatedQueue");
				while (defaultQueue.contains((Integer)jobID)) {
					defaultQueue.remove((Integer)jobID);
					terminatedQueue.add(jobID);
				}
				while (blockedInQueue.contains((Integer)jobID)) {
					blockedInQueue.remove((Integer)jobID);
					terminatedQueue.add(jobID);
				}
			}
			// Move to blocked
			else if (JobTable.isBlocked(jobID)) {
				// Move to blockedOut
				if (JobTable.getAddress(jobID) == -1) {
					System.out.println("--Moving to blockedOutQueue");
					// Remove from blockedIn
					while (blockedInQueue.contains((Integer)jobID)) {
						blockedInQueue.remove((Integer)jobID);
						blockedOutQueue.add(jobID);
					}
					while (defaultQueue.contains((Integer)jobID)) {
						defaultQueue.remove((Integer)jobID);
						blockedOutQueue.add(jobID);
					}
				}
				// Move to blockedIn
				else {
					System.out.println("--Moving to blockedInQueue");
					// Remove from default
					while (defaultQueue.contains((Integer)jobID)) {
						defaultQueue.remove((Integer)jobID);
						blockedInQueue.add(jobID);
					}
				}
			}
		}
	}

	

	

}