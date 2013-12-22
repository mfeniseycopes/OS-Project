public class Job 
{
// 		p[1]: job number p[2]: job priority
// 		p[3]: job size (in kb) p[4]: maximum CPU time
// 		p[5]: current time
		// This will model after job given by sos in Crint
	int idNum;		// The job's id number
	int size;			// Size of job in Memory-to-Drum
	int maxCPUTime;	// Maximum time to run in CPU- terminate after
	int address;		// The address of the job if in memory
	int currentCPUTime;	// Time ran in CPU
	int pendingIO;		// Number of I/O requests pending
	int direction; 	// 0 = Drum-to-Memory, 1 = Memory-to-Drum, -1 = No Swap
	boolean inMemory;	// True is job is in memory
	boolean terminated;	// True if job has terminated
	int priorityTime;	// Amount of time job has been in memory
	boolean latched; 	// doing IO
	boolean ready;		// Job is in CPU ready queue
	boolean blocked;	// Job is blocked /(waiting for I/O)
	boolean swapped;	// Job has been swapped out of memory at least once
	boolean inSwapQueue;// Job is waiting to be swapped /(in or out)
	boolean inDrum;	// Currently swapping into/out of memory

	/**
	 * CONSTRUCTOR
	 */
	Job (int[] p) 
	{
		// From sos
		idNum = p[1];
		size = p[3];
		maxCPUTime = p[4];
		priorityTime = p[5];
		// Job defaults
		address = -1;
		currentCPUTime = 0;
		inMemory = false;
		pendingIO	= 0;
		direction 	= -1;
		terminated 	= false;
		latched = false;
		ready = false;
		blocked = false;
		swapped = false;
		inSwapQueue = false;
		inDrum = false;
		
	}
}