package it.unibs.pajc.labyrinth.client.views;

import it.unibs.pajc.labyrinth.client.components.game.AvailableCardPnl;
import it.unibs.pajc.labyrinth.client.components.game.BoardPnl;
import it.unibs.pajc.labyrinth.client.components.game.CurrentGoalPnl;
import it.unibs.pajc.labyrinth.client.components.game.CurrentPlayerPnl;
import it.unibs.pajc.labyrinth.client.components.game.GoalsPlayersPnl;
import it.unibs.pajc.labyrinth.client.components.game.PowerPnl;
import it.unibs.pajc.labyrinth.client.controllers.labyrinth.LabyrinthController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class GamePnl extends JPanel {
  private static final int DEFAULT_COLUMN_WIDTH = 275;
  private static final int PANEL_PADDING = 10;
  private static final int SCROLLBAR_WIDTH = 15;
  private static final int COLUMN_MARGIN = 20;

  private LabyrinthController controller;
  private int leftColumnWidth = DEFAULT_COLUMN_WIDTH;
  private int rightColumnWidth = DEFAULT_COLUMN_WIDTH;

  private JScrollPane leftScrollPnl;
  private JScrollPane rightScrollPnl;
  private CurrentPlayerPnl currentPlayerPnl;
  private AvailableCardPnl availableCardPnl;
  private PowerPnl powerPnl;
  private CurrentGoalPnl currentGoalsPnl;
  private GoalsPlayersPnl playersGoalsPnl;

  public GamePnl(LabyrinthController controller) {
    this.controller = controller;

    setLayout(new BorderLayout(PANEL_PADDING, PANEL_PADDING));
    setBorder(
        BorderFactory.createEmptyBorder(
            PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));

    // Initialize components
    JPanel leftPanel = createLeftPanel();
    JPanel rightPanel = createRightPanel();
    JPanel gameBoardPanel = new BoardPnl(controller);

    // Initialize scroll panes
    leftScrollPnl = createScrollPane(leftPanel, leftColumnWidth);
    rightScrollPnl = createScrollPane(rightPanel, rightColumnWidth);

    // Add panels to the layout
    add(leftScrollPnl, BorderLayout.WEST);
    add(rightScrollPnl, BorderLayout.EAST);
    add(gameBoardPanel, BorderLayout.CENTER);

    // Register listener for resizing
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            recalculateLayout();
          }
        });
  }

  private JPanel createLeftPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // Create components
    currentGoalsPnl = new CurrentGoalPnl(controller);
    currentGoalsPnl.setAlignmentX(CENTER_ALIGNMENT);

    playersGoalsPnl = new GoalsPlayersPnl(controller, leftColumnWidth);
    playersGoalsPnl.setAlignmentX(CENTER_ALIGNMENT);

    // Add components
    panel.add(currentGoalsPnl);
    panel.add(Box.createVerticalStrut(PANEL_PADDING));
    panel.add(playersGoalsPnl);

    return panel;
  }

  private JPanel createRightPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // Create components
    currentPlayerPnl = new CurrentPlayerPnl(controller, rightColumnWidth);
    currentPlayerPnl.setAlignmentX(CENTER_ALIGNMENT);

    availableCardPnl = new AvailableCardPnl(controller);
    availableCardPnl.setAlignmentX(CENTER_ALIGNMENT);

    powerPnl = new PowerPnl(controller);
    powerPnl.setAlignmentX(CENTER_ALIGNMENT);

    // Add components
    panel.add(currentPlayerPnl);
    panel.add(Box.createVerticalStrut(PANEL_PADDING));
    panel.add(availableCardPnl);
    panel.add(Box.createVerticalStrut(PANEL_PADDING));
    panel.add(powerPnl);

    return panel;
  }

  private JScrollPane createScrollPane(JPanel panel, int width) {
    JScrollPane scrollPane = new JScrollPane(panel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.setPreferredSize(new Dimension(width, 0));

    // Customize scrollbar appearance
    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
    verticalScrollBar.setPreferredSize(new Dimension(SCROLLBAR_WIDTH, 0));

    return scrollPane;
  }

  /** Recalculates the layout based on current component size */
  public void recalculateLayout() {
    updateGamePanelWidth();

    notifyComponentsOfResize();

    revalidate();
    repaint();
  }

  /** Notifies all child components that parent has been resized */
  private void notifyComponentsOfResize() {
    currentGoalsPnl.handleParentResize();
    playersGoalsPnl.handleParentResize();
    currentPlayerPnl.handleParentResize();
    availableCardPnl.handleParentResize();
    powerPnl.handleParentResize();
  }

  private void updateGamePanelWidth() {
    int gamePnlWidth = getWidth();
    int gamePnlHeight = getHeight();
    int boardSize = Math.min(gamePnlWidth, gamePnlHeight);
    int remainingWidth = Math.max(0, gamePnlWidth - boardSize);

    // Calculate column widths based on available space
    int newColumnWidth = Math.max(remainingWidth / 2 - COLUMN_MARGIN, DEFAULT_COLUMN_WIDTH);

    // Only update if dimensions actually changed to avoid unnecessary layout passes
    if (newColumnWidth != leftColumnWidth) {
      leftColumnWidth = rightColumnWidth = newColumnWidth;

      // Update component sizes
      leftScrollPnl.setPreferredSize(new Dimension(leftColumnWidth, 0));
      rightScrollPnl.setPreferredSize(new Dimension(rightColumnWidth, 0));
    }
  }

  public void update() {
    if (controller.isGameOver()) {
      JOptionPane.showMessageDialog(
          this,
          "Game Over: " + controller.getCurrentPlayer().getColorName() + " won",
          "Game Over",
          JOptionPane.INFORMATION_MESSAGE);
      goBackToHome();
    }
    if (controller.isGameCrashed()) {
      JOptionPane.showMessageDialog(
          this,
          "A player has crashed. You will be redirected to the home page.",
          "Game Crashed",
          JOptionPane.ERROR_MESSAGE);
      goBackToHome();
    }

    repaint();
  }

  private void goBackToHome() {
    JPanel parent = (JPanel) getParent();
    parent.removeAll();
    parent.setLayout(new BorderLayout());
    parent.add(new StartPnl(), BorderLayout.CENTER);
    parent.revalidate();
    parent.repaint();
  }
}
