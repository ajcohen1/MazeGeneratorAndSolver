package mazeGeneratorAndSolver;

import java.util.*;

public abstract class MazeSolver {

	public final char[][] maze;
	public final HashMap<String, int[]> blockIndicies;
	public final int[] startingIndex;
	public final int[] endingIndex;
	public final LinkedList<int[]> path = new LinkedList<>();
	public PathFinders pf;
	public String pfName;
	public SingleMazeSolution sms;


	public MazeSolver() {
		maze = MazeGenerator.getFreshMaze();
		blockIndicies = MazeGenerator.walledIndicies;
		startingIndex = MazeGenerator.startingPoint;
		endingIndex = MazeGenerator.endingPoint;
	}


	abstract public SingleMazeSolution solveMaze();


	// common functions
	public ArrayList<int[]> getAdjacentIndicies(int[] currentPoint) {

		int currentPointRow = currentPoint[0], currentPointCol = currentPoint[1];

		int[] topPoint = { currentPointRow - 1, currentPointCol },

				rightPoint = { currentPointRow, currentPointCol + 1 },

				bottomPoint = { currentPointRow + 1, currentPointCol },

				leftPoint = { currentPointRow, currentPointCol - 1 };

		int[][] adjacentIndicies = { topPoint, rightPoint, bottomPoint, leftPoint };

		return getValidAdjacentPoints(adjacentIndicies);
	}


	private ArrayList<int[]> getValidAdjacentPoints(int[][] adjacentIndicies) {
		var validAdjacentPoints = new ArrayList<int[]>();

		int mazeHeight = maze.length, mazeWidth = maze[0].length;
		boolean withinMazeWidth, withinMazeHeight, withinMazeAndNotBlock, notBlock;

		for (int[] adjacentIndex : adjacentIndicies) {

			int adjacentIndexRow = adjacentIndex[0], adjacentIndexCol = adjacentIndex[1];

			notBlock = !blockIndicies.containsKey(Arrays.toString(adjacentIndex));
			withinMazeWidth = adjacentIndexRow > -1 && adjacentIndexRow < mazeHeight;
			withinMazeHeight = adjacentIndexCol > -1 && adjacentIndexCol < mazeWidth;
			withinMazeAndNotBlock = withinMazeWidth && withinMazeHeight && notBlock;

			if (withinMazeAndNotBlock) {
				validAdjacentPoints.add(adjacentIndex);
				if (!Arrays.equals(startingIndex, adjacentIndex) && !Arrays.equals(endingIndex, adjacentIndex))
					path.add(adjacentIndex);
			}
		}

		return validAdjacentPoints;
	}
}
