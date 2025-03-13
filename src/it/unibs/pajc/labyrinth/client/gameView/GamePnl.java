package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.core.LabyrinthController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class GamePnl extends JPanel {
  LabyrinthController controller;

  public GamePnl(LabyrinthController controller) {
    this.controller = controller;

    setLayout(new BorderLayout(10, 10)); // set 10px padding between components
    this.setBorder(
        BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 20px padding around the frame

    // Left panel (2 vertical components)
    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

    // Create components for left panel
    CurrentGoalPnl goalsPnl = new CurrentGoalPnl(controller);
    goalsPnl.setAlignmentX(CENTER_ALIGNMENT);

    GoalsPlayersPnl currentGoalPnl = new GoalsPlayersPnl(controller);
    currentGoalPnl.setAlignmentX(CENTER_ALIGNMENT);

    // Add components to left panel with flexible spacing
    leftPanel.add(goalsPnl);
    leftPanel.add(Box.createVerticalStrut(10));
    leftPanel.add(currentGoalPnl);
    leftPanel.revalidate();

    // Set minimum width but let height be determined by content
    leftPanel.setPreferredSize(new Dimension(300, 0));

    // Wrap the leftPanel in a JScrollPane to handle overflow
    JScrollPane leftScrollPane = new JScrollPane(leftPanel);
    leftScrollPane.setPreferredSize(new Dimension(300, leftPanel.getPreferredSize().height));
    leftScrollPane.setBorder(BorderFactory.createEmptyBorder());
    leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    add(leftScrollPane, BorderLayout.WEST);

    // Right panel (3 vertical components)
    JPanel rightPanel = new JPanel(new GridLayout(3, 1, 10, 10));
    rightPanel.add(createPanel());
    rightPanel.add(createPanel());
    rightPanel.add(createPanel());
    rightPanel.setPreferredSize(new Dimension(300, rightPanel.getPreferredSize().height));
    add(rightPanel, BorderLayout.EAST);

    // Center panel
    JPanel gameBoardPanel = new BoardPnl(controller);
    add(gameBoardPanel, BorderLayout.CENTER);
  }

  // Utility method to create a placeholder panel with a label
  private static JPanel createPanel() {
    JPanel panel = new JPanel();
    panel.setBackground(Color.LIGHT_GRAY);
    return panel;
  }
}


// gridBagLayout