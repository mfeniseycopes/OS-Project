import java.util.LinkedList;

public class PriorityQueue {
	LinkedList<Integer> q1;
	LinkedList<Integer> q2;
	LinkedList<Integer> q3;
	LinkedList<Integer> q4;
	LinkedList<Integer> q5;
	int size;

	PriorityQueue () {
		q1 = new LinkedList<Integer>();
		q2 = new LinkedList<Integer>();
		q3 = new LinkedList<Integer>();
		q4 = new LinkedList<Integer>();
		q5 = new LinkedList<Integer>();
		size = 0;
	}


	void add (int jobID) {
		if (jobID != -1) {
			switch (JobTable.getPriority(jobID)) {
				case 1:  q1.add(jobID);
						 System.out.println("Added to q1");
	                     break;
	            case 2:  q2.add(jobID);
	            		 System.out.println("Added to q2");
	                     break;
	            case 3:  q3.add(jobID);
	            		 System.out.println("Added to q3");
	                     break;
	            case 4:  q4.add(jobID);
	            		 System.out.println("Added to q4");
	                     break;
	            case 5:  q5.add(jobID);
	            		 System.out.println("Added to q5");
	                     break;
			}
			size++;
			print();
		}
		else {
			System.out.println("FUCK!");
		}
	}

	void remove (int jobID) {
		switch (JobTable.getPriority(jobID)) {
			case 1: q1.remove((Integer)jobID);
					System.out.println("Removed from q1");
                    break;
            case 2: q2.remove((Integer)jobID);
					System.out.println("Removed from q2");
                    break;
            case 3: q3.remove((Integer)jobID);
		            System.out.println("Removed from q3");
                    break;
            case 4: q4.remove((Integer)jobID);
		            System.out.println("Removed from q4");
                    break;
            case 5: q5.remove((Integer)jobID);
		            System.out.println("Removed from q5");
                    break;
		}
		size--;
	}

	void raisePriority (int jobID) {

		remove(jobID);
		JobTable.raisePriority(jobID);
		add(jobID);
	}

	void lowerPriority (int jobID) {
		remove(jobID);
		JobTable.lowerPriority(jobID);
		add(jobID);
	}

	int removeNext () {
		int nextJob = -1;
		if (!q1.isEmpty()) {
			nextJob = q1.remove();
		}
		else if (!q2.isEmpty()) {
			nextJob = q2.remove();
		}
		else if (!q3.isEmpty()) {
			nextJob = q3.remove();
		}
		else if (!q4.isEmpty()) {
			nextJob = q4.remove();
		}
		else if (!q5.isEmpty()) {
			nextJob = q5.remove();
		}
		if (nextJob != -1) {
			size--;
		}
		return nextJob;
	}

	int getNext () {
		int nextJob = -1;
		if (!q1.isEmpty()) {
			nextJob = q1.getFirst();
		}
		else if (!q2.isEmpty()) {
			nextJob = q2.getFirst();
		}
		else if (!q3.isEmpty()) {
			nextJob = q3.getFirst();
		}
		else if (!q4.isEmpty()) {
			nextJob = q4.getFirst();
		}
		else if (!q5.isEmpty()) {
			nextJob = q5.getFirst();
		}
		return nextJob;
	}

	boolean isEmpty () {
		if (size == 0) {
			return true;
		}
		else {
			return false;
		}
	}

	void print () {
		System.out.print("--Pri 1 size : " + q1.size() + " with ");
		for (int i = 0; i < q1.size(); i++) {
			System.out.print(q1.get(i) + ", ");
		}
		
		System.out.print("\n--Pri 2 size : " + q2.size() + " with ");
		for (int i = 0; i < q2.size(); i++) {
			System.out.print(q2.get(i) + ", ");
		}
		System.out.print("\n--Pri 3 size : " + q3.size() + " with ");
		for (int i = 0; i < q3.size(); i++) {
			System.out.print(q3.get(i) + ", ");
		}
		System.out.print("\n--Pri 4 size : " + q4.size() + " with ");
		for (int i = 0; i < q4.size(); i++) {
			System.out.print(q4.get(i) + ", ");
		}
		System.out.print("\n--Pri 5 size : " + q5.size() + " with ");
		for (int i = 0; i < q5.size(); i++) {
			System.out.print(q5.get(i) + ", ");
		}
		System.out.println("");
	}

	int size () {
		return size;
	}
}