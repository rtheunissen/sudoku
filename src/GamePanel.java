/* File: GamePanel.java  -  April 2011 */
package sudoku;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import javax.swing.*;

/**
 * Works with {@link GameNumbers} to create the game interface.
 *
 * @author Rudi Theunissen
 */
public class GamePanel extends JPanel implements ActionListener {

   /** Instance of {@link Images}, used to load images. */
   private Images images;
   /** Instance of {@link GameNumbers}, the main utility class for the game. */
   private GameNumbers numbers;
   /** A square position along the x-axis; column. */
   private int squareX;
   /** A square position along the y-axis; row. */
   private int squareY;
   /** The current difficulty. */
   public static int currentDifficulty;
   /** True is a game is in progress. */
   private static boolean gameState;
   /** True if the help-exceeded notification dialog should be painted. */
   private static boolean showHelpDialog;
   /** True if the end-game notification dialog should be painted. */
   private static boolean showFinishedDialog;
   /** True if validation is enabled. */
   private boolean validateState;
   /** Contains all the buttons in the panel. */
   private JButton[] buttons;
   /** Amount of extra hints that have been requested. */
   private int helpCount;

   /**
    * Defines the initial values of a few data fields and sets up the buttons.
    * @see Button
    */
   public GamePanel() {
      this.setFocusable(true);
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      this.setPreferredSize(new Dimension(511, 410));
      this.setMaximumSize(new Dimension(511, 410));
      this.setBackground(Color.WHITE);
      this.setFocusTraversalKeysEnabled(false);
      this.addKeyListener(new KeyboardAdapter());
      this.addMouseListener(new MouseClickAdapter());

      numbers = new GameNumbers();
      images = new Images(true);
      squareX = 5;
      squareY = 5;
      currentDifficulty = 1;

      setButtons();
   }

   /**
    * Creates the buttons and sets them up in the panel.
    *
    * For each {@link Button}; adds an ActionListener, sets the alignment of the
    * button, and adds it to the class.
    */
   private void setButtons() {
      // create the buttons..
      Button newGameButton = new Button("newGame", "new", 100, 91);
      Button diffSwitch = new Button("1-diff", "diff", 100, 46);
      Button helpButton = new Button("help", "help", 100, 45);
      Button solveButton = new Button("solve", "solve", 100, 45);
      Button validateButton = new Button("validate", "validate", 100, 46);
      Button clearMinisButton = new Button("clearminis", "clear", 100, 45);
      Button restartButton = new Button("restart", "restart", 100, 45);
      Button exitButton = new Button("exitGame", "exit", 100, 47);

      buttons = new JButton[8];
      buttons[0] = newGameButton;
      buttons[1] = diffSwitch;
      buttons[2] = helpButton;
      buttons[3] = solveButton;
      buttons[4] = validateButton;
      buttons[5] = clearMinisButton;
      buttons[6] = restartButton;
      buttons[7] = exitButton;

      for (JButton button : buttons) {
         button.addActionListener(this);
         button.setAlignmentX(JButton.RIGHT_ALIGNMENT);
         this.add(button);
      }
   }

   /**
    * Creates a new game.
    */
   public void newGame() {
      numbers.newGame();
      squareX = 5;
      squareY = 5;
      gameState = true;
      validateState = false;
      showFinishedDialog = false;
      showHelpDialog = false;
      helpCount = 0;
      buttons[2].setEnabled(true);
   }

   /**
    * Cycles the current difficulty and sets the icon of the difficulty button.
    */
   private void switchDifficulty() {
      currentDifficulty += (currentDifficulty != 3) ? 1 : -3;
      ImageIcon diff = images.getImageIcon(currentDifficulty + "-diff-button");
      buttons[1].setIcon(diff);
   }

   /**
    * Solves a single random, empty square on the grid - maximum of 5 requests.
    *
    * Works with {@link GameNumbers#solveNumber()}
    */
   public void help() {
      if (!gameState) {
         return;
      }
      helpCount++;
      if (helpCount < 6) {
         numbers.solveNumber();
         if (helpCount == 5) {
            buttons[2].setEnabled(false);
         }
      } else {
         showHelpDialog = true;
      }
   }

