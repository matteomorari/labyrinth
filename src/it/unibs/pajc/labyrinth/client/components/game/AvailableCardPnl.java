package it.unibs.pajc.labyrinth.client.components.game;

import it.unibs.pajc.labyrinth.client.animation.Animatable;
import it.unibs.pajc.labyrinth.client.animation.Animator;
import it.unibs.pajc.labyrinth.client.animation.EasingFunction;
import it.unibs.pajc.labyrinth.client.components.CardImage;
import it.unibs.pajc.labyrinth.client.components.RoundedIconButton;
import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.LabyrinthController;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class AvailableCardPnl extends JPanel implements Animatable {

  // UI Constants
  private static final int DEFAULT_CARD_WIDTH = 100;
  private static final int MIN_CONTAINER_WIDTH = 200;
  private static final int PANEL_VERTICAL_PADDING = 185;
  private static final int PARENT_CONTAINER_MARGIN = 20;
  private static final int SCROLL_BAR_WIDTH = 25;
  private static final int CARD_HORIZONTAL_MARGIN = 80;
  private static final int PANEL_CORNER_RADIUS = 20;
  private static final int TITLE_TEXT_TOP_MARGIN = 40;
  private static final int TITLE_TEXT_LINE_SPACING = 5;
  private static final int BUTTON_HORIZONTAL_GAP = 20;
  private static final int GOAL_IMAGE_TOP_MARGIN = 20;
  private static final int BUTTON_TOP_MARGIN = 20;
  private static final int TITLE_FONT_SIZE = 25;
  private static final String TITLE_FONT_FAMILY = "Times New Roman";

  private LabyrinthController controller;
  private BufferedImage availableCardImage;
  private int cardWidth = DEFAULT_CARD_WIDTH;
  private final Font titleFont = new Font(TITLE_FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE);
  private int panelWidth;
  private RoundedIconButton rotateButton;
  private RoundedIconButton skipTurnButton;
  private Animator animator;
  private int animationCardAngle;
  private boolean isRotating = false;

  public AvailableCardPnl(LabyrinthController controller) {
    this.controller = controller;
    availableCardImage = getCorrectCardImage();

    // Initialize the rotate button
    rotateButton = new RoundedIconButton("resource\\images\\rotate.svg");
    rotateButton.setButtonSize(50, 0);
    rotateButton.setBorderRadius(40);
    rotateButton.setSvgIconSize(40, 40);
    rotateButton.addActionListener(e -> handleRotationCardBtn());

    // Initialize the skip turn button
    skipTurnButton = new RoundedIconButton("resource\\images\\skip.svg");
    skipTurnButton.setButtonSize(50, 0);
    skipTurnButton.setBorderRadius(40);
    skipTurnButton.setSvgIconSize(40, 40);
    skipTurnButton.addActionListener(e -> handleSkipTurnBtn());

    // Set layout and add buttons
    add(rotateButton);
    add(skipTurnButton);

    // set animation
    animationCardAngle = 0;
    animator =
        new Animator(
            this,
            1000,
            EasingFunction.EASE_OUT_BOUNCE,
            () -> {
              onAnimationEnded();
            });
  }

  private void updatePanelSize(int width) {
    this.panelWidth = width;
    int panelHeight = availableCardImage.getHeight() + PANEL_VERTICAL_PADDING;
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
      updateCardImage();
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
    String line1 = "CARTA";
    String line2 = "DISPONIBILE";

    int textX1 = (getWidth() - fm.stringWidth(line1)) / 2;
    int textX2 = (getWidth() - fm.stringWidth(line2)) / 2;
    int textY1 = TITLE_TEXT_TOP_MARGIN; // Position from top
    int textY2 =
        textY1 + fm.getHeight() + TITLE_TEXT_LINE_SPACING; // Add some spacing between lines

    g2.drawString(line1, textX1, textY1);
    g2.drawString(line2, textX2, textY2);
    updateCardImage();

    int imageX = (getWidth() - availableCardImage.getWidth(null)) / 2;
    int imageY = textY2 + GOAL_IMAGE_TOP_MARGIN; // Position below the text with some padding
    g2.drawImage(availableCardImage, imageX, imageY, null);

    // Position the rotate and skip turn buttons below the image with some padding
    int buttonY = imageY + availableCardImage.getWidth() + BUTTON_TOP_MARGIN;
    int totalButtonWidth =
        rotateButton.getPreferredSize().width
            + skipTurnButton.getPreferredSize().width
            + BUTTON_HORIZONTAL_GAP;
    int buttonX = (getWidth() - totalButtonWidth) / 2;

    rotateButton.setBounds(
        buttonX,
        buttonY,
        rotateButton.getPreferredSize().width,
        rotateButton.getPreferredSize().height);
    skipTurnButton.setBounds(
        buttonX + rotateButton.getPreferredSize().width + BUTTON_HORIZONTAL_GAP,
        buttonY,
        skipTurnButton.getPreferredSize().width,
        skipTurnButton.getPreferredSize().height);
  }

  public void updateCardImage() {
    // Check if we need to recreate the scaled background
    cardWidth = Math.max(DEFAULT_CARD_WIDTH, panelWidth - CARD_HORIZONTAL_MARGIN);

    // if (controller.getHasCurrentPlayerInserted()) {
    availableCardImage = getCorrectCardImage();
    // ImageCntrl.valueOf("CARD_" + controller.getAvailableCard().getType()).getImage();
    availableCardImage = scaleImage(availableCardImage, cardWidth, Integer.MAX_VALUE);

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

    BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
    Graphics2D g2d = scaledImage.createGraphics();
    g2d.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.drawImage(original, 0, 0, width, height, null);
    g2d.dispose();

    return scaledImage;
  }

  private BufferedImage getCorrectCardImage() {
    Card card = controller.getAvailableCard();
    // BufferedImage availableCardImage = ;
    CardImage cardImage =
        new CardImage(
            ImageCntrl.valueOf("CARD_" + card.getType()), (Graphics2D) this.getGraphics());
    cardImage.rotate(card.getOrientation().ordinal() * 90 + animationCardAngle);
    return cardImage.getImage();
  }

  private void handleRotationCardBtn() {
    controller.getAvailableCard().rotate();
    isRotating = true;
    animationCardAngle = -90;
    animator.initializeAnimation(new int[] {-90}, new int[] {0});
    animator.start();
  }

  private void handleSkipTurnBtn() {
    controller.skipTurn();
  }

  @Override
  public void updateAnimation(int[] values) {
    animationCardAngle = values[0];
  }

  private boolean onAnimationEnded() {
    return isRotating = false;
  }
}
