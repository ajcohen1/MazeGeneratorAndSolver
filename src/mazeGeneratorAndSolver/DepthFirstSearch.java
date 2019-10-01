package mazeGeneratorAndSolver;

import java.util.*;

public class DepthFirstSearch extends MazeSolver {

	public DepthFirstSearch() {
		super();
		pf = PathFinders.DEPTH_FIRST_SEARCH;
		pfName = "Depth First Search";
	}


	@Override
	public SingleMazeSolution solveMaze() {

		var nextPointsToSearch = new Stack<LinkedIndex>();
		var startingPoint = new LinkedIndex(startingIndex);

		var visitedPoints = new HashMap<String, int[]>();

		nextPointsToSearch.add(startingPoint);

		while (nextPointsToSearch.size() > 0) {

			var pointToSearch = nextPointsToSearch.pop();
			if (Arrays.equals(pointToSearch.index, endingIndex)) {
				sms = new SingleMazeSolution(maze, pointToSearch, path, pfName);
				return sms;
			}

			if (!visitedPoints.containsKey(Arrays.toString(pointToSearch.index))) {
				visitedPoints.put(Arrays.toString(pointToSearch.index), pointToSearch.index);
				var nextIndicies = getAdjacentIndicies(pointToSearch.index);
				for (var aPoint : nextIndicies) {
					var nextPoint = new LinkedIndex(aPoint, pointToSearch);
					nextPointsToSearch.add(nextPoint);
				}
			}
		}

		sms = new SingleMazeSolution(maze, path, pfName);
		return sms;
	}

}