   /**
    * Fills the grid with the solution values.
    *
    * Works with {@link GameNumbers#solveAll()}
    */
   public void solveGame() {
      numbers.solveAll();
      validateState = false;
      gameState = false;
   }

   /**
    * Clears all entered possibilities.
    *
    * Works with {@link GameNumbers#clearMinis()}
    */
   public void clearMinis() {
      if (gameState && JOptionPane.showConfirmDialog(
              this, "This will clear all the possibilities.\n"
              + "Are you sure you want to do this?\n",
              "Clear all possibilities?",
              JOptionPane.YES_NO_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              (Icon) images.getImageIcon("clear-confirm")) == 0) {
         numbers.clearMinis();
      }
   }

   /**
    * Restarts the current game.
    */
   public void restartGame() {
      if (numbers.getNonZeros() > 0) {
         numbers.restartGame();
         squareX = 5;
         squareY = 5;
         validateState = false;
         buttons[2].setEnabled(true);
         gameState = true;
         helpCount = 0;
      }
   }

   /**
    * Indicates the end of a game by showing a notification dialog image.
    */
   public static void endGame() {
      gameState = false;
      showFinishedDialog = true;
      showHelpDialog = false;
   }

   /**
    * Adds a number to the current game's grid.
    *
    * @param numberToAdd value to be added to the current game's grid.
    */
   public void inputNumber(int numberToAdd) {
      if (gameState) {
         numbers.addNumber(numberToAdd, squareY, squareX);
      }
   }

    private void toggleMini(int number) {
        numbers.toggleMini(number, squareY, squareX);
    }

   /**
    * Paints all the components and images onto the panel.
    *
    * Overrides - {@link JComponent#paintComponent(java.awt.Graphics)}
    *
    * @param g Graphics parameter.
    */
   @Override
   public void paintComponent(Graphics g) {
      requestFocusInWindow();
      super.paintComponent(g);

      // paint the grid.
      g.drawImage(images.getImage("sudoku-grid"), 0, 0, this);

      int number, x, y, row, col;
      for (int i = 0; i < 81; i++) {

         row = Algorithms.getRow(i);
         col = Algorithms.getCol(i);
         x = Algorithms.getBase(col);
         y = Algorithms.getBase(row);
         number = numbers.getCurrentNumber(i);

         if (number == 0) {
            // paint the possibilities..
            for (int mini : numbers.getMiniNumbers(i)) {
               if (mini != 0) {
                  int c = Algorithms.getMiniSquareLocationX(mini, x);
                  int r = Algorithms.getMiniSquareLocationY(mini, y);
                  g.drawImage(images.getNumberImage("mini", mini), c, r, this);
               }
            }
            // if the number is zero, no image is to be painted, so skip.
            continue;
         }

         // if validation is not enabled..
         if (!validateState) {
            g.drawImage(images.getNumberImage("white", number), x, y, this);

            // otherwise if validation is enabled, and the number is correct..
         } else if (numbers.validate(i)) {
            g.drawImage(images.getNumberImage("green", number), x, y, this);

            // otherwise if the numbers is incorrect.
         } else {
            g.drawImage(images.getNumberImage("red", number), x, y, this);
         }
      }

      // paint the selector square if the game is in progress.
      if (gameState) {
         int xLoc = Algorithms.getBase(squareX);
         int yLoc = Algorithms.getBase(squareY);
         g.drawImage(images.getImage("selector"), xLoc, yLoc, null);
      }

      // paints the appropriate dialog notification images.
      if (showHelpDialog) {
         g.drawImage(images.getImage("help-exceeded"), 47, 183, this);
      } else if (showFinishedDialog) {
         g.drawImage(images.getImage("game-finished"), 54, 154, this);
      }
   }

