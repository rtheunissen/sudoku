/* File: GameNumbers.java  -  April 2011 */
package sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * Creates the number infrastructure used by the game.
 *
 * Involves reading int tokens from the puzzle and solution .sud (txt) files and
 * handles all the operations that are to be performed on the number arrays.
 *
 * @author Rudi Theunissen
 */
public final class GameNumbers {

   /** A specific, random number line in the puzzle/solution text files. */
   private int newLineNumber;
   /** The amount of non-zero numbers that are currently on the grid. */
   private int nonZeros;
   /** Random number generator. */
   private Random random;
   /** The current puzzle number line. */
   private String currentLine;
   /** The current solution number line. */
   private String solutionLine;
   /** True if the a game is in progress. */
   private boolean gameState;
   /** True if a restart if allowed. */
   private boolean startOverAllow;
   /** True if there are possibilities on the grid. */
   // private boolean thereAreMinis;
   /** The array of integers that are currently in play. */
   private int[] gameArray;
   /** A snapshot array of the puzzle that was generated at the start. */
   private int[] initialArray;
   /** The array of integers that contains the solution of the puzzle array. */
   private int[] solutionArray;
   /** A Scanner that reads the lines in the puzzles text file. */
   private Scanner puzzleScan;
   /* A Scanner that reads the lines in the solutions text file. */
   private Scanner solutionScan;
   /** An ArrayList that contains all the lines of the puzzles text file. */
   private ArrayList<String> puzzleLinesArray;
   /** An ArrayList that contains all the lines of the solutions text file. */
   private ArrayList<String> solutionLinesArray;
   /** A nested ArrayList containing all the entered possibilities. */
   private ArrayList<ArrayList<Integer>> minisArray;

   /**
    * Default Constructor - creates the arrays and the number lines.
    */
   public GameNumbers() {
      createArrays();
      createLines();
   }

   /**
    * Initialises all field arrays.
    */
   private void createArrays() {

      gameArray = new int[81];
      initialArray = new int[81];
      solutionArray = new int[81];

      puzzleLinesArray = new ArrayList<String>();
      solutionLinesArray = new ArrayList<String>();
      minisArray = new ArrayList<ArrayList<Integer>>();

      for (int i = 0; i < 81; i++) {
         minisArray.add(new ArrayList<Integer>());
         for (int l = 0; l < 10; l++) {
            minisArray.get(i).add(0);
         }
      }
   }

   /**
    * Adds a possibility.
    *
    * @param numberEntered the possibility to be added.
    * @param row the selected row on the grid.
    * @param col the selected column on the grid.
    */
   public void addMiniNumber(int numberEntered, int row, int col) {
      int arrayIndex = Algorithms.toIndex(row, col);
      if (!minisArray.get(arrayIndex).contains(numberEntered)) {
         minisArray.get(arrayIndex).set(numberEntered, numberEntered);
      }
   }

   /**
    * Creates a new Sudoku puzzle.
    */
   public void newGame() {
      setValues();
      clearMinis();
      gameState = true;
   }

   /**
    * Resets the current game.
    *
    * If a game is in progress or if a reset is allowed, this method resets the
    * values of the numbers on the grid back to their initial states.
    */
   public void restartGame() {
      if (gameState || startOverAllow) {
         System.arraycopy(initialArray, 0, gameArray, 0, 81);
         nonZeros = 0;
         for (int n : initialArray) {
            nonZeros += (n != 0) ? 1 : 0;
         }
         clearMinis();
         gameState = true;
      }
   }

   /**
    * Clears all the possibilities on the grid.
    */
   public void clearMinis() {
      for (int l = 0; l < 81; l++) {
         for (int p = 0; p < 10; p++) {
            minisArray.get(l).set(p, 0);
         }
      }
   }

   /**
    *
    *
    * @param number the possibility to be deleted.
    * @param row the selected row on the grid.
    * @param col the selected column on the grid.
    */
   public void toggleMini(int number, int row, int col) {
      ArrayList<Integer> minis = minisArray.get(Algorithms.toIndex(row, col));
      System.out.println(minis);
      minis.set(number, minis.get(number) == 0 ? number : 0);
   }

   /**
    * Solves one random square.
    *
    * Called when the "Help" button or "H" key is pressed.<br>
    * If the grid isn't full it solves one random empty square on the grid.
    */
   public void solveNumber() {
      if (gameState) {
         int randomIndex;
         do {
            randomIndex = random.nextInt(80);
         } while (gameArray[randomIndex] != 0);
         gameArray[randomIndex] = solutionArray[randomIndex];
      }
   }

   /**
    * Makes the current grid's puzzle values equal the current solution values.
    *
    * Copies each value from the array of solution values to the array of puzzle
    * values. Also ends the current game but allows it to be restart again.
    */
   public void solveAll() {
      System.arraycopy(solutionArray, 0, gameArray, 0, gameArray.length);
      gameState = false;
      startOverAllow = true;
   }

