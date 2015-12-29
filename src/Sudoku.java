/* File: Sudoku.java  -  April 2011 */
package sudoku;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;

/**
 * Application class containing the main method that manages the
 * {@link CardLayout}.
 *
 * Forces the {@link LookAndFeel LAF}, creates the frame and layout, and adds
 * instances of each sub-application class to the deck of cards.
 *
 * @author Rudi Theunissen
 */
public class Sudoku {

   /** The main window frame. */
   private static JFrame frame;
   /** Sub-application instance - {@link GamePanel}. */
   private static GamePanel game;
   /** {@link Menu} that allows choice between game, solver, and instructions.*/
   private static Menu menu;
   /** First {@link Instructions} page, explaining the rules of the game. */
   private static Instructions aboutPage;
   /** Second {@link Instructions} page, showing a summary of the buttons. */
   private static Instructions buttonSummary;
   /** Sub-application instance - {@link SolverPanel}. */
   private static SolverPanel solverPanel;
   /** Panel that contains all the other panels, deck of cards. */
   private static JPanel cards;
   /** Layout for the application, see {@link CardLayout} */
   private static CardLayout cardLayout;
    /** The relative path to the image resources. */
   public final static String IMAGE_DIRECTORY = "resources/images/";
   /** The relative path to the puzzle resources. */
   public final static String PUZZLE_DIRECTORY = "resources/puzzles/";
   /** The number of puzzles that has been added to the puzzle files. */
   public final static int NUMBER_OF_PUZZLES_PER_DIFFICULTY = 30;

   /**
    * Initialises the application.
    *
    * @param args command line is not used in this application.
    */
   public static void main(String[] args) {
      initialise();
   }

   /**
    * Sets the layout and window mechanics.
    */
   private static void initialise() {
      try {
         UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      } catch (ClassNotFoundException ex) {
         System.err.println(ex);
         System.exit(0);
      } catch (InstantiationException ex) {
         System.err.println(ex);
         System.exit(0);
      } catch (IllegalAccessException ex) {
         System.err.println(ex);
         System.exit(0);
      } catch (UnsupportedLookAndFeelException ex) {
         System.err.println(ex);
         System.exit(0);
      }

      frame = new JFrame("Sudoku");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setResizable(false);
      frame.getContentPane().setPreferredSize(new Dimension(517, 416));

      menu = new Menu();

      // creates the main objects in a seperate thread - faster app load.
      SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
         @Override
         protected Void doInBackground() {
            game = new GamePanel();
            aboutPage = new Instructions("about");
            buttonSummary = new Instructions("summary");
            solverPanel = new SolverPanel();
            new Algorithms();
            return null;
         }
         @Override
         protected void done() {
            cards.add(game, "Card with Game");
            cards.add(aboutPage, "Card with About Section");
            cards.add(buttonSummary, "Card with Button Summary");
            cards.add(solverPanel, "Card with Solver");
         }
      };

      cards = new JPanel(new CardLayout());
      frame.getContentPane().add(cards, BorderLayout.CENTER);
      cardLayout = (CardLayout) (cards.getLayout());
      cards.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

      cards.add(menu, "Card with Menu");
      worker.execute();

      cardLayout.show(cards, "Card with Menu");
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   /**
    * Sets the current card of the {@link CardLayout}.
    *
    * @param cardName name of the card to be pushed to the front.
    */
   public static void setCard(String cardName) {
      cardLayout.show(cards, cardName);
   }
}
