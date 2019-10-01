package mazeGeneratorAndSolver;

import java.util.*;
import java.util.concurrent.*;

public class MazeGenerator {

	public static char[][] maze;
	public static HashMap<String, int[]> walledIndicies = new HashMap<>();
	public static int[] startingPoint, endingPoint;

	private boolean printProgressively;
	private PrintSpeeds ps;
	final char block = '█';
	final char hole = ' ';
	final int dimensionImpossibleToWall = 4;
	final int otherDimensionImpossibleToWall = 5;
	final double maxHolesPerSubWalls = 3;
	final int mazeLengthImpossibleToWall = dimensionImpossibleToWall - 1;
	final int noHoleFound = -1;
	final int[][] invalidWallIndex = { { -1, -1 }, { -1, -1 } };
	final int minDistanceNeededForTraversal = 2; // Don't want adjacent to wall


	public void PRINTgenerateMazeAndStartingAndEndPoints(int numMazeRows, int numMazeColumns, PrintSpeeds ps) {
		generateMazeAndPrintProgress(numMazeRows, numMazeColumns, ps);
		setRandomStartingAndEndingPoints();
	}


	public void generateMazeAndStartingAndEndPoints(int numMazeRows, int numMazeColumns) {
		generateMaze(numMazeRows, numMazeColumns);
		setRandomStartingAndEndingPoints();
	}


	private void generateMaze(int rows, int cols) {

		printProgressively = false;

		maze = new char[rows][cols];

		addMazeOutline();

		int[] topLeftCorner = { 0, 0 }, bottomRightCorner = { rows - 1, cols - 1 };

		divideChamberIntoFourths(topLeftCorner, bottomRightCorner);
		printMaze();
	}


	public void generateMazeAndPrintProgress(int rows, int cols, PrintSpeeds ps) {

		printProgressively = true;

		this.ps = ps;
		maze = new char[rows][cols];

		addMazeOutline();

		int[] topLeftCorner = { 0, 0 }, bottomRightCorner = { rows - 1, cols - 1 };

		divideChamberIntoFourths(topLeftCorner, bottomRightCorner);
		printMaze();
	}


	// takes in corners, including the walls.
	public void divideChamberIntoFourths(int[] topLeftCorner, int[] bottomRightCorner) {

		// includes the blocks. That is, the highest row and column in a chamber will
		// include the blocks
		int highestRow = topLeftCorner[0], lowestRow = bottomRightCorner[0], leftMostColumn = topLeftCorner[1],
				rightMostColumn = bottomRightCorner[1];

		// break when the chamber is a single cell sized in either direction
		if (chamberIsSizeOneInEitherDirection(highestRow, lowestRow, leftMostColumn, rightMostColumn))
			return;

		// add walls to maze
		var wallBeginningsAndEndings = addVerticalAndHorizontalLineToMaze(leftMostColumn, rightMostColumn, highestRow,
				lowestRow);

		boolean zeroWallsCanBeMade = wallBeginningsAndEndings.size() == 0;
		if (zeroWallsCanBeMade)
			return;

		// get the subWalls
		// Defn: The four sub walls are labeled topWall, rightWall, bottomWall, leftWall
		var subWalls = findSubWalls(wallBeginningsAndEndings);

		// add a single hole to three of the subwalls
		addThreeRandomHolesToSubWalls(subWalls);

		var newChambers = getNewChambersTopLeftAndBottomRightCorners(subWalls, highestRow, lowestRow, leftMostColumn,
				rightMostColumn);

		// repeat for all new chambers
		for (int chamberNum = 0; chamberNum < newChambers.size(); chamberNum++)
			divideChamberIntoFourths(newChambers.get(chamberNum)[0], newChambers.get(chamberNum)[1]);
	}


	//////////////////////////////////////////////////////////////////////////////////
	/* Recursion break condition */
	//////////////////////////////////////////////////////////////////////////////////
	private boolean chamberIsSizeOneInEitherDirection(int highestRow, int lowestRow, int leftMostCol,
			int rightMostCol) {

		boolean verticallyBelowMinDistance = lowestRow - highestRow <= minDistanceNeededForTraversal,
				horizontallyBelowMinDistnace = rightMostCol - leftMostCol <= minDistanceNeededForTraversal;

		if (verticallyBelowMinDistance || horizontallyBelowMinDistnace)
			return true;

		return false;
	}

