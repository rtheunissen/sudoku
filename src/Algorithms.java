/* File: Algorithms.java  -  April 2011 */
package sudoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Used for grid and square computations, as well as solver validity checks.
 * 
 * @author Rudi Theunissen
 */
public class Algorithms {

   /** Contains the pixel location values of the grid-lines. */
   public static ArrayList<Integer> gapLocations;
   /** Contains the pixel location values of a row or column's upper bounds. */
   public static ArrayList<Integer> upperBounds;
   /** Contains keys for the number and keypad-number keys, and their values. */
   public static Map<Integer, Integer> numberKeyMap;

   public Algorithms() {
      upperBounds = new ArrayList<Integer>();
      gapLocations = new ArrayList<Integer>();
      numberKeyMap = new HashMap<Integer, Integer>();
      setLocationRanges();
      setNumberKeys();
   }

   /**
    * Sets pixel ranges of squares on the grid.
    *
    * This is useful for when the player clicks on a line, or when a calculation
    * has to be made to determine a particular square given a pixel location.
    * Each square is 44x44, and the grid-lines are 1px and 2px wide.
    * <br>
    * <strong>These methods use the location ranges: </strong><ul>
    * <li> {@link Algorithms#getSquare(int) getSquare} </li>
    * <li> {@link Algorithms#getBase(int) getBase} </li></ul>
    */
   private void setLocationRanges() {
      upperBounds.add(0);
      gapLocations.add(0);

      for (int i = 1; i < 10; i++) {
         upperBounds.add((i == 4 || i == 7)
                 ? (upperBounds.get(i - 1) + 46)
                 : (upperBounds.get(i - 1) + 45));
      }
      for (int i = 1; i < 14; i++) {
         gapLocations.add((i == 1 || i == 5 || i == 9 || i == 13)
                 ? (gapLocations.get(i - 1) + 1)
                 : (gapLocations.get(i - 1) + 45));
      }
   }

   /**
    * Sets the keycodes that correspond to numbers and keypad-numbers.
    */
   private void setNumberKeys() {

      // This maps two key values with one number value.
      for (int i = 48; i < 58; i++) {
         numberKeyMap.put(i, i - 48);
         numberKeyMap.put(i + 48, i - 48);
      }
   }

   /**
    * Used to convert a pixel location on the grid to a particular square number
    * on the grid, for either x or y as the grid is a square.
    *
    * @param p the single axis pixel location.
    * @return the particular square number (1 to 9).
    */
   public static int getSquare(int p) {
      if (!gapLocations.contains(p)) {
         for (int i = 0; i < 10; i++) {
            if (p < upperBounds.get(i)) {
               return i;
            }
         }
      }
      return 0;
   }

   /**
    * Returns the location of the left or top of a square on the grid.
    *
    * @param n square location (1 to 9).
    * @return exact pixel location of either top or left of square.
    */
   public static int getBase(int n) {
      return gapLocations.get((int) (n * 1.3)) + 1;
   }

   /**
    * Converts a row and column value into a specific array index.
    *
    * @param row the row location.
    * @param col the column location.
    * @return the location of the row and column as index (0 to 80).
    */
   public static int toIndex(int row, int col) {
      return ((row - 1) * 9 + (col - 1));
   }

   /**
    * Converts an index into a row value.
    *
    * @param index the array index.
    * @return the index as a row value.
    */
   public static int getRow(int index) {
      return ((index) / 9) + 1;
   }

   /**
    * Converts an index into a column value.
    *
    * @param index the array index.
    * @return the index as a column value.
    */
   public static int getCol(int index) {
      return ((index) % 9) + 1;
   }

   /**
    * Returns the array index of a 3 by 3 section's upper left square.
    *
    * @param index the array index of a square in a 3 by 3 section.
    * @return the upper left corner square in a section's index.
    */
   public static int getUpperLeftIndex(int index) {
      int row = (getRow(index) - 1) / 3;
      int col = (getCol(index) - 1) / 3;
      return (row * 30) - (row * 3) + (col * 3);
   }

   /**
    * Calculates the x-axis pixel location of a possibility's image.
    *
    * @param num the value of the possibility.
    * @param squarePositionX the x-axis square value.
    * @return the calculated x-axis pixel location of a possibility's image.
    */
   public static int getMiniSquareLocationX(int num, int squarePositionX) {
      return ((num - 1) % 3) * 15 + squarePositionX;
   }

   /**
    * Calculates the y-axis pixel location of a possibility's image.
    *
    * @param num the value of the possibility.
    * @param squarePositionY the y-axis square value.
    * @return the calculated y-axis pixel location of a possibility's image.
    */
   public static int getMiniSquareLocationY(int num, int squarePositionY) {
      return ((num - 1) / 3) * 15 + squarePositionY;
   }

