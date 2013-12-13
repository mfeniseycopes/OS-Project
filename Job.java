public class Job {
// 		p[1]: job number p[2]: job priority
// 		p[3]: job size (in kb) p[4]: maximum CPU time
// 		p[5]: current time
		// This will model after job given by sos in Crint
	int idNum;
	int size;
	int address;
	int pendingIO;
	int direction; // 0 = Drum-to-Memory, 1 = Memory-to-Drum, -1 = No Swap
	int currentTime;
	int maxTime;
	boolean terminated;

	/**
	 * CONSTRUCTOR
	 */
	Job (int[] p) {
		idNum = p[1];
		size = p[3];
		address = -1;
		pendingIO = 0;
		direction = -1;
		currentTime = 0;
		maxTime = p[4];
		terminated = false;
	}
}