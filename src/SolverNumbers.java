/* File: SolverNumbers.java  -  April 2011 */
package sudoku;

import java.util.Arrays;

/**
 * Creates the number infrastructure used by the solver.
 *
 * @author Rudi Theunissen
 */
public class SolverNumbers {

   /** Array that is still to be solved. */
  private int[] solvingArray;
  /** Array that contains the values of a solving array that has been solved. */
  private int[] solvedArray;
  /** Instance of {@link Solver} that handles the solving algorithm. */
  private Solver solver;

  /**
   * Default Constructor - initialises the arrays.
   */
  public SolverNumbers() {
    solver = new Solver();
    solvingArray = new int[81];
    solvedArray = new int[81];
  }

  /**
   * Adds a number to the grid.
   */
  public void addNumber(int userInt, int col, int row) {
    int arrayPosition = Algorithms.toIndex(row, col);
    solvingArray[arrayPosition] = userInt;
  }

  /**
   * If the solving request was valid, run the solver and copy the solved values
   * into the solving array.
   */
  public boolean attemptSolve() {
    if (!solver.checkValidSolveRequest(solvingArray)) {
      return false;
    } else {
      solver.solveArray(solvingArray);
      System.arraycopy(solver.getSolvedArray(), 0, solvedArray, 0, 81);
      return true;
    }
  }

  /**
   * Resets all values of the current solving array to 0.
   */
  public void newSolver() {
    Arrays.fill(solvingArray, 0);
  }

  /**
   * Returns the array that is still to be solved.
   * @return the array that is still to be solved.
   */
  public int[] getSolvingArray() {
    return solvingArray;
  }

  /**
   * Returns the array containing the solved values.
   * @return the array containing the solved values.
   */
  public int[] getSolvedArray() {
    return solvedArray;
  }
}
