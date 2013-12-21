public class Job {
// 		p[1]: job number p[2]: job priority
// 		p[3]: job size (in kb) p[4]: maximum CPU time
// 		p[5]: current time
		// This will model after job given by sos in Crint
	int idNum;
	int priority;
	int size;
	int maxCPUTime;
	int address;
	int currentCPUTime;
	int pendingIO;
	int direction; // 0 = Drum-to-Memory, 1 = Memory-to-Drum, -1 = No Swap
	
	boolean inMemory;
	boolean terminated;
	int priorityTime;
	boolean latched; // doing IO
	boolean ready;
	boolean blocked;
	boolean swapped;
	boolean inDrum;

	/**
	 * CONSTRUCTOR
	 */
	Job (int[] p) {
		// From sos
		idNum 		= p[1];
		priority 	= p[2];
		size 		= p[3];
		maxCPUTime 	= p[4];
		priorityTime = p[5];
		address 	= -1;
		// Defaults
		currentCPUTime = 0;
		inMemory = false;
		pendingIO	= 0;
		direction 	= -1;
		terminated 	= false;
		latched = false;
		ready = false;
		blocked = false;
		swapped = false;
		inDrum = false;
		
	}
}