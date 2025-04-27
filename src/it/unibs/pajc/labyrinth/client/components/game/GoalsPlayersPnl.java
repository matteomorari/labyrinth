package it.unibs.pajc.labyrinth.client.components.game;

import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.core.LabyrinthController;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class GoalsPlayersPnl extends JPanel {
  // Font constants
  private static final String FONT_FAMILY = "Times New Roman";
  private static final int TITLE_FONT_SIZE = 25;
  private static final int PLAYER_FONT_SIZE = 20;

  // Layout constants
  private static final int CORNER_RADIUS = 20;
  private static final int STARTING_Y = 50;
  private static final int LINE_HEIGHT = 25;
  private static final int TITLE_BOTTOM_SPACING = 30;
  private static final int PLAYER_BOTTOM_SPACING = 20;
  private static final int PLAYER_SIZE = 35;
  private static final int TEXT_OVAL_SPACING = 10;
  private static final int OVAL_Y_ADJUSTMENT = 2;

  // Panel dimension constants
  private static final int PARENT_WIDTH_PADDING = 20;
  private static final int SCROLLBAR_WIDTH = 25;
  private static final int MIN_WIDTH = 100;
  private static final int BASE_PANEL_HEIGHT = 150;
  private static final int PLAYER_HEIGHT = 40;

  private LabyrinthController controller;
  private ArrayList<Player> players;
  private final Font titleFont = new Font(FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE);
  private final Font playerFont = new Font(FONT_FAMILY, Font.PLAIN, PLAYER_FONT_SIZE);

  // Performance optimization fields
  private int lastWidth = -1;

  public GoalsPlayersPnl(LabyrinthController controller, int width) {
    this.controller = controller;
    this.players = new ArrayList<>();
    for (Player player : controller.getPlayers()) {
      this.players.add(player);
    }

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
    int numPlayers =
        (controller != null && controller.getPlayers() != null)
            ? controller.getPlayers().size()
            : 0;
    return baseHeight + (PLAYER_HEIGHT * numPlayers);
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

    // Draw title
    g2.setFont(titleFont);
    g2.setColor(Color.DARK_GRAY);
    FontMetrics titleMetrics = g2.getFontMetrics();
    String titleLine1 = "CARTE";
    String titleLine2 = "RIMANENTI";

    int titleWidth1 = titleMetrics.stringWidth(titleLine1);
    int titleWidth2 = titleMetrics.stringWidth(titleLine2);

    g2.drawString(titleLine1, (width - titleWidth1) / 2, currentY);
    currentY += LINE_HEIGHT;
    g2.drawString(titleLine2, (width - titleWidth2) / 2, currentY);
    currentY += LINE_HEIGHT + TITLE_BOTTOM_SPACING; // Add some spacing after title

    // Draw player information
    g2.setFont(playerFont);

    // Find the maximum width of player text
    int maxTextWidth = 0;
    for (Player player : players) {
      String playerText = " " + player.getGoals().size() + " goals left";
      FontMetrics fm = g2.getFontMetrics();
      int textWidth = fm.stringWidth(playerText);
      if (textWidth > maxTextWidth) {
        maxTextWidth = textWidth;
      }
    }

    for (Player player : players) {
      String playerText = " " + player.getGoals().size() + " goals left";

      // Calculate the total width needed (oval + spacing + max text width)
      int totalWidth = PLAYER_SIZE + TEXT_OVAL_SPACING + maxTextWidth;

      // Calculate left position to center the entire element
      int leftPosition = (width - totalWidth) / 2;

      // Draw colored oval at the centered position
      int playerY =
          currentY - PLAYER_SIZE / 2 - OVAL_Y_ADJUSTMENT; // Align vertically with text center
      g2.drawImage(
          ImageCntrl.valueOf(player.getColorName() + "_PLAYER_SPRITE").getStandingAnimationImage(),
          leftPosition,
          playerY - 10,
          PLAYER_SIZE,
          PLAYER_SIZE ,
          null);

      // Draw player text to the right of the oval
      g2.setColor(Color.DARK_GRAY);
      g2.drawString(playerText, leftPosition + PLAYER_SIZE + TEXT_OVAL_SPACING, currentY);

      currentY += LINE_HEIGHT + PLAYER_BOTTOM_SPACING;
    }
  }
}
