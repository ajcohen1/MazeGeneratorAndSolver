package mazeGeneratorAndSolver;

import java.util.*;
import java.util.concurrent.*;

public class MazeSolutionExporter {
	private final SingleMazeSolution[] allMazeSolutions;
	private final char[][] maze;
	private final int[] startingPoint, endingPoint;
	private final char visitedPoint = '◦';
	private final char finalPathChar = '•';
	private int spaceBetweenMazes = 1;

	private final int mazeWidth;
	private final int mazeHeight;


	public MazeSolutionExporter(SingleMazeSolution[] allMazeSolutions) {
		startingPoint = MazeGenerator.startingPoint;
		endingPoint = MazeGenerator.endingPoint;
		maze = MazeGenerator.maze;
		mazeWidth = maze[0].length;
		mazeHeight = maze.length;
		this.allMazeSolutions = allMazeSolutions;
	}


	public void printSolutions() throws InterruptedException {
		printAttemptedPaths();
		printAllShortestPaths();
		// printSolverKPIs();

	}


	private void printAllShortestPaths() throws InterruptedException {
		boolean notAllSolutionsPrinted = true;
		int numSolutionsPrinted = 0;
		while (notAllSolutionsPrinted) {
			waitAFew();
			clearBoard();
			numSolutionsPrinted = 0;

			for (var aMazeSolution : allMazeSolutions)
				printMazeSolverMethod(aMazeSolution.pfName);
			System.out.println();

			for (int mazeRow = 0; mazeRow < mazeHeight; mazeRow++) {
				for (var aMazeSolution : allMazeSolutions) {
					printMazeRow(aMazeSolution.maze, mazeRow);
					printNumSpaces(spaceBetweenMazes);
				}
				System.out.println();
			}

			for (var aMazeSolution : allMazeSolutions) {
				if (aMazeSolution.endingLinkToShortestPath != null)
					addNextSlnPathPointToMaze(aMazeSolution);
				else
					numSolutionsPrinted++;
			}

			notAllSolutionsPrinted = numSolutionsPrinted < allMazeSolutions.length;
			spaceBetweenMazes = 1;

		}

	}


	private void addNextSlnPathPointToMaze(SingleMazeSolution aMazeSolution) {
		var nextShortestPathPoint = aMazeSolution.endingLinkToShortestPath.index;
		if (!Arrays.equals(nextShortestPathPoint, startingPoint) && !Arrays.equals(nextShortestPathPoint, endingPoint))
			aMazeSolution.maze[nextShortestPathPoint[0]][nextShortestPathPoint[1]] = finalPathChar;
		aMazeSolution.endingLinkToShortestPath = aMazeSolution.endingLinkToShortestPath.prev;

	}


	private void printAttemptedPaths() throws InterruptedException {
		boolean notAllSolutionsPrinted = true;
		int numSolutionsPrinted = 0;

		while (notAllSolutionsPrinted) {
			waitAFew();
			clearBoard();
			numSolutionsPrinted = 0;

			for (var aMazeSolution : allMazeSolutions)
				printMazeSolverMethod(aMazeSolution.pfName);
			System.out.println();

			for (int mazeRow = 0; mazeRow < mazeHeight; mazeRow++) {
				for (var aMazeSolution : allMazeSolutions) {
					printMazeRow(aMazeSolution.maze, mazeRow);
					printNumSpaces(spaceBetweenMazes);
				}
				System.out.println();
			}

			for (var aMazeSolution : allMazeSolutions) {
				if (aMazeSolution.path.size() > 0)
					addNextPathPointToMaze(aMazeSolution);
				else
					numSolutionsPrinted++;
			}

			notAllSolutionsPrinted = numSolutionsPrinted < allMazeSolutions.length;
			spaceBetweenMazes = 1;
		}

	}


	private void addNextPathPointToMaze(SingleMazeSolution aMazeSolution) {
		var nextPoint = aMazeSolution.path.getFirst();
		aMazeSolution.maze[nextPoint[0]][nextPoint[1]] = visitedPoint;
		aMazeSolution.path.remove();
	}


	private void printMazeSolverMethod(String pf) {
		int nameWidth = pf.length();
		boolean nameIsMoreThanMazeWidth = nameWidth > mazeWidth, nameIsSameAsMazeWidth = nameWidth == mazeWidth;
		int leftSpacing, rightSpacing;

		if (nameIsSameAsMazeWidth)
			System.out.print(pf);

		else if (nameIsMoreThanMazeWidth) {

			int unitsOverMaze = nameWidth - mazeWidth;
			boolean unitsOverMazeIsEven = unitsOverMaze % 2 == 0;
			if (unitsOverMazeIsEven) {
				leftSpacing = rightSpacing = unitsOverMaze / 2;
				spaceBetweenMazes += unitsOverMaze;
				printNumSpaces(leftSpacing);
				System.out.print(pf);
				printNumSpaces(rightSpacing);
			} else {
				leftSpacing = unitsOverMaze / 2;
				rightSpacing = leftSpacing + 1;
				printNumSpaces(leftSpacing);
				System.out.print(pf);
				printNumSpaces(rightSpacing);
			}
		}

		else {
			int unitsUnderMaze = mazeWidth - nameWidth;
			boolean unitsUnderMazeIsEven = unitsUnderMaze % 2 == 0;

			if (unitsUnderMazeIsEven) {
				leftSpacing = rightSpacing = unitsUnderMaze / 2;
				printNumSpaces(leftSpacing);
				System.out.print(pf);
				printNumSpaces(rightSpacing);
			} else {
				leftSpacing = unitsUnderMaze / 2;
				rightSpacing = leftSpacing + 1;
				printNumSpaces(leftSpacing);
				System.out.print(pf);
				printNumSpaces(rightSpacing);
			}

		}

	}


	private void printMazeRow(char[][] singleSolutionMaze, int mazeRow) {

		char[] mazeRowToBePrinted = singleSolutionMaze[mazeRow];

		for (char col : mazeRowToBePrinted)
			System.out.print(col);

	}


	private void printNumSpaces(int numSpaces) {
		for (int spaces = 0; spaces < numSpaces; spaces++)
			System.out.print(" ");
	}


	private void clearBoard() {
		for (int numNewLine = 0; numNewLine < 100; numNewLine++)
			System.out.print("\n");
	}


	private void waitAFew() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep(50);
	}

}