	//////////////////////////////////////////////////////////////////////////////////
	/* add vert and horiz wall to maze */
	//////////////////////////////////////////////////////////////////////////////////


	// adds 2 perpindicular walls to maze. Returns the beginning and ending index of
	// both walls
	private ArrayList<int[][]> addVerticalAndHorizontalLineToMaze(int leftMostColumn, int rightMostColumn,
			int highestRow, int lowestRow) {

		var wallBeginningsAndEndings = new ArrayList<int[][]>();
		boolean wallImpossible;

		// first, add a vertical line
		int[][] verticalWallBeginningAndEndingIndicies = getVerticalWall(highestRow, lowestRow, leftMostColumn,
				rightMostColumn);
		wallImpossible = !Arrays.deepEquals(verticalWallBeginningAndEndingIndicies, invalidWallIndex);
		if (wallImpossible)
			wallBeginningsAndEndings.add(verticalWallBeginningAndEndingIndicies);

		// second, add a horizontal line
		int[][] horizWallBeginningAndEndingIndex = getHorizontalWall(rightMostColumn, leftMostColumn, highestRow,
				lowestRow);
		wallImpossible = !Arrays.deepEquals(horizWallBeginningAndEndingIndex, invalidWallIndex);
		if (wallImpossible)
			wallBeginningsAndEndings.add(horizWallBeginningAndEndingIndex);

		return wallBeginningsAndEndings;

	}


	// used for both wall making function
	private int incrementHolePosition(int colWithHole) {
		var random = new Random();
		boolean isOne = random.nextBoolean();

		if (isOne)
			return colWithHole + 1;
		return colWithHole - 1;
	}


	//////////////////////////////////////////////////////////////////////////////////
	/* ADD VERTICAL WALL */
	//////////////////////////////////////////////////////////////////////////////////

	private int[][] getVerticalWall(int highestRow, int lowestRow, int leftMostColumn, int rightMostColumn) {

		if (rightMostColumn - leftMostColumn == mazeLengthImpossibleToWall
				|| rightMostColumn - leftMostColumn == mazeLengthImpossibleToWall + 1)
			return invalidWallIndex;

		int wallCol = getColForWall(highestRow, lowestRow, leftMostColumn, rightMostColumn);
		int[] vertWallBeginningIndex = { highestRow, wallCol };
		createVerticalWallBetweenPoints(wallCol, highestRow, lowestRow);

		int[][] vertWallBeginningAndEndingIndicies = { vertWallBeginningIndex, { lowestRow, wallCol } };

		return vertWallBeginningAndEndingIndicies;
	}


	private int getColForWall(int highestRow, int lowestRow, int leftMostColumn, int rightMostColumn) {

		int colWithHole = getColWithHole(highestRow, lowestRow, leftMostColumn, rightMostColumn);

		boolean holeFound = colWithHole != noHoleFound;

		if (holeFound)
			return getVertWallPositionWithHole(colWithHole, leftMostColumn, rightMostColumn);

		return getRandomVerticalWallPosition(highestRow, lowestRow, leftMostColumn, rightMostColumn);
	}


	private int getVertWallPositionWithHole(int colWithHole, int leftMostColumn, int rightMostColumn) {
		// have to choose row either 1 up or down from hole
		int newVertWallColumn;
		boolean withinChamber, withinChamberFromLeft, withinChamberFromRight;
		do {

			newVertWallColumn = incrementHolePosition(colWithHole);
			withinChamberFromLeft = newVertWallColumn >= leftMostColumn + minDistanceNeededForTraversal;
			withinChamberFromRight = newVertWallColumn <= rightMostColumn - minDistanceNeededForTraversal;
			withinChamber = withinChamberFromLeft && withinChamberFromRight;

		} while (!withinChamber);

		return newVertWallColumn;

	}


	private int getColWithHole(int highestRow, int lowestRow, int leftMostColumn, int rightMostColumn) {

		boolean holeAtColumn, holeOnTopRow, holeOnBottomRow;

		for (int col = leftMostColumn + 1; col < rightMostColumn - 1; col++) {

			holeOnTopRow = maze[highestRow][col] == hole;
			holeOnBottomRow = maze[lowestRow][col] == hole;
			holeAtColumn = holeOnTopRow || holeOnBottomRow;
			if (holeAtColumn)
				return col;
		}

		return noHoleFound;
	}


