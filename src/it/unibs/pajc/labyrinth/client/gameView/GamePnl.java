package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.core.LabyrinthController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class GamePnl extends JPanel {
  private final LabyrinthController controller;
  private static int LEFT_COLUMN_WIDTH = 250; // Default initial width
  private static int RIGHT_COLUMN_WIDTH = 250; // Default initial width

  private final JScrollPane leftScrollPnl;
  private final JScrollPane rightScrollPnl;
  private final CurrentPlayerPnl currentPlayerPnl;
  private final AvailableCardPnl availableCardPnl;
  private final PowerPnl powerPnl;
  private final CurrentGoalPnl currentGoalsPnl;
  private final GoalsPlayersPnl playersGoalsPnl;

  public GamePnl(LabyrinthController controller) {
    this.controller = controller;

    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Left panel (2 vertical components)
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

    // Create components for left panel
    currentGoalsPnl = new CurrentGoalPnl(controller);
    currentGoalsPnl.setAlignmentX(CENTER_ALIGNMENT);

    playersGoalsPnl = new GoalsPlayersPnl(controller, LEFT_COLUMN_WIDTH);
    playersGoalsPnl.setAlignmentX(CENTER_ALIGNMENT);

    leftPanel.add(currentGoalsPnl);
    leftPanel.add(Box.createVerticalStrut(10));
    leftPanel.add(playersGoalsPnl);

    // Wrap the leftPanel in a JScrollPane to handle overflow
    leftScrollPnl = new JScrollPane(leftPanel);
    leftScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    leftScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    leftScrollPnl.setBorder(BorderFactory.createEmptyBorder());
    leftScrollPnl.setPreferredSize(new Dimension(LEFT_COLUMN_WIDTH, 0));

    // Customize the scrollbar appearance
    JScrollBar leftVerticalScrollBar = leftScrollPnl.getVerticalScrollBar();
    leftVerticalScrollBar.setPreferredSize(new Dimension(15, 0));

    add(leftScrollPnl, BorderLayout.WEST);

    // Right panel (3 vertical components)
    JPanel rightPanel = new JPanel();
    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

    // Create components for right panel
    currentPlayerPnl = new CurrentPlayerPnl(controller, RIGHT_COLUMN_WIDTH);
    currentPlayerPnl.setAlignmentX(CENTER_ALIGNMENT);

    availableCardPnl = new AvailableCardPnl(controller);
    availableCardPnl.setAlignmentX(CENTER_ALIGNMENT);

    powerPnl = new PowerPnl(controller);
    powerPnl.setAlignmentX(CENTER_ALIGNMENT);

    rightPanel.add(currentPlayerPnl);
    rightPanel.add(Box.createVerticalStrut(10));
    rightPanel.add(availableCardPnl);
    rightPanel.add(Box.createVerticalStrut(10));
    rightPanel.add(powerPnl);

    // Wrap the rightPanel in a JScrollPane to handle overflow
    rightScrollPnl = new JScrollPane(rightPanel);
    rightScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    rightScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    rightScrollPnl.setBorder(BorderFactory.createEmptyBorder());
    rightScrollPnl.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, 0));

    // Customize the scrollbar appearance
    JScrollBar rightVerticalScrollBar = rightScrollPnl.getVerticalScrollBar();
    rightVerticalScrollBar.setPreferredSize(new Dimension(15, 0));

    add(rightScrollPnl, BorderLayout.EAST);

    // Center panel - create it once
    JPanel gameBoardPanel = new BoardPnl(controller);
    add(gameBoardPanel, BorderLayout.CENTER);

    // Add a component listener to set the column widths after the component is displayed
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            recalculateLayout();
          }
        });
  }

  /** Recalculates the layout based on current component size */
  private void recalculateLayout() {
    int gamePnlHeight = getHeight();
    int gamePnlWidth = getWidth();

    boolean isVertical = gamePnlWidth < gamePnlHeight;

    // Remove components to rearrange them
    remove(leftScrollPnl);
    remove(rightScrollPnl);

    if (isVertical) {
      // Vertical orientation - reconstruct left panel with horizontal layout
      JPanel horizontalLeftPanel = new JPanel(new GridLayout(1, 2, 10, 0));
      JPanel horizontalRightPanel = new JPanel(new GridLayout(1, 3, 10, 0));

      // Ensure components are visible with appropriate sizes
      currentGoalsPnl.setVisible(true);
      playersGoalsPnl.setVisible(true);
      currentPlayerPnl.setVisible(true);
      availableCardPnl.setVisible(true);
      powerPnl.setVisible(true);

      // Set minimum sizes to ensure visibility
      currentGoalsPnl.setMinimumSize(new Dimension(100, 100));
      playersGoalsPnl.setMinimumSize(new Dimension(100, 100));
      currentPlayerPnl.setMinimumSize(new Dimension(100, 100));
      availableCardPnl.setMinimumSize(new Dimension(100, 100));
      powerPnl.setMinimumSize(new Dimension(100, 100));

      horizontalLeftPanel.add(currentGoalsPnl);
      horizontalLeftPanel.add(playersGoalsPnl);

      horizontalRightPanel.add(currentPlayerPnl);
      horizontalRightPanel.add(availableCardPnl);
      horizontalRightPanel.add(powerPnl);

      // Update scroll pane settings for horizontal layout
      leftScrollPnl.setViewportView(horizontalLeftPanel);
      leftScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      leftScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

      rightScrollPnl.setViewportView(horizontalRightPanel);
      rightScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      rightScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

      // Vertical orientation (top and bottom panels)
      add(leftScrollPnl, BorderLayout.NORTH);
      add(rightScrollPnl, BorderLayout.SOUTH);

      // Resize for vertical layout
      int topHeight = Math.min(gamePnlHeight / 4, 250);
      int bottomHeight = Math.min(gamePnlHeight / 4, 250);

      leftScrollPnl.setPreferredSize(new Dimension(0, topHeight));
      rightScrollPnl.setPreferredSize(new Dimension(0, bottomHeight));
    } else {
      // Horizontal orientation - restore original vertical layout
      JPanel verticalLeftPanel = new JPanel();
      verticalLeftPanel.setLayout(new BoxLayout(verticalLeftPanel, BoxLayout.Y_AXIS));
      JPanel verticalRightPanel = new JPanel();
      verticalRightPanel.setLayout(new BoxLayout(verticalRightPanel, BoxLayout.Y_AXIS));

      // Ensure components are visible with appropriate sizes
      currentGoalsPnl.setVisible(true);
      playersGoalsPnl.setVisible(true);
      currentPlayerPnl.setVisible(true);
      availableCardPnl.setVisible(true);
      powerPnl.setVisible(true);

      currentGoalsPnl.setAlignmentX(CENTER_ALIGNMENT);
      playersGoalsPnl.setAlignmentX(CENTER_ALIGNMENT);
      currentPlayerPnl.setAlignmentX(CENTER_ALIGNMENT);
      availableCardPnl.setAlignmentX(CENTER_ALIGNMENT);
      powerPnl.setAlignmentX(CENTER_ALIGNMENT);

      verticalLeftPanel.add(currentGoalsPnl);
      verticalLeftPanel.add(Box.createVerticalStrut(10));
      verticalLeftPanel.add(playersGoalsPnl);

      verticalRightPanel.add(currentPlayerPnl);
      verticalRightPanel.add(Box.createVerticalStrut(10));
      verticalRightPanel.add(availableCardPnl);
      verticalRightPanel.add(Box.createVerticalStrut(10));
      verticalRightPanel.add(powerPnl);

      // Update scroll pane settings for vertical layout
      leftScrollPnl.setViewportView(verticalLeftPanel);
      leftScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      leftScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      rightScrollPnl.setViewportView(verticalRightPanel);
      rightScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      rightScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      // Horizontal orientation (left and right panels)
      add(leftScrollPnl, BorderLayout.WEST);
      add(rightScrollPnl, BorderLayout.EAST);

      // Calculate column widths based on available space
      LEFT_COLUMN_WIDTH = Math.max((gamePnlWidth - gamePnlHeight) / 2 - 20, 250);
      RIGHT_COLUMN_WIDTH = Math.max((gamePnlWidth - gamePnlHeight) / 2 - 20, 250);

      // Update component sizes
      leftScrollPnl.setPreferredSize(new Dimension(LEFT_COLUMN_WIDTH, 0));
      rightScrollPnl.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, 0));
    }

    // Force the components to refresh their state
    currentGoalsPnl.handleParentResize();
    playersGoalsPnl.handleParentResize();
    currentPlayerPnl.handleParentResize();
    availableCardPnl.handleParentResize();
    powerPnl.handleParentResize();

    // Make sure the scroll pane updates its UI
    leftScrollPnl.revalidate();
    rightScrollPnl.revalidate();

    revalidate();
    repaint();
  }

  // Utility method to create a placeholder panel
  private static JPanel createPanel() {
    JPanel panel = new JPanel();
    panel.setBackground(Color.LIGHT_GRAY);
    panel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, 100));
    return panel;
  }
}
