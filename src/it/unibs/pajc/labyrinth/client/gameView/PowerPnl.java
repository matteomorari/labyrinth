package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.core.LabyrinthController;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.*;
import java.awt.image.BufferedImage;

import it.unibs.pajc.labyrinth.core.Card;
import javax.swing.*;
import java.awt.FontMetrics;

public class PowerPnl extends JPanel {
  // UI Constants
  private static final int DEFAULT_CARD_WIDTH = 200;
  private static final int MIN_CONTAINER_WIDTH = 200;
  private static final int PANEL_VERTICAL_PADDING = 45;
  private static final int PARENT_CONTAINER_MARGIN = 20;
  private static final int SCROLL_BAR_WIDTH = 25;
  private static final int CARD_HORIZONTAL_MARGIN = 80;
  private static final int PANEL_CORNER_RADIUS = 20;
  private static final int TITLE_TEXT_TOP_MARGIN = 40;
  private static final int TITLE_TEXT_LINE_SPACING = 5;
  private static final int GOAL_IMAGE_TOP_MARGIN = 20;
  private static final int TITLE_FONT_SIZE = 25;
  private static final String TITLE_FONT_FAMILY = "Times New Roman";
  private static final float OVAL_STROKE_WIDTH = 3.0f;

  // Image scaling constants
  private static final int OVERLAY_WIDTH_DIVISOR = 2;
  private static final int OVERLAY_HEIGHT_DIVISOR = 3;

  private LabyrinthController controller;
  private BufferedImage powerImage;
  private int cardWidth = DEFAULT_CARD_WIDTH;
  private final Font titleFont = new Font(TITLE_FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE);
  private int panelWidth;

  public PowerPnl(LabyrinthController controller) {
    this.controller = controller;
  }

  private void updatePanelSize(int width) {
    this.panelWidth = width;
    int panelHeight =  ImageCntrl.valueOf("SWAP_POSITION").getImage().getWidth() + PANEL_VERTICAL_PADDING;
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
              ancestor.getWidth() - SCROLL_BAR_WIDTH; // Account for scrollbar and insets
        } else {
          // Fallback to a reasonable default if no ancestor with width is found
          containerWidth = MIN_CONTAINER_WIDTH;
        }
      } else {
        containerWidth = parent.getWidth() - PARENT_CONTAINER_MARGIN;
      }

      panelWidth = containerWidth;
      updateGoalImage();
      updatePanelSize(containerWidth);
      revalidate();
      repaint();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    // Enable anti-aliasing for smoother rendering
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // draw background
    g2.setColor(Color.LIGHT_GRAY);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), PANEL_CORNER_RADIUS, PANEL_CORNER_RADIUS);

    // Draw the title text
    g2.setFont(titleFont);
    g2.setColor(Color.DARK_GRAY);

    // Use FontMetrics to center the text properly
    FontMetrics fm = g2.getFontMetrics();
    String line1 = "POTERE";

    int textX1 = (getWidth() - fm.stringWidth(line1)) / 2;
    int textY1 = TITLE_TEXT_TOP_MARGIN; // Position from top
    int textY2 =
        textY1 + fm.getHeight() + TITLE_TEXT_LINE_SPACING; // Add some spacing between lines

    g2.drawString(line1, textX1, textY1);

    updateGoalImage();
    int imageX = (getWidth() - ImageCntrl.valueOf("SWAP_POSITION").getImage().getWidth(null)) / 2;
    int imageY = textY2 + GOAL_IMAGE_TOP_MARGIN; // Position below the text with some padding
    g2.drawImage(powerImage, imageX, imageY, null);
  }

  public void updateGoalImage() {
    // Check if we need to recreate the scaled background
    cardWidth = Math.max(DEFAULT_CARD_WIDTH, panelWidth - CARD_HORIZONTAL_MARGIN);
    Card card= controller.getAvailableCard();

    
    if(card.getPower()!=null){
      powerImage = scaleImage(ImageCntrl.valueOf(card.getPower().getType().toString()).getImage(), cardWidth, cardWidth);
    }
    repaint();
  }

  private BufferedImage scaleImage(BufferedImage original, int maxWidth, int maxHeight) {
    int originalWidth = original.getWidth();
    int originalHeight = original.getHeight();

    if (originalWidth <= 0 || originalHeight <= 0) {
      return original;
    }

    double aspectRatio = (double) originalWidth / originalHeight;
    int width = maxWidth;
    int height = (int) (width / aspectRatio);

    if (height > maxHeight) {
      height = maxHeight;
      width = (int) (height * aspectRatio);
    }

    Image tmp = original.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = scaledImage.createGraphics();
    g2d.drawImage(tmp, 0, 0, null);
    g2d.dispose();

    return scaledImage;
  }
}
