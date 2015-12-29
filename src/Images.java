/* File: Images.java  -  April 2011 */
package sudoku;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 * Utility class that loads and stores all the images that are used in the app.
 *
 * @author Rudi Theunissen
 */
public class Images {

   /** An array that contains all the number images - white, mini, green, red.*/
   public Image[] numberImages;
   /** Maps a filename to an image. */
   public HashMap<String, Image> imageMap;
   /** Maps a number type (white, mini, green red) to an array index value. */
   public HashMap<String, Integer> numberImageMap;

   /**
    * Default Constructor - sets an instance up for image loading.
    *
    * @param loadGameImages whether or not to load the number images.
    */
   public Images(boolean loadGameImages) {
      if (loadGameImages) {
         loadNumberPictures();
      }
      loadImages();
   }

   /**
    * Loads all the non-number images into maps and arrays.
    */
   private void loadImages() {
      String[] names = new String[]{"sudoku-grid", "selector", "instructions",
         "button-summary", "help-exceeded", "game-finished", "invalid-solve"};

      ArrayList<Image> images = new ArrayList<Image>();
      imageMap = new HashMap<String, Image>();

      for (int i = 0; i < names.length; i++) {
         Image current = getResourceImage(names[i]);
         do {
            images.add(current);
         } while (current == null);
         imageMap.put(names[i], images.get(i));
      }
   }

   /**
    * Loads the number images into maps and arrays.
    */
   private void loadNumberPictures() {
      numberImageMap = new HashMap<String, Integer>();
      numberImageMap.put("white", 0);
      numberImageMap.put("mini", 9);
      numberImageMap.put("green", 18);
      numberImageMap.put("red", 27);

      // loads all the number images into the array.
      numberImages = new Image[36];
      for (int i = 1; i < 10; i++) {
         numberImages[i - 1] = getResourceImage(i + "");
         numberImages[i + 8] = getResourceImage(i + "-mini");
         numberImages[i + 17] = getResourceImage(i + "-green");
         numberImages[i + 26] = getResourceImage(i + "-red");
      }
   }

   /**
    * Loads and returns an {@link ImageIcon} resource.
    *
    * Reduces code clutter when loading image icon resources.
    *
    * @param fileName The path of the image resource.
    * @return ImageIcon for a JButton's constructor parameter.
    */
   public ImageIcon getImageIcon(String fileName) {
      String imageDirectory = Sudoku.IMAGE_DIRECTORY;
      URL imgURL = getClass().getResource(imageDirectory + fileName + ".gif");
      Image image = Toolkit.getDefaultToolkit().getImage(imgURL);
      ImageIcon imageIcon = new ImageIcon(image);
      return imageIcon;
   }

   /**
    * Loads and returns an {@link Image} resource.
    *
    * Reduces code clutter when loading an image resource from a file.
    *
    * @param fileName name of the image resource.
    * @return Image as resource.
    */
   private Image getResourceImage(String fileName) {
      String imageDirectory = Sudoku.IMAGE_DIRECTORY;
      URL imgURL = getClass().getResource(imageDirectory + fileName + ".gif");
      Image image = Toolkit.getDefaultToolkit().getImage(imgURL);
      return image;
   }

   /**
    * Loads and returns a number {@link Image} resource.
    *
    * Reduces code clutter when loading a number image resource from a class.
    *
    * @param type specifies what type of number image - white, mini, green, red.
    * @param numberImage The position (from 0 - 80) in the game array.
    * @return Image as resource.
    */
   public Image getNumberImage(String type, int numberImage) {
      return numberImages[numberImage + numberImageMap.get(type) - 1];
   }

   /**
    * Reduces code clutter when loading an image resource from a class.
    *
    * @param type specifies which image is being requested.
    * @return Image as resource.
    */
   public Image getImage(String type) {
      return imageMap.get(type);
   }

   /**
    * Returns the array that contains all the number images.

    * @return the array that contains all the number images.
    */
   public Image[] getNumberImagesArray() {
      return numberImages;
   }
}
