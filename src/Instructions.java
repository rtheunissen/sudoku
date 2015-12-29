/* File: Instructions.java  -  April 2011 */
package sudoku;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.JPanel;

/**
 * Instructions panel that paints the instruction pages onto the panel.
 *
 * @author Rudi Theunissen
 */
public class Instructions extends JPanel {

   /** Instance of {@link Images}, used to load images. */
   private Images images;
   /** Specifies the current page. */
   private String page;

   /**
    * Default Constructor - sets the initial page and sets up the panel.
    * @param page the initial page.
    */
   public Instructions(String page) {
      this.page = page;
      images = new Images(false);
      this.setPreferredSize(new Dimension(541, 410));
      addKeyListener(new KeyboardAdapter());
      addMouseListener(new MouseClickAdapter());
   }

   /**
    * Paints components.
    * @param g Graphics parameter.
    */
   @Override
   public void paintComponent(Graphics g) {
      requestFocusInWindow();
      super.paintComponent(g);

      String currentPage = (page.equals("about"))
              ? "instructions"
              : "button-summary";

      g.drawImage(images.getImage(currentPage), 0, 0, this);
   }

   /**
    * Cycles to the next page.
    */
   private void nextPage() {
      Sudoku.setCard(page.equals("about")
              ? "Card with Button Summary"
              : "Card with Menu");
   }

   /**
    * Listens for keyboard input.
    */
   private class KeyboardAdapter extends KeyAdapter {

      /**
       * Fires when a key is pressed - simply switches to the next page.
       * @param e KeyEvent parameter.
       */
      @Override
      public void keyPressed(KeyEvent e) {
         nextPage();
      }
   }

   /**
    * Listens for mouse input.
    */
   private class MouseClickAdapter extends MouseAdapter {

      /**
       * Fires when a mouse-click is detected - switches to the next page.
       * @param e MouseEvent parameter.
       */
      @Override
      public void mousePressed(MouseEvent e) {
         nextPage();
      }
   }
}
