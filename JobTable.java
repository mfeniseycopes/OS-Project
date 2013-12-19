import java.util.LinkedList;

public class JobTable {

	static LinkedList<Job> table;

	/**
	 * The job table
	 */
	JobTable () {
		table = new LinkedList<Job>();
	}

	/**
	 * Accepts p parameter from sos, creates new Job, adds to job table
	 * @param p job details provided by sos
	 */
	public static void add (int[] p) {
		Job newJob = new Job(p);
		table.add(newJob);
	}

	public static int getPriority (int jobID) {
		return table.get(jobID - 1).priority;
	}

	public static void raisePriority (int jobID) {
		if (table.get(jobID - 1).priority != 5) {
			resetPriorityTime(jobID);
			table.get(jobID - 1).priority = table.get(jobID - 1).priority - 1;
		}
	}

	public static void lowerPriority (int jobID) {
		if (table.get(jobID - 1).priority != 5) {
			resetPriorityTime(jobID);
			table.get(jobID - 1).priority = table.get(jobID - 1).priority + 1;
			System.out.println("Priority value increased");
		}
		else {
			System.out.println("Nothing happened.");
		}
	}

	public static boolean doingIO(int jobID) {
		if (jobID != -1) {
			return table.get(jobID - 1).latched;
		}
		else {
			return false;
		}
	}

	public static void setDoingIO (int jobID) {
		if (jobID != -1) {
			System.out.println("-JobTable sets " + jobID + " to latched");
			table.get(jobID - 1).latched = true;
		}
	}

	public static void unsetDoingIO (int jobID) {
		if (jobID != -1) {
			System.out.println("-JobTable sets " + jobID + " to unlatched");
			table.get(jobID - 1).latched = false;
		}
	}

	public static boolean isBlocked(int jobID) {
		if (jobID != -1) {
			return table.get(jobID - 1).blocked;
		}
		else {
			return false;
		}
	}

	public static void setBlocked (int jobID) {
		if (jobID != -1) {
			System.out.println("-JobTable sets " + jobID + " to blocked");
			table.get(jobID - 1).blocked = true;
		}
	}

	public static void unsetBlocked (int jobID) {
		if (jobID != -1) {
			System.out.println("-JobTable sets " + jobID + " to unblocked");
			table.get(jobID - 1).blocked = false;
		}
	}

	public static boolean isReady(int jobID) {
		if (jobID != -1) {
			return table.get(jobID - 1).ready;
		}
		else {
			return false;
		}
	}

	public static void inMemory(int jobID) {
		table.get(jobID -1).inMemory = true;
	}

	public static void outMemory(int jobID) {
		table.get(jobID -1).inMemory = false;
	}

	public static void setReady (int jobID) {
		if (jobID != -1) {
			System.out.println("-JobTable sets " + jobID + " as ready");
			table.get(jobID - 1).ready = true;
		}
	}

	public static void unsetReady (int jobID) {
		if (jobID != -1) {
			System.out.println("-JobTable sets " + jobID + " to unready");
			table.get(jobID - 1).ready = false;
		}
	}


	/**
	 * Returns memory address for given job
	 * This will be in place even if the job is not yet in memory
	 * @param jobID   jobID of job to query
	 * @return address new memory address
	 */
	public static int getAddress (int jobID) {
		return table.get(jobID - 1).address;
	}

	/**
	 * Changes memory address for given job
	 * This will be in place even if the job is not yet in memory
	 * @param jobID   jobID of job to change
	 * @param address new memory address
	 */
	public static void setAddress (int jobID, int address) {
		table.get(jobID - 1).address = address;
	}
	/**
	 * Clears set address for given job
	 * @param jobID 
	 */
	public static void clearAddress (int jobID) {
		if (!table.get(jobID-1).inMemory) {
			table.get(jobID - 1).address = -1;
		}
	}

