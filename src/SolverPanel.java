/* File: SolverPanel.java  -  April 2011 */
package sudoku;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Creates an interface which solves a grid of values.
 *
 * Allows a user to enter values onto a blank grid, and displays the solved grid
 * when the 'solve' command is passed from either the mouse or the keyboard.
 *
 * @author Rudi Theunissen
 */
public class SolverPanel extends JPanel implements ActionListener {

   /** A square position along the x-axis; column. */
   private int squareX;
    /** A square position along the y-axis; row. */
   private int squareY;
   /** Indicates the state of the solver. */
   private JProgressBar processNotifier;
   /** Instance of {@link SolverNumbers}, the main utility class. */
   private SolverNumbers numbers;
   /** Instance of {@link Images}, used to load images. */
   private Images images;
   /** True if numbers are currently able to be entered. */
   private boolean inputNumberState;
   /** True if the grid was solved. */
   private boolean solvedState;
   /** True if the invalid-solve-request notification dialog should be shown. */
   private boolean showInvalidDialog;

   /**
    * Default Constructor - sets initial values for data fields, creates all the
    * swing components and defines the layout of the panel.
    */
   public SolverPanel() {
      this.setPreferredSize(new Dimension(540, 410));
      this.setMaximumSize(new Dimension(540, 410));
      this.setFocusTraversalKeysEnabled(false);
      this.setFocusable(true);
      this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
      this.setBackground(Color.WHITE);
      this.addKeyListener(new KeyboardAdapter());
      this.addMouseListener(new MouseClickAdapter());

      images = new Images(true);
      numbers = new SolverNumbers();
      squareX = 5;
      squareY = 5;
      inputNumberState = true;

      processNotifier = new JProgressBar();
      processNotifier.setString("Idle");
      processNotifier.setPreferredSize(new Dimension(100, 45));
      processNotifier.setMaximumSize(new Dimension(100, 45));
      processNotifier.setStringPainted(true);

      setComponents();
   }

   /**
    * Creates the buttons, adds listeners to the components and sets the layouts
    * and alignments.
    */
   private void setComponents() {
      Button newSolverButton = new Button("newSolver", "new", 100, 91);
      Button solveButton = new Button("solver-solve", "solve", 100, 227);
      Button exitButton = new Button("exitGame", "exit", 100, 47);

      JComponent[] components = new JComponent[4];
      components[0] = newSolverButton;
      components[1] = solveButton;
      components[2] = processNotifier;
      components[3] = exitButton;

      newSolverButton.addActionListener(this);
      solveButton.addActionListener(this);
      exitButton.addActionListener(this);

      for (JComponent component : components) {
         component.setAlignmentX(Component.RIGHT_ALIGNMENT);
         add(component);
      }
   }

   /**
    * Uses the button that was pressed to call the corresponding utility method.
    *
    * @param e ActionEvent parameter.
    */
   public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();

      if ("new".equals(command)) {
         newSolver();
      } else if ("solve".equals(command)) {
         Solve();
      } else if ("exit".equals(command)) {
         Sudoku.setCard("Card with Menu");
      }
      repaint();
   }

   /**
    * Creates a new, blank grid.
    */
   public void newSolver() {
      squareX = 5;
      squareY = 5;
      numbers.newSolver();
      solvedState = false;
      showInvalidDialog = false;
      processNotifier.setString("Blank");
   }

   /**
    * Runs the solving algorithm in the {@link Solver}, using a call to
    * {@link Solver#solveArray(int[])}.
    */
   public void Solve() {
      solvedState = false;
      showInvalidDialog = false;
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      // attempts to solve the array in a seperate thread..
      SwingWorker<Void, Void> solveThread = new SwingWorker<Void, Void>() {
         @Override
         public Void doInBackground() {
            showInvalidDialog = !numbers.attemptSolve();
            if (!showInvalidDialog) {
               processNotifier.setIndeterminate(true);
               processNotifier.setString("Solving...");
               try {
                  Thread.sleep(500);
               } catch (InterruptedException ex) {
                  System.err.println("Thread was interrupted.");
               }
            }
            return null;
         }
         // after the solver has executed..
         @Override
         protected void done() {
            if (!showInvalidDialog) {
               processNotifier.setString("Solved");
            } else {
               processNotifier.setString("Invalid");
            }
            setCursor(Cursor.getDefaultCursor());
            processNotifier.setIndeterminate(false);
            inputNumberState = false;
            solvedState = true;
            squareX = 5;
            squareY = 5;
            repaint();
         }
      };
      solveThread.execute();
   }

   /**
    * Paints the current state of the panel.
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

         if (!solvedState || inputNumberState || showInvalidDialog) {
            number = numbers.getSolvingArray()[i];
         } else {
            number = numbers.getSolvedArray()[i];
         }

         // if the number is zero, skip to the next one.
         if (number == 0) {
            continue;
         }
         row = Algorithms.getRow(i);
         col = Algorithms.getCol(i);
         x = Algorithms.getBase(col);
         y = Algorithms.getBase(row);

         // paint the number.
         g.drawImage(images.getNumberImage("white", number), x, y, this);
      }

      int xLoc = Algorithms.getBase(squareX);
      int yLoc = Algorithms.getBase(squareY);

      // paint selector square
      g.drawImage(images.getImage("selector"), xLoc, yLoc, this);

      if (showInvalidDialog) {
         g.drawImage(images.getImage("invalid-solve"), 47, 47, this);
      }
   }

   /**
    * Adds an input number to the solving array / grid.
    *
    * @param numberToAdd Value to be added to the solving array of integers.
    */
   public void inputNumber(int numberToAdd) {
      numbers.addNumber(numberToAdd, squareX, squareY);
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
      public void keyPressed(KeyEvent e) {

         int key = e.getKeyCode();
         inputNumberState = true;
         processNotifier.setString("Standby");

         if (showInvalidDialog) {
            showInvalidDialog = false;
            repaint();
            return;
         }

         if (Algorithms.getNumberKeys().containsKey(key)) {
            inputNumber(Algorithms.getNumberKeys().get(key));
            repaint();
            return;
         }

         if (key == KeyEvent.VK_ESCAPE) {
            Sudoku.setCard("Card with Menu");
         } else if (key == KeyEvent.VK_UP && (squareY > 0 && squareY < 11)) {
            squareY += (squareY == 1) ? 8 : -1;
         } else if (key == KeyEvent.VK_DOWN && (squareY > 0 && squareY < 10)) {
            squareY += (squareY == 9) ? -8 : 1;
         } else if (key == KeyEvent.VK_LEFT && (squareX > 0 && squareX < 11)) {
            squareX += (squareX == 1) ? 8 : -1;
         } else if (key == KeyEvent.VK_RIGHT && (squareX > 0 && squareX < 10)) {
            squareX += (squareX == 9) ? -8 : 1;
         } else if (key == KeyEvent.VK_S || key == KeyEvent.VK_ENTER) {
            Solve();
         } else if (key == KeyEvent.VK_SPACE) {
            newSolver();
         } else if (key == KeyEvent.VK_DELETE) {
            inputNumber(0);
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
         if (showInvalidDialog) {
            showInvalidDialog = false;
         }
         int x = Algorithms.getSquare(e.getX());
         int y = Algorithms.getSquare(e.getY());

         if (x != 0 && y != 0) {
            squareX = x;
            squareY = y;
            inputNumberState = true;
            processNotifier.setString("Standby");
         }
         repaint();
      }
   }
}
