package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.core.LabyrinthController;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class CurrentPlayerPnl extends JPanel {
  // Font constants
  private static final String FONT_FAMILY = "Times New Roman";
  private static final int PLAYER_FONT_SIZE = 23; // Increased font size

  // Layout constants
  private static final int CORNER_RADIUS = 20;
  private static final int STARTING_Y = 50;
  private static final int LINE_HEIGHT = 25;
  private static final int PADDING_X = 10;
  private static final int PADDING_Y = 7;
  private static final int PLAYER_BOTTOM_SPACING = 20;
  private static final int PLAYER_SIZE = 35;
  private static final int TEXT_OVAL_SPACING = 10;
  private static final int OVAL_Y_ADJUSTMENT = 2;

  // Panel dimension constants
  private static final int PARENT_WIDTH_PADDING = 20;
  private static final int SCROLLBAR_WIDTH = 25;
  private static final int MIN_WIDTH = 100;
  private static final int BASE_PANEL_HEIGHT = 30;
  private static final int PLAYER_HEIGHT = 40;

  private LabyrinthController controller;
  private final Font playerFont = new Font(FONT_FAMILY, Font.BOLD, PLAYER_FONT_SIZE);

  // Performance optimization fields
  private int lastWidth = -1;

  public CurrentPlayerPnl(LabyrinthController controller, int width) {
    this.controller = controller;

    // Set preferred size for the panel
    int panelHeight = calculateHeight();
    setPreferredSize(new Dimension(width, panelHeight));
    setMaximumSize(new Dimension(width, panelHeight));
  }

  private void updatePanelSize(int width) {
    int panelHeight = calculateHeight();
    setPreferredSize(new Dimension(width, panelHeight));
    setMaximumSize(new Dimension(width, panelHeight));
  }

  public void handleParentResize() {
    Container parent = getParent();
    if (parent != null) {
      // Find the actual width we should be using
      int containerWidth;
      if (parent.getWidth() <= 0) {
        // If direct parent doesn't have width yet, try to get it from the scroll pnl
        Container ancestor = SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
        if (ancestor != null && ancestor.getWidth() > 0) {
          containerWidth =
              ancestor.getWidth() - SCROLLBAR_WIDTH; // Account for scrollbar and insets
        } else {
          // Fallback to a reasonable default if no ancestor with width is found
          containerWidth = MIN_WIDTH * 2;
        }
      } else {
        containerWidth = parent.getWidth() - PARENT_WIDTH_PADDING;
      }

      int newWidth = Math.max(containerWidth, MIN_WIDTH); // Ensure minimum width

      // Only update if the width actually changed
      if (newWidth != lastWidth) {
        lastWidth = newWidth;
        updatePanelSize(newWidth);
        revalidate();
        repaint();
      }
    }
  }

  private int calculateHeight() {
    int baseHeight = BASE_PANEL_HEIGHT; // Base height for title and some padding
    return baseHeight + (PLAYER_HEIGHT);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // draw background
    g2.setColor(Color.LIGHT_GRAY);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);

    // Calculate dimensions
    int width = getWidth();
    int currentY = STARTING_Y; // Starting Y position

    // Draw player information
    g2.setFont(playerFont);
    g2.setColor(Color.DARK_GRAY);

    // Find the maximum width of player text
    int maxTextWidth = 0;

    String playerText = "TURNO GIOCATORE :  ";
    FontMetrics fm = g2.getFontMetrics();
    int textWidth = fm.stringWidth(playerText) + PLAYER_SIZE;
    if (textWidth > maxTextWidth) {
      maxTextWidth = textWidth;
    }

    // Calculate the total width needed (image + spacing + max text width)
    int totalWidth = PLAYER_SIZE + TEXT_OVAL_SPACING + maxTextWidth;
    // Adjust font size if text is too wide
    if (totalWidth > width) {
      float newSize = (float) PLAYER_FONT_SIZE * width / totalWidth;
      g2.setFont(playerFont.deriveFont(newSize));
      fm = g2.getFontMetrics();
      textWidth = fm.stringWidth(playerText);
      totalWidth = PLAYER_SIZE  + textWidth;
    } else {
      g2.setFont(playerFont.deriveFont((float) PLAYER_FONT_SIZE ));
      fm = g2.getFontMetrics();
      textWidth = fm.stringWidth(playerText) ;
      totalWidth =  textWidth + PLAYER_SIZE;
    }

    // Calculate left position to center the entire element
    int leftPosition = (width - totalWidth) / 2;

    // Draw player image on the left
    int playerY =
        currentY - PLAYER_SIZE / 2 - OVAL_Y_ADJUSTMENT; // Align vertically with text center
    g2.drawImage(
        ImageCntrl.valueOf(controller.getCurrentPlayer().getColorName() + "_PLAYER_SPRITE")
            .getStandingAnimationImage(),
        leftPosition + textWidth + TEXT_OVAL_SPACING - PADDING_X,
        playerY - 20,
        PLAYER_SIZE,
        PLAYER_SIZE,
        null);

    // Draw player text to the right of the image
    g2.setColor(Color.DARK_GRAY);
    g2.drawString(playerText, leftPosition , currentY - PADDING_Y);

    currentY += LINE_HEIGHT + PLAYER_BOTTOM_SPACING;
  }
}
