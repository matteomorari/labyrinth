package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.LabyrinthController;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class PowerPnl extends JPanel {
  // UI Constants
  private static final int DEFAULT_CARD_WIDTH = 100;
  private static final int MIN_CONTAINER_WIDTH = 200;
  private static final int PANEL_VERTICAL_PADDING = 150;
  private static final int PARENT_CONTAINER_MARGIN = 20;
  private static final int SCROLL_BAR_WIDTH = 25;
  private static final int CARD_HORIZONTAL_MARGIN = 80;
  private static final int PANEL_CORNER_RADIUS = 20;
  private static final int TITLE_TEXT_TOP_MARGIN = 40;
  // private static final int TITLE_TEXT_LINE_SPACING = 10;
  // private static final int GOAL_IMAGE_TOP_MARGIN = 2;
  private static final int BUTTON_TOP_MARGIN = 10;
  private static final int TITLE_FONT_SIZE = 25;
  private static final String TITLE_FONT_FAMILY = "Times New Roman";

  private LabyrinthController controller;
  private BufferedImage powerImage;
  private int cardWidth = DEFAULT_CARD_WIDTH;
  private final Font titleFont = new Font(TITLE_FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE);
  private int panelWidth;
  private JButton useButton;

  public PowerPnl(LabyrinthController controller) {
    this.controller = controller;

    useButton = new CircularButton("resource\\images\\power.svg");
    useButton.addActionListener(e -> HandleUsePowerBtn());
    setLayout(null);
    add(useButton);
  }

  private void updatePanelSize(int width) {
    this.panelWidth = width;
    int panelHeight = powerImage.getHeight() + PANEL_VERTICAL_PADDING;
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
      updatePowerImage();
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
    int textY2 = textY1 + fm.getHeight(); // Add some spacing between lines

    g2.drawString(line1, textX1, textY1);

    updatePowerImage();
    cardWidth = Math.max(DEFAULT_CARD_WIDTH, panelWidth - CARD_HORIZONTAL_MARGIN);
    // Center the power image
    int imageY = textY2; // + GOAL_IMAGE_TOP_MARGIN; // Position below the text with some padding
    if (powerImage != null) {
      int imageX = (getWidth() - powerImage.getWidth()) / 2;
      g2.drawImage(powerImage, imageX, imageY, null);
    } else {
      g2.setColor(Color.gray);
      g2.fill(new RoundRectangle2D.Float(0, 0, cardWidth, cardWidth, 80, 80));
    }
    // Position the use button below the image with some padding
    int buttonY = imageY + powerImage.getHeight() + BUTTON_TOP_MARGIN;
    useButton.setBounds(
        (getWidth() - useButton.getPreferredSize().width) / 2,
        buttonY + 10,
        useButton.getPreferredSize().width,
        useButton.getPreferredSize().height);
  }

  public void updatePowerImage() {
    // Check if we need to recreate the scaled background
    cardWidth = Math.max(DEFAULT_CARD_WIDTH, panelWidth - CARD_HORIZONTAL_MARGIN);
    Card card = controller.getAvailableCard();

    if (card.getPower() != null) {
      powerImage =
          scaleImage(
              ImageCntrl.valueOf(card.getPower().getType().toString()).getImage(),
              cardWidth,
              Integer.MAX_VALUE);
      if (controller.getHasUsedPower()) {
        // Make the border of the image green
        Graphics2D g2d = powerImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(7));
        g2d.draw(
            new RoundRectangle2D.Float(
                0, -2, powerImage.getWidth(), powerImage.getHeight(), 100, 100));
        g2d.dispose();
      }
    } else {
      // Create a white rounded rectangle with dashed borders the size of the swap player power card
      powerImage = new BufferedImage(cardWidth, cardWidth, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = powerImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setColor(Color.gray);
      g2d.fill(new RoundRectangle2D.Float(2, 2, cardWidth - 2, cardWidth - 2, 80, 80));
      g2d.setColor(Color.BLACK);

      g2d.dispose();
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

  private void HandleUsePowerBtn() {
    if (controller.getAvailableCard().getPower() != null) {
      controller.usePower();
    }
  }
}
