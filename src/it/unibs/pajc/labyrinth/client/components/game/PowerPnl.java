package it.unibs.pajc.labyrinth.client.components.game;

import it.unibs.pajc.labyrinth.client.components.SelectionDialog;
import it.unibs.pajc.labyrinth.client.components.SvgIconButton;
import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.client.controllers.labyrinth.LabyrinthController;
import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.enums.MyColors;
import it.unibs.pajc.labyrinth.core.enums.PowerType;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class PowerPnl extends JPanel {
  private static final int DEFAULT_CARD_WIDTH = 100;
  private static final int MIN_CONTAINER_WIDTH = 200;
  private static final int PANEL_VERTICAL_PADDING = 150;
  private static final int PARENT_CONTAINER_MARGIN = 20;
  private static final int SCROLL_BAR_WIDTH = 25;
  private static final int CARD_HORIZONTAL_MARGIN = 80;
  private static final int PANEL_CORNER_RADIUS = 20;
  private static final int TITLE_TEXT_TOP_MARGIN = 40;
  private static final int BUTTON_TOP_MARGIN = 10;
  private static final int TITLE_FONT_SIZE = 25;
  private static final String TITLE_FONT_FAMILY = "Times New Roman";

  private LabyrinthController controller;
  private BufferedImage powerImage;
  private int cardWidth = DEFAULT_CARD_WIDTH;
  private final Font titleFont = new Font(TITLE_FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE);
  private int panelWidth;
  private SvgIconButton useButton;

  public PowerPnl(LabyrinthController controller) {
    this.controller = controller;

    useButton = new SvgIconButton("resource\\icons\\power.svg");
    useButton.setButtonSize(50, 0);
    useButton.setBorderRadius(40);
    useButton.setSvgIconSize(40, 40);
    useButton.addActionListener(e -> handleUsePowerBtn());
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
    g2.setColor(MyColors.MAIN_BG_COLOR.getColor());
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
      powerImage = ImageCntrl.valueOf(card.getPower().getType().toString()).getImage();
      powerImage = ImageCntrl.scaleBufferedImage(powerImage, cardWidth, cardWidth);
      if (controller.getHasUsedPower()) {
        // Make the border of the image green
        Graphics2D g2d = powerImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(4));
        g2d.draw(
            new RoundRectangle2D.Float(
                0, 0, powerImage.getWidth(), powerImage.getHeight(), 100, 100));
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
  }

  private void handleUsePowerBtn() {
    if (controller.getAvailableCard().getPower() != null
        && !controller.getHasUsedPower()
        && controller.getHasCurrentPlayerInserted()) {
      if (controller.getAvailableCard().getPower().getType() == PowerType.SWAP_POSITION) {
        showSwapPlayerPopup();
      }
      if (controller.getAvailableCard().getPower().getType() == PowerType.CHOOSE_GOAL) {
        showSwapGoalPopup();
      }
      if (controller.getAvailableCard().getPower().getType() == PowerType.CHOOSE_SECOND_GOAL) {
        showSwapSecondGoalPopup();
      }
      controller.usePower();
    }
  }

  private void showSwapPlayerPopup() {
    Player currentPlayer = controller.getCurrentPlayer();
    java.util.List<SelectionDialog.SelectionItem> items = new java.util.ArrayList<>();

    for (Player player : controller.getPlayers()) {
      if (!player.equals(currentPlayer)) {
        BufferedImage playerImage =
            ImageCntrl.valueOf(player.getColorName() + "_PLAYER_SPRITE").getStandingImage();
        items.add(
            new SelectionDialog.SelectionItem(
                playerImage, () -> controller.setPlayerToSwap(player)));
      }
    }

    SelectionDialog.show(this, "SELECT   PLAYER", items);
  }

  private void showSwapGoalPopup() {
    Goal currentGoal = controller.getCurrentPlayer().getGoals().getFirst();
    java.util.List<SelectionDialog.SelectionItem> items = new java.util.ArrayList<>();

    for (Goal goal : controller.getCurrentPlayer().getGoals()) {
      if (!goal.equals(currentGoal)) {
        BufferedImage goalImage = ImageCntrl.valueOf("GOAL_" + goal.getType()).getImage();
        items.add(
            new SelectionDialog.SelectionItem(goalImage, () -> controller.setGoalToSwap(goal)));
      }
    }

    SelectionDialog.show(this, "SELECT   GOAL", items);
  }

  private void showSwapSecondGoalPopup() {
    Iterator<Goal> it = controller.getCurrentPlayer().getGoals().iterator();
    java.util.List<SelectionDialog.SelectionItem> items = new java.util.ArrayList<>();

    for (int i = 0; i < 2 && it.hasNext(); i++) {
      Goal goal = it.next();
      BufferedImage goalImage = ImageCntrl.valueOf("GOAL_" + goal.getType()).getImage();
      items.add(new SelectionDialog.SelectionItem(goalImage, () -> controller.setGoalToSwap(goal)));
    }

    SelectionDialog.show(this, "SELECT   GOAL", items);
  }
}
