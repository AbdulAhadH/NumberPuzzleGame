/**
 * NumberPuzzleGame.Java
 * 
 * This program uses Swing to play a number puzzle game.
 * It is a sliding puzzle game which uses a frame of square tiles.
 * The tiles are placed in random order and the frame has one tile missing.
 * To solve the puzzle game, the players must place the tiles in order from 1-15 
 * by sliding and utilizing the empty tile space.
 * 
 * @author AbdulAhad Hussain
 * @version 1
 */

import java.awt.BorderLayout; 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

//NumberPuzzleGame
public class NumberPuzzleGame extends JPanel { //Grid is drawn in a panel
  
  // size
  private int size;
  // Number of tiles
  private int numTiles;
  // Grid UI Dimension
  private int dimension;
  // Foreground Color
  private static final Color FOREGROUND_COLOR = new Color(150, 53, 50); 
  
  // Random object to shuffle tiles
  private static final Random RANDOM = new Random();
  
  // Storing the tiles in a 1D Array of integers
  private int[] tiles;
  
  // Size of tile on UI
  private int tileSize;
  // Position of the blank tile
  private int blankPos;
  // Margin for the grid on the frame
  private int margin;
  // Grid UI Size
  private int gridSize;
//true if game over, false otherwise
  private boolean gameOver; 
  
  public NumberPuzzleGame(int size, int dim, int mar) {
    this.size = size;
    this.dimension = dim;
    this.margin = mar;
    
    numTiles = size * size - 1; // -1 because the blank tile does not count
    tiles = new int[size * size];
    
    // calculate grid size and tile size
    gridSize = (dim - 2 * margin);
    tileSize = gridSize / size;
    
    setPreferredSize(new Dimension(dimension, dimension + margin));
    setBackground(Color.BLACK);
    setForeground(FOREGROUND_COLOR);
    setFont(new Font("SansSerif", Font.BOLD, 60));
    
    gameOver = true;
    
    //Mouse clicks
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (gameOver) {
          newGame();
        } else {
          // get position of the click
          int ex = e.getX() - margin;
          int ey = e.getY() - margin;
          
          // click in the grid 
          if (ex < 0 || ex > gridSize  || ey < 0  || ey > gridSize)
            return;
          
          // get position in the grid
          int c1 = ex / tileSize;
          int r1 = ey / tileSize;
          
          // get position of the blank cell
          int c2 = blankPos % size;
          int r2 = blankPos / size;
          
          // convert in the 1D  
          int clickPos = r1 * size + c1;
          
          //direction
          int dir = 0;
          
          // search direction for multiple tile moves at once
          if (c1 == c2  &&  Math.abs(r1 - r2) > 0)
            dir = (r1 - r2) > 0 ? size : -size;
          else if (r1 == r2 && Math.abs(c1 - c2) > 0)
            dir = (c1 - c2) > 0 ? 1 : -1;
            
          if (dir != 0) {
            // move tiles in the direction wanted
            do {
              int newBlankPos = blankPos + dir;
              tiles[blankPos] = tiles[newBlankPos];
              blankPos = newBlankPos;
            } while(blankPos != clickPos);
            
            tiles[blankPos] = 0;
          }
          
          // check if game is solved
          gameOver = isSolved();
        }
        
        // repaint panel
        repaint();
      }
    });
    
    newGame();
  }
  
  private void newGame() {
    do {
      reset(); // reset in intial state
      shuffle(); // shuffle
    } while(!isSolvable()); //until the grid is solvable
    
    gameOver = false;
  }
  
  private void reset() {
    for (int i = 0; i < tiles.length; i++) {
      tiles[i] = (i + 1) % tiles.length;
    }
    
    // The blank tile is set to the last space
    blankPos = tiles.length - 1;
  }
  
  private void shuffle() {
    // Leave blank tile in the solved position
    int n = numTiles;
    
    while (n > 1) {
      int r = RANDOM.nextInt(n--);
      int tmp = tiles[r];
      tiles[r] = tiles[n];
      tiles[n] = tmp;
    }
  }
  
  
  // When the tile is preceded by a tile with higher value it counts
  // as an inversion. In this case, with the blank tile in the solved position,
  // the number of inversions must be even for the puzzle to be solvable.
  private boolean isSolvable() {
    int countInversions = 0;
    
    for (int i = 0; i < numTiles; i++) {
      for (int j = 0; j < i; j++) {
        if (tiles[j] > tiles[i])
          countInversions++;
      }
    }
    
    return countInversions % 2 == 0;
  }
  
  private boolean isSolved() {
    if (tiles[tiles.length - 1] != 0) // if blank tile is not in the solved position it's not solved
      return false;
    
    for (int i = numTiles - 1; i >= 0; i--) {
      if (tiles[i] != i + 1)
        return false;      
    }
    
    return true;
  }
  
  private void drawGrid(Graphics2D g) {
    for (int i = 0; i < tiles.length; i++) {
      
      int r = i / size;
      int c = i % size;
      // Convert in coordinates on the UI
      int x = margin + c * tileSize;
      int y = margin + r * tileSize;
      
      // check special case for blank tile
      if(tiles[i] == 0) {
        if (gameOver) {
          g.setColor(FOREGROUND_COLOR);
          drawCenteredString(g, "\u2713", x, y);
        }
        
        continue;
      }
      
      // for other tiles
      g.setColor(getForeground());
      g.fillRoundRect(x, y, tileSize, tileSize, 24, 24);
      g.setColor(Color.BLACK);
      g.drawRoundRect(x, y, tileSize, tileSize, 24, 24);
      g.setColor(Color.WHITE);
      
      drawCenteredString(g, String.valueOf(tiles[i]), x , y);
    }
  }
  
  private void drawStartMessage(Graphics2D g) {
    if (gameOver) {
      g.setFont(getFont().deriveFont(Font.BOLD, 18));
      g.setColor(FOREGROUND_COLOR);
      String s = "Click to start new game";
      g.drawString(s, (getWidth() - g.getFontMetrics().stringWidth(s)) / 2,
          getHeight() - margin);
    }
  }
  
  private void drawCenteredString(Graphics2D g, String s, int x, int y) {
    // center string s for the given tile (x,y)
    FontMetrics fm = g.getFontMetrics();
    int asc = fm.getAscent();
    int desc = fm.getDescent();
    g.drawString(s,  x + (tileSize - fm.stringWidth(s)) / 2, 
        y + (asc + (tileSize - (asc + desc)) / 2));
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2D = (Graphics2D) g;
    g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    drawGrid(g2D);
    drawStartMessage(g2D);
  }
  
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setTitle("NumberPuzzleGame");
      frame.setResizable(false);
      frame.add(new NumberPuzzleGame(4, 550, 30), BorderLayout.CENTER);
      frame.pack();
      // center on the screen
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }

  
}