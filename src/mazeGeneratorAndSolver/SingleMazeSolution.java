package mazeGeneratorAndSolver;

import java.util.*;

public class SingleMazeSolution {
	public final String pfName;
	public LinkedIndex endingLinkToShortestPath;
	public LinkedList<int[]> path;
	public char[][] maze;
	public final LinkedIndex noSolutionPossible = null;


	public SingleMazeSolution(char[][] maze, LinkedIndex endingLinkToShortestPath, LinkedList<int[]> path,
			String pfName) {
		this.maze = maze;
		this.endingLinkToShortestPath = endingLinkToShortestPath;
		this.path = path;
		this.pfName = pfName;
	}


	public SingleMazeSolution(char[][] maze, LinkedList<int[]> path, String pfName) {
		this.maze = maze;
		this.endingLinkToShortestPath = noSolutionPossible;
		this.path = path;
		this.pfName = pfName;
	}

}
