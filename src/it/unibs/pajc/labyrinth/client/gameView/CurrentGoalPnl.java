package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.core.LabyrinthController;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class CurrentGoalPnl extends JPanel {

  private LabyrinthController controller;
  private ImageIcon goalCardBackground;
  private Image currentGoalImage;
  private final int CARD_WIDTH = 200;
  private final int CARD_HEIGHT = 280;
  private final Font titleFont = new Font("Times New Roman", Font.BOLD, 20);

  public CurrentGoalPnl(LabyrinthController controller) {
    this.controller = controller;

    // Set preferred size for the panel
    setPreferredSize(new Dimension(250, 400));

    // Load goal card background
    goalCardBackground = new ImageIcon("resource/images/goal_card_bg.png");
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
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

    // Draw the title text
    g2.setFont(titleFont);
    g2.setColor(Color.DARK_GRAY);

    // Use FontMetrics to center the text properly
    FontMetrics fm = g2.getFontMetrics();
    String line1 = "PROSSIMO";
    String line2 = "OBBIETTIVO";

    int textX1 = (getWidth() - fm.stringWidth(line1)) / 2;
    int textX2 = (getWidth() - fm.stringWidth(line2)) / 2;
    int textY1 = 40; // Position from top
    int textY2 = textY1 + fm.getHeight() + 5; // Add some spacing between lines

    g2.drawString(line1, textX1, textY1);
    g2.drawString(line2, textX2, textY2);

    // Draw the goal card image if it exists
    updateGoalImage();
    if (currentGoalImage != null) {
      int imageX = (getWidth() - CARD_WIDTH) / 2;
      int imageY = textY2 + 20; // Position below the text with some padding
      g2.drawImage(currentGoalImage, imageX, imageY, null);
    }
  }

  public void updateGoalImage() {
    // Create a scaled version of the background
    ImageIcon scaledBackground = scaleImage(goalCardBackground, CARD_WIDTH, CARD_HEIGHT);

    // If there's a goal, prepare to overlay it on the background
    if (controller.getCurrentPlayer() != null
        && !controller.getCurrentPlayer().getGoals().isEmpty()) {

      ImageCntrl goalImageCntrl =
          ImageCntrl.valueOf(
              "GOAL_" + controller.getCurrentPlayer().getCurrentGoal().getType().name());

      // Create a combined image
      currentGoalImage =
          createCombinedImage(
              scaledBackground.getImage(),
              goalImageCntrl.getImage(),
              scaledBackground.getIconWidth(),
              scaledBackground.getIconHeight());
    } else {
      // Just use the background
      currentGoalImage = scaledBackground.getImage();
    }

    // Request the panel to be repainted with the new image
    repaint();
  }

  private ImageIcon scaleImage(ImageIcon original, int maxWidth, int maxHeight) {
    if (original == null || original.getImageLoadStatus() != MediaTracker.COMPLETE) {
      System.err.println("Error loading image.");
      return null;
    }

    int originalWidth = original.getIconWidth();
    int originalHeight = original.getIconHeight();

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

    Image scaledImage = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
    return new ImageIcon(scaledImage);
  }

  private Image createCombinedImage(Image background, Image overlay, int width, int height) {
    // Create a new buffered image
    BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = combined.createGraphics();

    // Draw background
    g.drawImage(background, 0, 0, null);

    // Scale the overlay image
    int overlayWidth = width / 2;
    int overlayHeight = height / 3;

    // Maintain aspect ratio for overlay
    Image scaledOverlay =
        overlay.getScaledInstance(overlayWidth, overlayHeight, Image.SCALE_SMOOTH);

    // Draw the overlay centered
    int x = (width - overlayWidth) / 2;
    int y = (height - overlayHeight) / 2;
    g.drawImage(scaledOverlay, x, y, null);

    g.dispose();
    return combined;
  }
}