   /**
    * Checks to see whether a specific value in the current puzzle is correct.
    *
    * @param i the number in the current game array to be checked.
    * @return true if the value is correct.
    */
   public boolean validate(int i) {
      return (gameArray[i] == solutionArray[i]);
   }

   /**
    * Sets the values of the current game's puzzle and solution array.
    *
    * Sets each value in the grid values array to the corresponding value in the
    * puzzle array (initial hints). Does the same for the solution array.
    */
   private void setValues() {
      setCurrentLine();
      Scanner current = new Scanner(currentLine);
      Scanner solution = new Scanner(solutionLine);

      // Only reads single digit integers.
      current.useDelimiter("");
      solution.useDelimiter("");

      nonZeros = 0;
      for (int i = 0; i < 81; i++) {
         gameArray[i] = current.nextInt();
         solutionArray[i] = solution.nextInt();
         if (gameArray[i] != 0) {
            nonZeros++;
         }
      }
      System.arraycopy(gameArray, 0, initialArray, 0, 81);
   }

   /**
    * Creates puzzle/solution line pair.
    *
    * Determines the puzzle/solution pair for a new game by randomly choosing a
    * line from within the current difficulty's line array. Also makes sure that
    * the line that is chosen is not already the current line (current game).
    */
   private void setCurrentLine() {
      random = new Random();

      int numberOfPuzzles = Sudoku.NUMBER_OF_PUZZLES_PER_DIFFICULTY;
      int difficulty = GamePanel.currentDifficulty;
      int lineStart = difficulty * (numberOfPuzzles) + (difficulty + 1) * 3 + 5;
      int oldLineNumber = newLineNumber;

      do {
         newLineNumber = random.nextInt(numberOfPuzzles) + lineStart;
      } while (newLineNumber == oldLineNumber);
      // makes sure that the new random line is not the same as the old one.

      currentLine = puzzleLinesArray.get(newLineNumber - 1);
      solutionLine = solutionLinesArray.get(newLineNumber - 1);
   }

   /**
    * Scans the entire puzzle and solution files' lines into arrays.
    */
   private void createLines() {
      puzzleScan = getPuzzleScanner("puzzles.sud");
      solutionScan = getPuzzleScanner("solutions.sud");

      while (puzzleScan.hasNextLine()) {
         puzzleLinesArray.add(puzzleScan.nextLine());
         solutionLinesArray.add(solutionScan.nextLine());
      }
   }

   /**
    * Adds a specific number to the current grid values array.
    *
    * Determines which position in the specified array needs to be updated with
    * an input number based on the column and row location specified.
    *
    * @param numberEntered the number that is being added.
    * @param col selected column on the grid.
    * @param row selected row on the grid.
    */
   public void addNumber(int numberEntered, int row, int col) {
      int arrayIndex = Algorithms.toIndex(row, col);

      if (gameArray[arrayIndex] == numberEntered) {
         numberEntered = 0;
      }

      /* if the number that was entered is zero and the square was empty, or
       * if the value to be updated was an initial hint at the start, return. */
      if ((numberEntered == 0 && gameArray[arrayIndex] == 0)
              || initialArray[arrayIndex] != 0) {
         return;
      }

      // keep track of non-zero numbers that are on the grid..
      nonZeros += (numberEntered == 0)
              ? ((gameArray[arrayIndex] == 0) ? 0 : -1)
              : ((gameArray[arrayIndex] == 0) ? 1 : 0);

      // update the current game array at the given index.
      gameArray[arrayIndex] = numberEntered;

      // check to see if the game is finished..
      if (nonZeros == 81 && Arrays.equals(gameArray, solutionArray)) {
         gameState = false;
         startOverAllow = true;
         GamePanel.endGame();
      }
   }

   /**
    * Returns the value of the game array at a given index.
    *
    * @param index given array index.
    * @return the value of the game array at a given index.
    */
   public int getCurrentNumber(int index) {
      return gameArray[index];
   }

   /**
    * Used to set up a Scanner that uses a file as an input stream.
    *
    * @param fileName Location of the file that is to be scanned.
    * @return Scanner that is ready to read data from a file.
    */
   private Scanner getPuzzleScanner(String fileName) {
      String puzzleDir = Sudoku.PUZZLE_DIRECTORY;
      return new Scanner(getClass().getResourceAsStream(puzzleDir + fileName));
   }

   /**
    * Returns an ArrayList of possibilities (mini numbers) at a given index.
    *
    * @param index given array index.
    * @return an ArrayList of possibilities (mini numbers) at a given index.
    */
   public ArrayList<Integer> getMiniNumbers(int index) {
      return minisArray.get(index);
   }

   /**
    * Returns the amount of non-zero numbers that are currently on the grid.
    *
    * @return the amount of non-zero numbers that are currently on the grid.
    */
   public int getNonZeros() {
      return nonZeros;
   }
}
