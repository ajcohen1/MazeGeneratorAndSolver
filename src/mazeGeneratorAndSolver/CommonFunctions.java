package mazeGeneratorAndSolver;

import java.util.*;

public class CommonFunctions {

	private char[][] maze;
	private int[] startingIndex, endingIndex;
	HashMap<String, int[]> blockIndicies;
	private PrintSpeeds ps;
	private final char visitedPoint = '◦';
	private final char finalPathChar = '•';


	public CommonFunctions(char[][] maze, int[] startingIndex, int[] endingIndex, HashMap<String, int[]> blockIndicies,
			PrintSpeeds ps) {
		this.maze = maze;
		this.startingIndex = startingIndex;
		this.endingIndex = endingIndex;
		this.blockIndicies = blockIndicies;
		this.ps = ps;
	}


	public ArrayList<int[]> getAdjacentIndicies(int[] currentPoint, char[][] maze, ArrayList<int[]> path) {

		int currentPointRow = currentPoint[0], currentPointCol = currentPoint[1];

		int[] topPoint = { currentPointRow - 1, currentPointCol },

				rightPoint = { currentPointRow, currentPointCol + 1 },

				bottomPoint = { currentPointRow + 1, currentPointCol },

				leftPoint = { currentPointRow, currentPointCol - 1 };

		int[][] adjacentIndicies = { topPoint, rightPoint, bottomPoint, leftPoint };

		return getValidAdjacentPoints(adjacentIndicies, maze, path);
	}


	private ArrayList<int[]> getValidAdjacentPoints(int[][] adjacentIndicies, char[][] maze, ArrayList<int[]> path) {
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


	public void printPath(LinkedIndex endingPoint) {

		if (endingPoint.index[0] == -1) {
			System.out.print("NO PATH POSSIBLE");
			return;
		}

		var pathIndex = endingPoint.prev;
		boolean isNotStartingPoint = pathIndex.prev != null;

		while (isNotStartingPoint) {
			var index = pathIndex.index;
			int row = index[0], col = index[1];
			maze[row][col] = finalPathChar;
			// mg.waitSomeTimeAndPrint(ps);
			pathIndex = pathIndex.prev;
			isNotStartingPoint = pathIndex.prev != null;
		}

		MazeGenerator.printMaze(maze);
	}
}