   /**
    * Determines whether a solve request was valid.
    * <br><br>
    * Checks for the following:<ul>
    * <li>If there are <strong>at least 17 provided clues.</strong><br>
    *        <em>However, if there are less than 6,
    *            it skips the band and stack check.</em></li>
    * <li>If there are <strong>two empty rows in a band</strong>,
    *     or <strong>two empty columns in a stack.</strong><br>
    *        <em>A band is made up of three adjacent 3 by 3 sections.</em><br>
    *        <em>A stack is made up of three stacked 3 by 3 sections.</em></li>
    * <li>If there are 
    *     <strong>not any of the same numbers in a row or column.</strong></li>
    * <li>If there are 
    *     <strong>not any of the same numbers in a 3 by 3 section.</strong></li>
    *
    * @param input an array that contains all the numbers that were entered.
    * @param noZeros a list of array indexes
    *                of all the non-zero numbers that were entered.
    * @return true if all conditions have been met for a puzzle to be valid.
    */
   public static boolean isValidSolve(int[] input, ArrayList<Integer> noZeros) {
      if (!(noZeros.size() < 6 || noZeros.size() > 16)) {
         return false;
      }

      /**** BAND AND STACK CHECK ****/
      /* Checks if the sums of rows 1 and 2, 2 and 3, 1 and 3 are zero. */
      if (noZeros.size() > 6) {
         int bandPairSum = 0;
         int stackPairSum = 0;
         int bandSplitSum = 0;
         int stackSplitSum = 0;

         for (int q = 0; q < 3; q++) {
            for (int j = 0; j < 2; j++) {
               for (int k = 0; k < 2; k++) {
                  for (int i = 1; i < 10; i++) {
                     bandPairSum += input[(i + q * 27 + 9 * j + 9 * k) - 1];
                     stackPairSum += input[(i - 1) * 9 + q * 3 + k + j];
                  }
               }
               if (bandPairSum == 0 || stackPairSum == 0) {
                  return false;
               }
               bandPairSum = 0;
               stackPairSum = 0;
            }
            for (int i = 0; i < 9; i++) {
               bandSplitSum += input[i + q * 27] + input[i + q * 27 + 18];
               stackSplitSum += input[i * 9 + q * 3] + input[i * 9 + q * 3 + 2];
            }
            if (bandSplitSum == 0 || stackSplitSum == 0) {
               return false;
            }
            bandSplitSum = 0;
            stackSplitSum = 0;
         }
      }

      /**** SAME NUMBERS IN ROW OR COLUMN ****/
      /* For every index that has a number, check values of its row and col. */
      for (int n : noZeros) {
         int row = Algorithms.getRow(n);
         int col = Algorithms.getCol(n);

         // Looks for the same number in the same row, columns to the right..
         for (int c = col + 1; c < 10; c++) {
            if (input[Algorithms.toIndex(row, c)] == input[n]) {
               return false;
            }
         }
         // Looks for the same number in the same row, columns to the left..
         for (int c = col - 1; c > 0; c--) {
            if (input[Algorithms.toIndex(row, c)] == input[n]) {
               return false;
            }
         }
         // Looks for the same number in the same column, rows below..
         for (int r = row + 1; r < 10; r++) {
            if (input[Algorithms.toIndex(r, col)] == input[n]) {
               return false;
            }
         }
         // Looks for the same number in the same column, rows above..
         for (int r = row - 1; r > 0; r--) {
            if (input[Algorithms.toIndex(r, col)] == input[n]) {
               return false;
            }
         }
         // Looks for the same number in the 3 by 3 section..
         int upperLeft = Algorithms.getUpperLeftIndex(n);
         for (int j = 0; j < 27; j += 9) {
            for (int i = upperLeft + j; i < upperLeft + j + 3; i++) {
               if (i != n && input[i] == input[n]) {
                  return false;
               }
            }
         }
      }
      // all conditions met, solve is valid.
      return true;
   }

   /**
    * Accesses the <em>ArrayList</em> that contains the line pixel values.
    *
    * @return The <em>ArrayList</em> that contains the line pixel values.
    */
   public static ArrayList<Integer> getGaps() {
      return gapLocations;
   }

   /**
    * Accesses the <em>ArrayList</em> that contains the square pixel values.
    *
    * @return The <em>ArrayList</em> that contains the square pixel values.
    */
   public static ArrayList<Integer> getUpperBounds() {
      return upperBounds;
   }

   public static Map<Integer, Integer> getNumberKeys() {
      return numberKeyMap;
   }
}
