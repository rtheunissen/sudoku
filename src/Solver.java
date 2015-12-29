/* File: Solver.java  -  April 2011 */
package sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Solves a Sudoku puzzle.
 *
 * @author Rudi Theunissen (adapted from <a href="http://goo.gl/eY3Pv">here)</a>
 */
public class Solver {

   /** Array of characters that are to be solved. */
   public char[] unsolvedArray;
   /** Array of integers containing solved values. */
   public int[] solvedArray;
   /** ArrayList containing the indexes of non-zero values. */
   private ArrayList<Integer> nonZeros;
   /** True if an array is solved. */
   private boolean solved;

   /**
    * Default Constructor - initialises the arrays.
    */
   public Solver() {
      nonZeros = new ArrayList<Integer>();
      solvedArray = new int[81];
      unsolvedArray = new char[81];
   }

   /**
    * Solves a given array and sets the values of <em>solvedArray</em> to these
    * values.
    *
    * @param input the array that is to be solved.
    */
   public void solveArray(int[] input) {
      setupSolver(input);
      Solve();
   }

   /**
    * Checks to see if a solve request was valid.
    *
    * @param input array to check.
    * @return true if the request was valid.
    */
   public boolean checkValidSolveRequest(int[] input) {
      setNonZeros(input);
      return (Algorithms.isValidSolve(input, nonZeros));
   }

   /**
    * Sets up the solver.
    *
    * @param input array containing the values to be solved.
    */
   private void setupSolver(int[] input) {
      solved = false;
      for (int i = 0; i < 81; i++) {
         unsolvedArray[i] = (char) (input[i] + '0');
      }
   }

   /**
    * Solving algorithm, adapted from <a href="http://goo.gl/eY3Pv">here</a>.
    */
   public void Solve() {
      for (char c : unsolvedArray) {
         if (c == '0') {
            solved = false;
            break;
         } else {
            solved = true;
         }
      }

      /******************************************************************
       ** This is the implementation of the adapted solving algorithm. **/
      int i, j;

      for (i = 0; i < 81; i++) {
         if (unsolvedArray[i] != '0') {
            continue;
         }

         Map<String, String> h = new HashMap<String, String>();
         for (j = 0; j < 81; j++) {
            h.put(j / 9 == i / 9 || j % 9 == i % 9 || (j / 27 == i / 27)
                    && ((j % 9 / 3) == (i % 9 / 3))
                    ? "" + unsolvedArray[j] : "0", "1");
         }
         for (j = 1; j <= 9; j++) {
            if (h.get("" + j) == null) {
               unsolvedArray[i] = (char) ('0' + j);
               if (solved) {
                  return;
               } else {
                  Solve();
               }
            }
         }
         unsolvedArray[i] = '0';
         return;
      }
      /******************************************************************/
      // Creates an integer array from the char array.
      for (i = 0; i < 81; i++) {
         solvedArray[i] = Integer.parseInt(unsolvedArray[i] + "");
      }
   }

   /**
    * Sets the indexes of all non-zero numbers on the grid.
    *
    * @param input the array who's values are to be check for non-zero numbers.
    */
   private void setNonZeros(int[] input) {
      nonZeros.clear();
      for (int i = 0; i < 81; i++) {
         if (input[i] != 0) {
            nonZeros.add(i);
         }
      }
   }

   /**
    * Returns the array containing the 'solved' values.
    * @return - <em>solvedArray</em> - the array containing the 'solved' values.
    */
   public int[] getSolvedArray() {
      return solvedArray;
   }
}