   /**
    * Uses the button that was pressed to call the corresponding method.
    *
    * @param e ActionEvent parameter.
    */
   public void actionPerformed(ActionEvent e) {
      showHelpDialog = false;
      String command = e.getActionCommand();

      if ("new".equals(command)) {
         newGame();
      } else if ("diff".equals(command)) {
         switchDifficulty();
      } else if ("restart".equals(command)) {
         restartGame();
      } else if ("help".equals(command)) {
         help();
      } else if ("clear".equals(command)) {
         clearMinis();
      } else if ("solve".equals(command)) {
         solveGame();
      } else if ("validate".equals(command)) {
         validateState = !validateState;
      } else if ("exit".equals(command)) {
         Sudoku.setCard("Card with Menu");
      }
      repaint();
   }

   /**
    * Listens for keyboard events.
    */
   private class KeyboardAdapter extends KeyAdapter {

      /**
       * Fires whenever a key on the keyboard is pressed.
       *
       * Using the keycode of the key that was pressed, first determines if the
       * key was a number or numpad-number key, and if so adds the number to the
       * grid.
       *
       * If not, it uses the keycode to determine which method should be called.
       *
       * @param e KeyEvent parameter.
       */
      @Override
      public void keyPressed(KeyEvent e) {
          if (showFinishedDialog || showHelpDialog) {
            showFinishedDialog = false;
            showHelpDialog = false;
            repaint();
            return;
          }

          int key = e.getKeyCode();
          Map<Integer, Integer> numberKeys = Algorithms.getNumberKeys();

          // if the key that was pressed was a number..
          if (numberKeys.containsKey(key)) {
            if (e.isControlDown() || e.isMetaDown() || e.isAltDown()) {
               toggleMini(numberKeys.get(key));
            } else {
               inputNumber(numberKeys.get(key));
            }
            repaint();
            return;
         }

         if (key == KeyEvent.VK_SPACE) {
            newGame();
         } else if (key == KeyEvent.VK_R) {
            restartGame();
         } else if (key == KeyEvent.VK_H) {
            help();
         } else if (key == KeyEvent.VK_D) {
            switchDifficulty();
         } else if (key == KeyEvent.VK_S) {
            solveGame();
         } else if (key == KeyEvent.VK_C || key == KeyEvent.VK_ENTER) {
            validateState = !validateState;
         } else if (key == KeyEvent.VK_ESCAPE) {
            Sudoku.setCard("Card with Menu");
         } else if (key == KeyEvent.VK_UP && (squareY > 0 && squareY < 11)) {
            squareY += (squareY == 1) ? 8 : -1;
         } else if (key == KeyEvent.VK_DOWN && (squareY > 0 && squareY < 10)) {
            squareY += (squareY == 9) ? -8 : 1;
         } else if (key == KeyEvent.VK_LEFT && (squareX > 0 && squareX < 11)) {
            squareX += (squareX == 1) ? 8 : -1;
         } else if (key == KeyEvent.VK_RIGHT && (squareX > 0 && squareX < 10)) {
            squareX += (squareX == 9) ? -8 : 1;
         } else if (key == KeyEvent.VK_DELETE || key == KeyEvent.VK_BACK_SPACE) {
            if (e.isControlDown() || e.isAltDown() || e.isMetaDown()) {
               clearMinis();
            } else {
               inputNumber(0);
            }
         }
         repaint();
      }
   }

   /**
    * Listens for mouse events.
    */
   private class MouseClickAdapter extends MouseAdapter {

      /**
       * Fires when the mouse is clicked.
       *
       * If the user clicked on a valid square,
       * the current square data fields are updated.
       *
       * @param e MouseEvent
       */
      @Override
      public void mouseClicked(MouseEvent e) {
         if (showFinishedDialog || showHelpDialog) {
            showFinishedDialog = false;
            showHelpDialog = false;
            repaint();
            return;
         }
         if (gameState) {
            int x = Algorithms.getSquare(e.getX());
            int y = Algorithms.getSquare(e.getY());

            if (x != 0 && y != 0) {
               squareX = x;
               squareY = y;
            }
            repaint();
         }
      }
   }
}
