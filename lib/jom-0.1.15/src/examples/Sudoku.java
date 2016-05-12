/*******************************************************************************
 * Copyright (c) 2013 Pablo Pavon Mariño.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     Pablo Pavon Mariño - initial API and implementation
 ******************************************************************************/
package examples;

import com.jom.OptimizationProblem;

/** This class contains the code to solve Sudokus, solving an Integer Linear Programming formulation with JOM (Java Optimization Modeler).
 * @author Pablo Pavon Mariño
 * @version March 2013
 * @see http://ait.upct.es/~ppavon/jom 
 */
public class Sudoku 
{

	public static void main(String[] args)
	{
		int [][] partialSudoku = new int [9][9];
		
		/* Take any partial sudoku example to check. The following lines include an example */
  	// Some numbers are already fixed in the sudoku
		partialSudoku [0][1] = 2;
		partialSudoku [0][4] = 6;
		partialSudoku [0][7] = 3;
		partialSudoku [1][0] = 9;
		partialSudoku [1][3] = 1;
		partialSudoku [1][5] = 5;
		partialSudoku [1][8] = 2;
		partialSudoku [2][2] = 3;
		partialSudoku [2][6] = 7;
		partialSudoku [3][1] = 6;
		partialSudoku [3][3] = 3;
		partialSudoku [3][5] = 4;
		partialSudoku [3][7] = 7;
		partialSudoku [4][0] = 5;
		partialSudoku [4][8] = 1;
		partialSudoku [5][1] = 9;
		partialSudoku [5][3] = 5;
		partialSudoku [5][5] = 7;
		partialSudoku [5][7] = 6;
		partialSudoku [6][2] = 1;
		partialSudoku [6][6] = 9;
		partialSudoku [7][0] = 2;
		partialSudoku [7][3] = 6;
		partialSudoku [7][5] = 1;
		partialSudoku [7][8] = 8;
		partialSudoku [8][1] = 5;
		partialSudoku [8][4] = 4;
		partialSudoku [8][7] = 1;
		
		/* Call the method that completes the sudoku */
		int [][] sudoku = Sudoku.solveSudoku (partialSudoku);
		
		checkSudokuValidity (sudoku);
		printSudoku (sudoku);
	}
	
	
	public static int [][] solveSudoku (int [][] partialSudoku)
	{
		/* Create the optimization problem object */
		OptimizationProblem op = new OptimizationProblem();

		/* Add the decision variables to the problem */
		/* x(n,brow,bcol,crow,ccol) = 1, if number (n+1) is in the cell that is inside block (brow,bcol), in the relative position inside the block (crow,ccol)  */
		op.addDecisionVariable("x", true, new int[] { 9 , 3 , 3 , 3 , 3 }, 0, 1); // number, block-row, block-col , cellInBlock-row, cellInBlock-col

		/* Sets the objective function */
		op.setObjectiveFunction("minimize", "sum(x)");
		 
		/* Add the constraints */
  	op.addConstraint(" sum(x,1) == 1" , "cells");  // for all cells: each cell has assigned exactly one number 
  	op.addConstraint(" sum(sum(x,4),2) == 1" , "columns"); // for all number and all columns: each number appears once in each column
  	op.addConstraint(" sum(sum(x,5),3) == 1" , "rows"); // for all numbers and all rows: each number appears once in each row
  	op.addConstraint(" sum(sum(x,5),4) == 1" , "blocks"); // for all numbers and all 3x3 blocks: each number appears once in each block
		
  	// Some numbers are already fixed in the sudoku
  	for (int r = 0 ; r < 9 ; r ++)
  		for (int c = 0 ; c < 9 ; c ++)
  			if (partialSudoku [r][c] != 0)
  				op.addConstraint(" x(" + (partialSudoku [r][c]-1) + "," + r/3 + "," + c/3 + "," + r%3 + "," + c%3 + ") == 1");
		
		/* Call the solver to solve the problem */
		op.solve("glpk");

		if (!op.solutionIsOptimal ()) throw new RuntimeException ("An optimal solution was not found");

		double [][][][][] sol = (double [][][][][]) op.getPrimalSolution("x").toArray();
		
		/* compose the solution */
		int [][] sudoku = new int [9][9];
		for (int n = 0 ; n < 9 ; n ++)
	  	for (int r = 0 ; r < 9 ; r ++)
	  		for (int c = 0 ; c < 9 ; c ++)
	  			if (sol[n][r/3][c/3][r%3][c%3] == 1)
	  				sudoku [r][c] = n+1;	
		
		return sudoku;
	}
	
	/* This method checks that the table given as an input, is a valid sudoku  */
	public static void checkSudokuValidity (int [][] sudoku)
	{
		/* Each cell has a number between 1 and 9 */
		for (int r = 0 ; r < 9 ; r ++)
			for (int c = 0 ; c < 9 ; c ++)
				if ((sudoku [r][c] <= 0) || (sudoku [r][c] >= 10)) throw new RuntimeException ("Only numbers between 1 and 9 in each cell are allowed"); 
		
		/* For each row, each number appears once */
		for (int r = 0 ; r < 9 ; r ++)
			for (int n = 0 ; n < 9 ; n ++)
			{
				int numTimesRepeated = 0;
				for (int c = 0 ; c < 9 ; c ++)
					if (sudoku [r][c] == n) { numTimesRepeated ++; if (numTimesRepeated > 1) throw new RuntimeException ("Number " + n + "appears more than once in row " + r); }
			}

		/* For each row, each number appears once */
		for (int c = 0 ; c < 9 ; c ++)
			for (int n = 0 ; n < 9 ; n ++)
				{
				int numTimesRepeated = 0;
				for (int r = 0 ; r < 9 ; r ++)
					if (sudoku [r][c] == n) { numTimesRepeated ++; if (numTimesRepeated > 1) throw new RuntimeException ("Number " + n + "appears more than once in column " + c); }
			}
		
		
		/* For each 3x3 block, each number appears once */
		for (int bRow = 0 ; bRow < 3 ; bRow ++)
		{
			int [] rowIds = new int [] { bRow*3 , bRow*3+1 , bRow*3+2  };
			for (int bCol = 0 ; bCol < 3 ; bCol ++)
			{
				int [] colIds = new int [] { bCol*3 , bCol*3+1 , bCol*3+2  };
				for (int n = 0 ; n < 9 ; n ++)
				{
					int numTimesRepeated = 0;
					for (int r : rowIds)
						for (int c : colIds)
							if (sudoku [r][c] == n) { numTimesRepeated ++; if (numTimesRepeated > 1) throw new RuntimeException ("Number " + n + "appears more than once in block (" + bRow + "," + bCol + ")"); }
				}
			}
		}
		
		
	}

	/* This method prints a given sudoku  */
	public static void printSudoku (int [][] sudoku)
	{
		/* Each cell has a number between 1 and 9 */
		for (int r = 0 ; r < 9 ; r ++)
		{
			for (int c = 0 ; c < 9 ; c ++)
				System.out.print(sudoku [r][c] + " ");
			System.out.println("");
		}
	}
	
}


