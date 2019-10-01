package mazeGeneratorAndSolver;

import java.util.*;

public class BreadthFirstSearch extends MazeSolver {

	BreadthFirstSearch() {
		super();
		pf = PathFinders.BREADTH_FIRST_SEARCH;
		pfName = "Breadth First Search";
	}


	@Override
	public SingleMazeSolution solveMaze() {
		LinkedList<LinkedIndex> nextPoints = new LinkedList<>();

		var visitedIndicies = new HashMap<String, int[]>();
		boolean currentPointIsEndingPoint;
		boolean pointNotVisited;

		var startingNode = new LinkedIndex(startingIndex);

		nextPoints.add(startingNode);
		visitedIndicies.put(Arrays.toString(startingIndex), startingIndex);

		while (nextPoints.size() > 0) {

			// dequeue
			var currentPoint = nextPoints.getFirst();
			nextPoints.remove(0);

			currentPointIsEndingPoint = Arrays.equals(currentPoint.index, endingIndex);
			if (currentPointIsEndingPoint) {
				sms = new SingleMazeSolution(maze, currentPoint, path, pfName);
				return sms;
			}

			ArrayList<int[]> adjacentIndicies = getAdjacentIndicies(currentPoint.index);

			for (int[] adjacentIndex : adjacentIndicies) {

				var adjacentIndexStr = Arrays.toString(adjacentIndex);
				pointNotVisited = !visitedIndicies.containsKey(adjacentIndexStr);
				if (pointNotVisited) {
					visitedIndicies.put(adjacentIndexStr, adjacentIndex);
					var nextPoint = new LinkedIndex(adjacentIndex, currentPoint);
					nextPoints.add(nextPoint);
				}

			}
		}

		sms = new SingleMazeSolution(maze, path, pfName);
		return sms;

	}
}