	private int getRandomVerticalWallPosition(int highestRow, int lowestRow, int leftMostColumn, int rightMostColumn) {

		boolean verticallyAdjacentToWall;
		int randomVerticalPositionOnWall;

		do {
			randomVerticalPositionOnWall = ThreadLocalRandom.current().nextInt(leftMostColumn, rightMostColumn);

			verticallyAdjacentToWall = randomVerticalPositionOnWall + minDistanceNeededForTraversal > rightMostColumn
					|| randomVerticalPositionOnWall - minDistanceNeededForTraversal < leftMostColumn;

		} while (verticallyAdjacentToWall);

		return randomVerticalPositionOnWall;
	}


	//////////////////////////////////////////////////////////////////////////////////
	/* ADD HORIZONTAL WALL */
	//////////////////////////////////////////////////////////////////////////////////

	private int[][] getHorizontalWall(int rightMostColumn, int leftMostColumn, int highestRow, int lowestRow) {

		if (lowestRow - highestRow == mazeLengthImpossibleToWall
				|| lowestRow - highestRow == mazeLengthImpossibleToWall + 1)
			return invalidWallIndex;

		int randomRowForWall = getRowForWall(rightMostColumn, leftMostColumn, highestRow, lowestRow);
		int[] horizWallBeginningIndex = { randomRowForWall, leftMostColumn };
		createHorizontalWallBetweenPoints(randomRowForWall, leftMostColumn, rightMostColumn);

		int[][] horizWallBeginningAndEndingIndicies = { horizWallBeginningIndex,
				{ randomRowForWall, rightMostColumn } };

		return horizWallBeginningAndEndingIndicies;
	}


	private int getRowForWall(int rightMostColumn, int leftMostColumn, int highestRow, int lowestRow) {

		int rowWithHole = getRowWithHole(highestRow, lowestRow, leftMostColumn, rightMostColumn);

		boolean holeFound = rowWithHole != noHoleFound;

		if (holeFound)
			return getRowWallPositionWithHole(rowWithHole, highestRow, lowestRow);

		return getRandomHorizWallPosition(rightMostColumn, leftMostColumn, highestRow, lowestRow);
	}


	private int getRowWallPositionWithHole(int rowWithHole, int highestRow, int lowestRow) {
		// have to choose row either 1 up or down from hole
		int newHorizWallRow;
		boolean withinChamber, withinChamberFromTop, withinChamberFromBottom;
		do {

			newHorizWallRow = incrementHolePosition(rowWithHole);
			withinChamberFromTop = newHorizWallRow >= highestRow + minDistanceNeededForTraversal;
			withinChamberFromBottom = newHorizWallRow <= lowestRow - minDistanceNeededForTraversal;
			withinChamber = withinChamberFromTop && withinChamberFromBottom;

		} while (!withinChamber);

		return newHorizWallRow;
	}


	private int getRowWithHole(int highestRow, int lowestRow, int leftMostColumn, int rightMostColumn) {

		boolean holeOnRow, holeOnLeftMostCol, holeOnRightMostCol;

		for (int row = highestRow + 1; row < lowestRow - 1; row++) {

			holeOnLeftMostCol = maze[row][leftMostColumn] == hole;
			holeOnRightMostCol = maze[row][rightMostColumn] == hole;
			holeOnRow = holeOnLeftMostCol || holeOnRightMostCol;
			if (holeOnRow)
				return row;
		}

		return noHoleFound;
	}


	private int getRandomHorizWallPosition(int rightMostColumn, int leftMostColumn, int highestRow, int lowestRow) {
		boolean horizontallyAdjacentToWall;
		int randomHorizontalPositionOnWall;

		do {
			randomHorizontalPositionOnWall = ThreadLocalRandom.current().nextInt(highestRow, lowestRow);

			horizontallyAdjacentToWall = randomHorizontalPositionOnWall + minDistanceNeededForTraversal > lowestRow
					|| randomHorizontalPositionOnWall - minDistanceNeededForTraversal < highestRow;

		} while (horizontallyAdjacentToWall);

		return randomHorizontalPositionOnWall;
	}


