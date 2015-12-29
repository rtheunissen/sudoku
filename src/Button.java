/* File: Button.java  -  April 2011 */
package sudoku;

import java.awt.Dimension;
import javax.swing.JButton;

/**
 * Creates a JButton to be used in {@link GamePanel} and {@link SolverPanel}.
 *
 * @author Rudi Theunissen
 * @see JButton
 */
public class Button extends JButton {

   /** Filename of the image to be used as the button's icon. */
   private String fileName;
   /** The button's{@link Button#setActionCommand(java.lang.String)} command. */
   private String command;
   /** The width of the button */
   private int width;
   /** The height of the button. */
   private int height;
   /** Instance of {@link Images} to load the ImageIcon for the button. */
   Images images = new Images(false);

   /**
    * Constructor for the button.
    *
    * @param fileName the filename.
    * @param command the action command.
    * @param width the width.
    * @param height the height.
    */
   public Button(String fileName, String command, int width, int height) {
      this.fileName = fileName;
      this.command = command;
      this.width = width;
      this.height = height;
      createButton();
   }

   /**
    * Creates the button according to the fields set by the constructor.
    */
   private void createButton() {
      this.setIcon(images.getImageIcon(fileName + "-button"));
      this.setActionCommand(command);
      this.setPreferredSize(new Dimension(width, height));
      this.setMaximumSize(new Dimension(width, height));
   }
}
