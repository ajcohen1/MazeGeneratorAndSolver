package mazeGeneratorAndSolver;

public class LinkedIndex {
	LinkedIndex prev;
	int[] index;


	LinkedIndex(int[] currentIndex, LinkedIndex prev) {
		this.index = currentIndex;
		this.prev = prev;
	}


	LinkedIndex(int[] index) {
		this.index = index;
		prev = null;
	}
}