	private ArrayList<int[][]> getNewChambersTopLeftAndBottomRightCorners(ArrayList<int[][]> subWalls, int highestRow,
			int lowestRow, int leftMostColumn, int rightMostColumn) {

		int numSubWalls = subWalls.size();
		var newChambersTopLeftAndBottomRightCorners = new ArrayList<int[][]>();

		if (numSubWalls == 1)
			return getNewChambersDividedBySingleWall(subWalls, highestRow, lowestRow, leftMostColumn, rightMostColumn);

		int[] topLeftCorner = { highestRow, leftMostColumn }, bottomRightCorner = { lowestRow, rightMostColumn };

		int[] wallIntersection = subWalls.get(0)[1], topWallBeginning = subWalls.get(0)[0],
				rightWallEnding = subWalls.get(1)[1], bottomWallEnding = subWalls.get(2)[1],
				leftWallBeginning = subWalls.get(3)[0];

		int[][] topLeftChamber = { topLeftCorner, wallIntersection },
				topRightChamber = { topWallBeginning, rightWallEnding },
				bottomRightChamber = { wallIntersection, bottomRightCorner },
				bottomLeftChamber = { leftWallBeginning, bottomWallEnding };

		newChambersTopLeftAndBottomRightCorners.add(topLeftChamber);
		newChambersTopLeftAndBottomRightCorners.add(topRightChamber);
		newChambersTopLeftAndBottomRightCorners.add(bottomRightChamber);
		newChambersTopLeftAndBottomRightCorners.add(bottomLeftChamber);

		return newChambersTopLeftAndBottomRightCorners;

	}


	private ArrayList<int[][]> getNewChambersDividedBySingleWall(ArrayList<int[][]> subWalls, int highestRow,
			int lowestRow, int leftMostColumn, int rightMostColumn) {

		var twoChamberCorners = new ArrayList<int[][]>();

		int deltaRow = subWalls.get(0)[0][0] - subWalls.get(0)[1][0];
		boolean isRowWall = deltaRow == 0;

		if (isRowWall) {
			int wallRow = subWalls.get(0)[0][0];
			int[][] chamber1 = { { highestRow, leftMostColumn }, { wallRow, rightMostColumn } };
			int[][] chamber2 = { { wallRow, leftMostColumn }, { lowestRow, rightMostColumn } };
			twoChamberCorners.add(chamber1);
			twoChamberCorners.add(chamber2);
			return twoChamberCorners;
		}

		// else is a column wall

		int wallColumn = subWalls.get(0)[0][1];

		int[][] chamber1 = { { highestRow, leftMostColumn }, { lowestRow, wallColumn } };
		int[][] chamber2 = { { highestRow, wallColumn }, { lowestRow, rightMostColumn } };
		twoChamberCorners.add(chamber1);
		twoChamberCorners.add(chamber2);
		return twoChamberCorners;
	}


	private ArrayList<int[][]> findSubWalls(ArrayList<int[][]> wallBeginningsAndEndings) {

		int numWalls = wallBeginningsAndEndings.size();

		// if there is only a single wall, then there is no other wall to divide that
		// wall into 2 subwalls. Thus, the only 'subwall' is thw wall given to the
		// function.
		if (numWalls == 1)
			return wallBeginningsAndEndings;

		var subWalls = new ArrayList<int[][]>();
		// defining the walls
		int[][] verticalWall = wallBeginningsAndEndings.get(0), horizontalWall = wallBeginningsAndEndings.get(1);

		// defining attributes of the vertical wall
		int[] verticalWallBeginningIndex = verticalWall[0], verticalWallEndingIndex = verticalWall[1];

		// defining attributes of the horizontal wall
		int[] horizontalWallBeginningIndex = horizontalWall[0], horizontalWallEndingIndex = horizontalWall[1];

		// defining the intersection
		int verticalWallColumn = verticalWallBeginningIndex[1], horizontalWallRow = horizontalWallBeginningIndex[0];
		int[] intersection = { horizontalWallRow, verticalWallColumn };

		// now, finally, to define the subwalls

		int[][] topSubWall = { verticalWallBeginningIndex, intersection },
				rightSubWall = { intersection, horizontalWallEndingIndex },
				bottomSubWall = { intersection, verticalWallEndingIndex },
				leftSubWall = { horizontalWallBeginningIndex, intersection };

		subWalls.add(topSubWall);
		subWalls.add(rightSubWall);
		subWalls.add(bottomSubWall);
		subWalls.add(leftSubWall);

		return subWalls;

	}


