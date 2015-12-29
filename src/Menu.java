/* File: Menu.java  -  April 2011 */
package sudoku;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.JPanel;

/**
 * Creates a menu with 4 options: Game, Solver, Instructions and Exit.
 *
 * @author Rudi Theunissen
 */
public class Menu extends JPanel implements ActionListener {

   /** An array that contains references to all the buttons in the menu. */
   private Button[] buttons;

   /**
    * Default constructor that creates buttons and sets GUI properties.
    */
   public Menu() {
      this.setPreferredSize(new Dimension(541, 410));
      this.setMaximumSize(new Dimension(541, 410));
      this.setLayout(new GridLayout(2, 2));
      this.addKeyListener(new KeyboardAdapter());

      setButtons();
   }

   /**
    * Creates the buttons, adds listeners to the buttons
    * and adds them to the panel.
    */
   private void setButtons() {
      Button gameButton = new Button("game", "game", 271, 205);
      Button solverButton = new Button("solver", "solver", 271, 205);
      Button instrButton = new Button("instr", "instructions", 270, 205);
      Button exitButton = new Button("exitButton", "exit", 271, 205);

      buttons = new Button[4];
      buttons[0] = gameButton;
      buttons[1] = solverButton;
      buttons[2] = instrButton;
      buttons[3] = exitButton;

      for (Button b : buttons) {
         b.addActionListener(this);
         this.add(b);
      }
   }

   /**
    * Requests focus and draws a black line to conceal an image size mistake. :p
    *
    * Overrides - 
    * {@link JPanel#paintComponent(java.awt.Graphics) paintComponent}
    * @param g Graphics parameter.
    */
   @Override
   public void paintComponent(Graphics g) {
      requestFocusInWindow();
      g.drawLine(510, 0, 510, 410);
   }

   /**
    * Uses the button that was pressed to call the corresponding method.
    *
    * @param e ActionEvent parameter.
    */
   public void actionPerformed(ActionEvent e) {
      String command = e.getActionCommand();

      if ("game".equals(command)) {
         Sudoku.setCard("Card with Game");
      } else if ("solver".equals(command)) {
         Sudoku.setCard("Card with Solver");
      } else if ("instructions".equals(command)) {
         Sudoku.setCard("Card with About Section");
      } else if ("exit".equals(command)) {
         System.exit(0);
      }
   }

   /**
    * Listens for keyboard events.
    */
   private class KeyboardAdapter extends KeyAdapter {

      /**
       * Fires whenever a key on the keyboard is pressed.
       *
       * Changes to a card corresponding to the key that was pressed.
       *
       * @param e KeyEvent parameter.
       */
      @Override
      public void keyPressed(KeyEvent e) {
         int key = e.getKeyCode();

         if (key == KeyEvent.VK_ESCAPE
                 || key == KeyEvent.VK_E
                 || key == KeyEvent.VK_Q) {
            System.exit(0);
         } else if (key == KeyEvent.VK_ENTER
                 || key == KeyEvent.VK_SPACE
                 || key == KeyEvent.VK_P
                 || key == KeyEvent.VK_G) {
            Sudoku.setCard("Card with Game");
         } else if (key == KeyEvent.VK_S) {
            Sudoku.setCard("Card with Solver");
         } else if (key == KeyEvent.VK_I || key == KeyEvent.VK_H) {
            Sudoku.setCard("Card with About Section");
         }

      }
   }
}