	/**
	 * Returns direction of drum swap
	 * @param  jobID jobID of job
	 * @return direction Drum-to-Memory = 0, Memory-to-Drum = 1, No Swap = -1
	 */
	public static int getDirection (int jobID) {
		return table.get(jobID - 1).direction;
	}

	/**
	 * Changes Direction of Drum Swap
	 * @param jobID     jobID of job to be changed
	 * @param direction Drum-to-Memory = 0, Memory-to-Drum = 1, No Swap = -1
	 */
	public static void setDirection (int jobID, int direction) {
		table.get(jobID - 1).direction = direction;
		System.out.println("-JobTable sets swap direction");
	}

	public static int getSize (int jobID) {
		return table.get(jobID -1).size;
	}

	public static int getIO (int jobID) {
		return table.get(jobID - 1).pendingIO;
	}
	
	/**
	 * Increments job's I/O pending count
	 * @param  jobID jobID of job to be incremented
	 * @return       updated I/O pending count
	 */
	public static int incrementIO(int jobID) {
		Job incJob = table.get(jobID - 1);
		incJob.pendingIO++;
		System.out.println("-JobTable increments I/O");
		System.out.println("--Job# " + incJob.idNum + " has " + incJob.pendingIO + " i/o requests");
		return incJob.pendingIO;
	}

	/**
	 * Decrements job's I/O pending count
	 * @param  jobID jobID of job to be decremented
	 * @return       updated I/O pending count
	 */
	public static int decrementIO (int jobID) {
		Job decJob = table.get(jobID - 1);
		decJob.pendingIO--;
		System.out.println("-JobTable decrements I/O");
		System.out.println("--Job# " + decJob.idNum + " has " + decJob.pendingIO + " i/o requests");
		return decJob.pendingIO;
	}

	public static void clearIO (int jobID) {
		table.get(jobID - 1).pendingIO = 0;
	}

	/**
	 * Returns job object of given jobID (I'm not sure I like this access)
	 * @param  jobID of the Job to return
	 * @return       job object of given jobID from table
	 */
	public static Job returnJob (int jobID) {
		if (table.get(jobID - 1) != null) {
			return table.get(jobID - 1);
		}
		else {
			return null;
		}
	}

	/**
	 * Increments given job's currentTime by amount provided
	 * @param  jobID jobID of job to be changed
	 * @param  time  time to be added
	 * @return       job time remaining
	 */
	public static void incrementTime (int jobID, int time) {
		table.get(jobID - 1).currentCPUTime = table.get(jobID - 1).currentCPUTime + time;
	}

	public static int getCurrentCPUTime (int jobID) {
		return table.get(jobID - 1).currentCPUTime;
	}

	public static int getMaxCPUTime (int jobID) {
		return table.get(jobID - 1).maxCPUTime;
	}

	/**
	 * Returns difference between maxTime and currentTime of given job
	 * @param  jobID jobID of job to be queried
	 * @return       the CPU time remaining
	 */
	public static int getTimeLeft (int jobID) {
		if (jobID != -1) {
			return (table.get(jobID - 1).maxCPUTime - table.get(jobID - 1).currentCPUTime);
		}
		else {
			return -1;
		}
	}

	public static int getPriorityTime (int jobID) {
		return table.get(jobID -1).priorityTime;
	}

	public static void resetPriorityTime (int jobID) {
		table.get(jobID -1).priorityTime = os.currentTime;
	}

	public static void terminate(int jobID) {
		table.get(jobID-1).terminated = true;
	}

	public static boolean isTerminated (int jobID) {
		return table.get(jobID-1).terminated;
	}

	public void print () {
		System.out.println("-JobTable Report");
		System.out.print("--Jobs ");
		for (int i = 0; i < table.size(); i++) {
			String t = "";
			String b = "";
			String r = "";
			if (table.get(i).terminated) {
				t = "T";
			}
			if (table.get(i).blocked) {
				b = "B";
			}
			if (table.get(i).ready) {
				r = "R";
			}
			System.out.print((table.get(i).idNum) + ":" + t + b + r + ", ");
		}
		System.out.println("");
	}
}