	private void addThreeRandomHolesToSubWalls(ArrayList<int[][]> subWalls) {

		// recursive division maze making calls for us to choose three subwalls in
		// random order to add a hole into
		ArrayList<int[][]> randomizedSubWalls = new ArrayList(subWalls);
		Collections.shuffle(randomizedSubWalls);

		for (int holesAdded = 0; holesAdded < maxHolesPerSubWalls
				&& holesAdded < randomizedSubWalls.size(); holesAdded++)
			addHoleToSubWall(randomizedSubWalls.get(holesAdded));

	}


	private void addHoleToSubWall(int[][] singleSubWall) {

		int[] wallBeginning = singleSubWall[0], wallEnding = singleSubWall[1];

		boolean noChangeInColumns = (wallBeginning[0] - wallEnding[0]) == 0;
		char direction = (noChangeInColumns) ? 'h' : 'v';

		switch (direction) {
		case 'h':
			addHoleToHorizontalSubWall(wallBeginning, wallEnding);
			waitSomeTimeAndPrint();
			break;
		case 'v':
			addHoleToVerticalSubWall(wallBeginning, wallEnding);
			waitSomeTimeAndPrint();
			break;
		}
	}


	private void addHoleToVerticalSubWall(int[] wallBeginning, int[] wallEnding) {
		char hole = ' ';

		int beginningRow = wallBeginning[0], endingRow = wallEnding[0];
		int colWithWall = wallBeginning[1];
		int rowWithHole = getHolePositionOnVertWall(beginningRow, endingRow, colWithWall);
		maze[rowWithHole][colWithWall] = hole;

		int[] holeIndex = { rowWithHole, colWithWall };
		if (walledIndicies.containsKey(Arrays.toString(holeIndex)))
			walledIndicies.remove(Arrays.toString(holeIndex));
	}


	private void addHoleToHorizontalSubWall(int[] wallBeginning, int[] wallEnding) {
		char hole = ' ';

		int beginningCol = wallBeginning[1], endingCol = wallEnding[1];
		int rowWithWall = wallBeginning[0];
		int colWithHole = getHolePositionOnHorizWall(beginningCol, endingCol, rowWithWall);
		maze[rowWithWall][colWithHole] = hole;

		int[] holeIndex = { rowWithWall, colWithHole };
		if (walledIndicies.containsKey(Arrays.toString(holeIndex)))
			walledIndicies.remove(Arrays.toString(holeIndex));
	}


	private int getHolePositionOnVertWall(int beginningRow, int endingRow, int col) {

		boolean atWall;
		int holePosition;

		do {

			holePosition = ThreadLocalRandom.current().nextInt(beginningRow, endingRow);
			atWall = holePosition == beginningRow || holePosition == endingRow;

		} while (atWall);

		return holePosition;
	}


	private int getHolePositionOnHorizWall(int beginningCol, int endingCol, int row) {

		boolean atWall;
		int holePosition;

		do {

			holePosition = ThreadLocalRandom.current().nextInt(beginningCol, endingCol);
			atWall = holePosition == beginningCol || holePosition == endingCol;
		} while (atWall);

		return holePosition;
	}


	public void createHorizontalWallBetweenPoints(int rowForWall, int beginningCol, int endingCol) {

		int wallEnding = endingCol;
		if (maze[rowForWall][wallEnding] == hole)
			wallEnding--;

		for (; beginningCol < wallEnding; beginningCol++) {
			maze[rowForWall][beginningCol] = block;
			int[] blockIndex = { rowForWall, beginningCol };
			walledIndicies.put(Arrays.toString(blockIndex), blockIndex);
			waitSomeTimeAndPrint();
		}
	}


	public void createVerticalWallBetweenPoints(int colForWall, int beginningRow, int endingRow) {

		int wallEnding = endingRow;
		if (maze[endingRow][colForWall] == hole)
			wallEnding--;

		for (; beginningRow < wallEnding; beginningRow++) {
			maze[beginningRow][colForWall] = block;
			int[] blockIndex = { beginningRow, colForWall };
			walledIndicies.put(Arrays.toString(blockIndex), blockIndex);
			waitSomeTimeAndPrint();
		}
	}


