import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.ArrayDeque;

public class MemoryManager {

	// For the freeSpaceTable
	class FreeSpace {
		int start;
		int size;

		FreeSpace (int _start, int _size) {
			start = _start;
			size = _size;
		}
	}

	/**
	 * VARIABLES
	 */
	LinkedList<FreeSpace> freeSpaceTable;
	LinkedList<Integer> jobsInMemory;
	Queue<Integer> memQueue;

	/**
	 * CONSTRUCTOR
	 */
	MemoryManager () {
		// Initial freeSpace is all of memory
		FreeSpace empty = new FreeSpace (0, os.MEMSIZE);
		freeSpaceTable = new LinkedList<FreeSpace>();
		freeSpaceTable.add(empty);
		// Keeps track of jobs in memory and their base addresses
		jobsInMemory = new LinkedList<Integer>();
		// MemoryQueue initially empty
		memQueue = new ArrayDeque<Integer>();
	}

	/**
	 * PRIVATE METHODS***************************
	 */
	void print() {
		FreeSpace iterator;
		for (int i = 0; i < freeSpaceTable.size(); i++) {
			iterator = freeSpaceTable.get(i);
			System.out.println("--FreeSpace " + i + " : Address=" + iterator.start + " Size=" + iterator.size);
		}
	}


	/**
	 * Checks for availability in freeSpaceTable
	 * If found, adds job to jobsInMemory, updates freeSpaceTable
	 * Else, add jobs to memQueue
	 * @param job to be added
	 * @return  int address for swapper, returns -1 if no such address
	 */
	int findFreeSpace (int jobSize) {
		// Determines whether space was found
		boolean success = false;
		int address = -1;

		// Checks freeSpaceTable for first available memory location
		System.out.print("--Checking for free space...");
		FreeSpace iterator;
		//System.out.println(freeSpaceTable.size());
		for (int i = 0; i < freeSpaceTable.size(); i++) {
			iterator = freeSpaceTable.get(i);
			if (iterator.size >= jobSize) {
				address = iterator.start;
				iterator.start = iterator.start + jobSize;
				iterator.size = iterator.size - jobSize;
				freeSpaceTable.remove(i);
				if (iterator.size != 0) {
					freeSpaceTable.add(iterator);
				}
				success = true;
				break;
			}
		}
		if (success) {
			System.out.println("Found");
		}
		else {
			System.out.println("Not Found");
		}
		print();
		return address;
	}



	/**
	 * PUBLIC METHODS****************************
	 */
	
	/**
	 * adds new job to memory from id number
	 * checks to see if space is available
	 * Sets address in job table
	 * returns true if space is found and swapper should run
	 * false if otherwise
	 * @param  idNum job to add into memory
	 * @return       whether space was found
	 */
	public boolean add (int jobID) {
		int size = JobTable.returnJob(jobID).size;
		System.out.println ("-MemoryManager adds job " + jobID);
		if (memQueue.isEmpty()) {
			System.out.println("--No jobs in queue");
			int address = findFreeSpace (size);
			if (address != -1) {
				JobTable.setAddress(jobID, address);
				jobsInMemory.add(jobID);
				System.out.println("--Freespace found");
				return true;
			}
			else {
				System.out.println("--Freespace not found");
				return false;
			}
		}

		else {
			memQueue.add(jobID);
			System.out.println("--Jobs already in queue");
			return false;
		}
	}

	/**
	 * Allocates jobs memory to freespace table, will append current 
	 * freespace if they are contiguous
	 * Removes job from jobsInMemory
	 * @param jobID jobID of job with cleared memory
	 */
	public int free (int jobID) {
		System.out.println("-MemoryManager begins to free job");
		//FreeSpace newSpace;
		FreeSpace iterator;
		FreeSpace iterator2;
		Job job = JobTable.returnJob(jobID);

		// Free the space
		// First check to see if current freespace can be appended to
		boolean append = false;
		for (int i = 0; i < freeSpaceTable.size(); i++) {

			iterator = freeSpaceTable.get(i);
			System.out.println("--Job to Free start=" + job.address + " end=" + (job.size+job.address));
			System.out.println("--Iterator start=" + iterator.start + " end=" + (iterator.size+iterator.start));

			// Check to see if perfect fit between freespaces
			// If freespace does not start at zero && there is another freespace
			if (freeSpaceTable.get(i).start != 0 && (i + 1) < freeSpaceTable.size()) {
				iterator2 = freeSpaceTable.get(i+1);
				// If the boundaries match on both sides
				if (job.address == (iterator.start + iterator.size) && 
				   (job.address + job.size) == iterator2.start) {
				   	System.out.println("--Freespace appended to middle of existing two");

					iterator.size = iterator.size + job.size + iterator2.size;
					freeSpaceTable.add(i, iterator);
					freeSpaceTable.remove(i);
					freeSpaceTable.remove(i+1);
					append = true;
					break;
				}
			}

			if (job.address == (iterator.start + iterator.size)){
				System.out.println("--Freespace appended to end of existing");
				iterator.size = iterator.size + job.size;
				freeSpaceTable.remove(i);
				freeSpaceTable.add(i, iterator);
				append = true;
				break;
			}
			if ((job.address + job.size) == iterator.start) {
				System.out.println("--Freespace appended to start of existing");
				iterator.start = job.address;
				iterator.size = iterator.size + job.size;

				freeSpaceTable.remove(i);
				freeSpaceTable.add(i, iterator);
				append = true;
				break;
			}
		}
		// If the space couldn't be appended, need to add into correct location
		if (append == false) {
			System.out.println("--New freespace added");
			FreeSpace newSpace = new FreeSpace(job.address, job.size);
			for (int i = 0; i < freeSpaceTable.size(); i++) {
				iterator = freeSpaceTable.get(i);
				if (i == 0 && newSpace.start < iterator.start) {
					freeSpaceTable.add(i, newSpace);
					break;
				}
				if ( (i+1) < freeSpaceTable.size()) {
					iterator2 = freeSpaceTable.get(i+1);
					if (iterator.start < newSpace.start && newSpace.start < iterator2.start) {
						freeSpaceTable.add(i+1, newSpace);
						break;	
					}
				}
				if ( i == freeSpaceTable.size()-1) {
					freeSpaceTable.add(newSpace);
					break;
				}
			}
			
		}
		jobsInMemory.remove((Integer) jobID);


		// Send another job
		System.out.println("-MemoryManager attempts to add another job to memory");
		if (!memQueue.isEmpty()) {
			int nextID = memQueue.peek();
			System.out.println(nextID);
			int address = findFreeSpace (JobTable.getSize(nextID));
			if (address != -1) {
				JobTable.setAddress(nextID, address);
			}
			System.out.println("--" + nextID + " added and sent to swapper");
			print();
			return memQueue.remove();
		}
		else {
			System.out.println("--No jobs in queue");
			print();
			return -1;
		}
	}
}