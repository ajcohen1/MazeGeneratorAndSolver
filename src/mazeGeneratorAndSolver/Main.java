package mazeGeneratorAndSolver;

import static sbcc.Core.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import static java.lang.System.*;
import static org.apache.commons.lang3.StringUtils.*;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {

		// var maze = new char[4][4];
		MazeGenerator mg = new MazeGenerator();
		// mg.generateMazeAndPrintProgress(4, 20, PrintSpeeds.FAST);
		mg.PRINTgenerateMazeAndStartingAndEndPoints(30, 50, PrintSpeeds.FAST);
		// BreadthFirstSearch bfs = new BreadthFirstSearch(mg, PrintSpeeds.FAST);

		// should accept maze, starting and ending points,

		// bfs.printPath();
		// bfs.printPath();

		MazeSolver bfs = new BreadthFirstSearch(), dfs = new DepthFirstSearch();
		var bfsSln = bfs.solveMaze();
		var dfsSln = dfs.solveMaze();
		SingleMazeSolution[] slns = { bfsSln, dfsSln };

		MazeSolutionExporter mse = new MazeSolutionExporter(slns);
		mse.printSolutions();
	}

}