	private void addMazeOutline() {

		// add outline to sides
		for (int row = 0; row < maze.length; row++) {
			maze[row][0] = maze[row][maze[0].length - 1] = block;

			int[] leftSideIndex = { row, 0 }, rightSideIndex = { row, maze[0].length - 1 };
			walledIndicies.put(Arrays.toString(rightSideIndex), rightSideIndex);
			walledIndicies.put(Arrays.toString(leftSideIndex), leftSideIndex);
		}

		// add outline to top and bottom
		for (int col = 0; col < maze[0].length; col++) {
			maze[0][col] = maze[maze.length - 1][col] = block;

			int[] topSideIndex = { 0, col }, bottomSideIndex = { maze.length - 1, col };
			walledIndicies.put(Arrays.toString(topSideIndex), topSideIndex);
			walledIndicies.put(Arrays.toString(bottomSideIndex), bottomSideIndex);
		}

	}


	private void waitSomeTimeAndPrint() {

		for (int numNewLine = 0; numNewLine < 30; numNewLine++)
			System.out.print("\n");

		if (!printProgressively)
			return;

		int numMilliseconds = 0;

		switch (ps) {
		case SLOW:
			numMilliseconds = 40;
			break;
		case AVERAGE:
			numMilliseconds = 20;
			break;
		case FAST:
			numMilliseconds = 10;
			break;
		}

		try {
			TimeUnit.MILLISECONDS.sleep(numMilliseconds);
			printMaze();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void waitSomeTimeAndPrint(PrintSpeeds printSpeed) {
		for (int numNewLine = 0; numNewLine < 30; numNewLine++)
			System.out.print("\n");

		int numMilliseconds = 0;

		switch (printSpeed) {
		case SLOW:
			numMilliseconds = 40;
			break;
		case AVERAGE:
			numMilliseconds = 20;
			break;
		case FAST:
			numMilliseconds = 10;
			break;
		}

		try {
			TimeUnit.MILLISECONDS.sleep(numMilliseconds);
			printMaze();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void waitSomeTimeAndPrint(char[][] maze, PrintSpeeds printSpeed) {
		for (int numNewLine = 0; numNewLine < 30; numNewLine++)
			System.out.print("\n");

		int numMilliseconds = 0;

		switch (printSpeed) {
		case SLOW:
			numMilliseconds = 40;
			break;
		case AVERAGE:
			numMilliseconds = 20;
			break;
		case FAST:
			numMilliseconds = 10;
			break;
		}

		try {
			TimeUnit.MILLISECONDS.sleep(numMilliseconds);
			printMaze(maze);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void printMaze() {

		for (char[] x : maze) {
			for (char y : x) {
				System.out.print(y);
			}
			System.out.println();
		}

		System.out.println();
	}


	public static void printMaze(char[][] maze) {

		for (char[] x : maze) {
			for (char y : x) {
				System.out.print(y);
			}
			System.out.println();
		}

		System.out.println();
	}


	public HashMap<String, int[]> getWalledIndicies() {
		return walledIndicies;
	}


	public static char[][] getFreshMaze() {

		var freshMaze = new char[maze.length][];
		for (int i = 0; i < maze.length; i++)
			freshMaze[i] = maze[i].clone();

		return freshMaze;
	}


	public void setRandomStartingAndEndingPoints() {

		int[] randomStartingPoint, randomEndingPoint;

		do {
			randomStartingPoint = getRandomMazePoint();
			randomEndingPoint = getRandomMazePoint();
		} while (Arrays.equals(randomStartingPoint, randomEndingPoint));

		maze[randomStartingPoint[0]][randomStartingPoint[1]] = 'S';
		maze[randomEndingPoint[0]][randomEndingPoint[1]] = 'E';

		startingPoint = randomStartingPoint;
		endingPoint = randomEndingPoint;

	}


	private int[] getRandomMazePoint() {

		int randomRow, randomCol;
		boolean pointIsBlock;
		int[] randomPoint = new int[2];

		do {
			randomRow = ThreadLocalRandom.current().nextInt(0, maze.length);
			randomCol = ThreadLocalRandom.current().nextInt(0, maze[0].length);

			randomPoint[0] = randomRow;
			randomPoint[1] = randomCol;

			pointIsBlock = walledIndicies.containsKey(Arrays.toString(randomPoint));
		} while (pointIsBlock);

		return randomPoint;
	}

}
