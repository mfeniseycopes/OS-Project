import java.util.Queue;
import java.util.ArrayDeque;

public class Swapper {

	final int IN = 0;
	final int OUT = 1;

	/**
	 * VARIABLES***************************************************************
	 */
	Queue<Integer> swapQueue;
	int inDrum;

	/**
	 * CONSTRUCTOR*************************************************************
	 */
	Swapper () {
		swapQueue = new ArrayDeque<Integer>();
		inDrum = -1;
	}
	
	/**
	 * PRIVATE METHODS*********************************************************
	 */
	
	/**
	 * [doSwap description]
	 * @param jobID [description]
	 */
	void doSwap (int jobID) {
		// If drum not in use
		if (inDrum == -1 && swapQueue.isEmpty()){
			System.out.println("--Beginning swap");
			inDrum = jobID;
			Job job = JobTable.returnJob(jobID);
			sos.siodrum (job.idNum, job.size, job.address, job.direction);
		}
		else {
			System.out.println("--Added to swap queue");
			swapQueue.add(jobID);
		}

	}

	/**
	 * PUBLIC METHODS**********************************************************
	 */
	
	/**
	 * Prints status of Drum and Drum Queue
	 */
	public void print () {
		System.out.println("-Swap Report:");
		System.out.println("--In Drum: " + inDrum);
		System.out.print("--Next In Queue: ");
		if (!swapQueue.isEmpty()) {
			System.out.println(swapQueue.peek());
		}
		else {
			System.out.println("Nothing");
		}
	}

	/**
	 * [swapIn description]
	 * @param jobID [description]
	 */
	public void swapIn (int jobID) {
		System.out.println("-Swapper beginning swap in");

		JobTable.setDirection(jobID, 0);
		doSwap(jobID);
	} 
	/**
	 * [swapOut description]
	 * @param jobID [description]
	 */
	public void swapOut (int jobID) {
		System.out.println("-Swapper beginning swap out");

		JobTable.setDirection(jobID, 1);
		if (inDrum == -1 && swapQueue.isEmpty()){
			System.out.println("--Swap starting");
			inDrum = jobID;
			Job job = JobTable.returnJob(jobID);
			sos.siodrum (job.idNum, job.size, job.address, 1);
		}
		else {
	System.out.println("--Added to swap queue");
			swapQueue.add(jobID);
		}
	}

	/**
	 * Handles drmint
	 * @return idNum of job drum-to-memory, -1 if memory-to-drum
	 */
	public int swapDone () {
		System.out.println("-Swapper getting swap details");
		int jobID = inDrum;
		Job job = JobTable.returnJob(jobID);

		// Status
		if (job.direction == 1) {
			System.out.println("--Memory-to-Drum done for job " + jobID);
		}
		else {
			System.out.println("--Drum-to-Memory done for job " + jobID);
		}
		//JobTable.setDirection (jobID, -1);

		if (swapQueue.isEmpty()) {
			inDrum = -1;

			// Status
			System.out.println("--Mem Queue empty, no jobs to swap");
		}
		else {
			inDrum = swapQueue.remove();
			Job swapJob = JobTable.returnJob(inDrum);
			sos.siodrum (swapJob.idNum, swapJob.size, swapJob.address, swapJob.direction);

			// Status
			String descriptor = "";
			if (swapJob.direction == 0) {
				descriptor = " to ";
			}
			else if (swapJob.direction == 1) {
				descriptor = " from ";
			}
			System.out.println("--Begin swapping Job " + swapJob.idNum +
				" with size " + swapJob.size + descriptor + swapJob.address);
		}

		return jobID;
	}

	

}