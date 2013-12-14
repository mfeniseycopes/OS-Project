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

	public static void clearAddress (int jobID) {
		table.get(jobID - 1).address = -1;
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

	public static int returnIO (int jobID) {
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
		table.get(jobID - 1).currentTime = table.get(jobID - 1).currentTime + time;
	}

	/**
	 * Returns difference between maxTime and currentTime of given job
	 * @param  jobID jobID of job to be queried
	 * @return       the CPU time remaining
	 */
	public static int returnTimeLeft (int jobID) {
		return (table.get(jobID - 1).maxTime - table.get(jobID - 1).currentTime);
	}

	public static void terminate(int jobID) {
		table.get(jobID-1).terminated = true;
	}

	public void print () {
		System.out.println("-JobTable Report");
		System.out.print("--Jobs ");
		for (int i = 0; i < table.size(); i++) {
			String t = "";
			if (table.get(i).terminated) {
				t = "T";
			}
			System.out.print((table.get(i).idNum) + ":" + t + ", ");
		}
		System.out.println("");
	}
}