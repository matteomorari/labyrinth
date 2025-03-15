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
  private final CurrentGoalPnl currentGoalsPnl;
  private final GoalsPlayersPnl playersGoalsPnl;
  private final JPanel rightPanel;

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
    JScrollBar verticalScrollBar = leftScrollPnl.getVerticalScrollBar();
    verticalScrollBar.setPreferredSize(new Dimension(15, 0));

    add(leftScrollPnl, BorderLayout.WEST);

    // Right panel (3 vertical components)
    rightPanel = new JPanel(new GridLayout(3, 1, 10, 10));
    rightPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, 0));

    // Create panels only once
    rightPanel.add(createPanel());
    rightPanel.add(createPanel());
    rightPanel.add(createPanel());
    add(rightPanel, BorderLayout.EAST);

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
    remove(rightPanel);

    if (isVertical) {
      // Vertical orientation - reconstruct left panel with horizontal layout
      JPanel horizontalLeftPanel = new JPanel(new GridLayout(1, 2, 10, 0));

      // Ensure components are visible with appropriate sizes
      currentGoalsPnl.setVisible(true);
      playersGoalsPnl.setVisible(true);

      // Set minimum sizes to ensure visibility
      currentGoalsPnl.setMinimumSize(new Dimension(100, 100));
      playersGoalsPnl.setMinimumSize(new Dimension(100, 100));

      horizontalLeftPanel.add(currentGoalsPnl);
      horizontalLeftPanel.add(playersGoalsPnl);

      // Update scroll pane settings for horizontal layout
      leftScrollPnl.setViewportView(horizontalLeftPanel);
      leftScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      leftScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

      // Vertical orientation (top and bottom panels)
      add(leftScrollPnl, BorderLayout.NORTH);
      add(rightPanel, BorderLayout.SOUTH);

      // Resize for vertical layout
      int topHeight = Math.min(gamePnlHeight / 4, 250);
      int bottomHeight = Math.min(gamePnlHeight / 4, 250);

      leftScrollPnl.setPreferredSize(new Dimension(0, topHeight));
      rightPanel.setPreferredSize(new Dimension(0, bottomHeight));
    } else {
      // Horizontal orientation - restore original vertical layout
      JPanel verticalLeftPanel = new JPanel();
      verticalLeftPanel.setLayout(new BoxLayout(verticalLeftPanel, BoxLayout.Y_AXIS));

      // Ensure components are visible with appropriate sizes
      currentGoalsPnl.setVisible(true);
      playersGoalsPnl.setVisible(true);

      currentGoalsPnl.setAlignmentX(CENTER_ALIGNMENT);
      playersGoalsPnl.setAlignmentX(CENTER_ALIGNMENT);

      verticalLeftPanel.add(currentGoalsPnl);
      verticalLeftPanel.add(Box.createVerticalStrut(10));
      verticalLeftPanel.add(playersGoalsPnl);

      // Update scroll pane settings for vertical layout
      leftScrollPnl.setViewportView(verticalLeftPanel);
      leftScrollPnl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      leftScrollPnl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      // Horizontal orientation (left and right panels)
      add(leftScrollPnl, BorderLayout.WEST);
      add(rightPanel, BorderLayout.EAST);

      // Calculate column widths based on available space
      LEFT_COLUMN_WIDTH = Math.max((gamePnlWidth - gamePnlHeight) / 2 - 20, 250);
      RIGHT_COLUMN_WIDTH = Math.max((gamePnlWidth - gamePnlHeight) / 2 - 20, 250);

      // Update component sizes
      leftScrollPnl.setPreferredSize(new Dimension(LEFT_COLUMN_WIDTH, 0));
      rightPanel.setPreferredSize(new Dimension(RIGHT_COLUMN_WIDTH, 0));
    }

    // Force the components to refresh their state
    currentGoalsPnl.handleParentResize();
    playersGoalsPnl.handleParentResize();

    // Make sure the scroll pane updates its UI
    leftScrollPnl.revalidate();

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
