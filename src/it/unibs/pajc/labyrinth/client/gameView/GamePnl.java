package it.unibs.pajc.labyrinth.client.gameView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class GamePnl extends JPanel {

  public GamePnl() {
    setLayout(new BorderLayout(10, 10));
    this.setBorder(
        BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 20px padding around the frame

    // Left panel (2 vertical components)
    JPanel leftPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    leftPanel.add(createPanel());
    leftPanel.add(createPanel());
    leftPanel.setPreferredSize(new Dimension(300, leftPanel.getPreferredSize().height));
    add(leftPanel, BorderLayout.WEST);

    // Right panel (3 vertical components)
    JPanel rightPanel = new JPanel(new GridLayout(3, 1, 10, 10));
    rightPanel.add(createPanel());
    rightPanel.add(createPanel());
    rightPanel.add(createPanel());
    rightPanel.setPreferredSize(new Dimension(300, rightPanel.getPreferredSize().height));
    add(rightPanel, BorderLayout.EAST);

    // Center panel
    JPanel gameBoardPanel = new BoardPnl();
    add(gameBoardPanel, BorderLayout.CENTER);
  }

  // public GamePnl() {
  //     setLayout(new GridBagLayout());
  //     GridBagConstraints gbc = new GridBagConstraints();
  //     gbc.insets = new Insets(10, 10, 10, 10);

  //     // Left panel (2 vertical components)
  //     JPanel leftPanel = new JPanel(new GridLayout(2, 1, 10, 10));
  //     leftPanel.add(createPanel());
  //     leftPanel.add(createPanel());
  //     leftPanel.setPreferredSize(new Dimension(200, leftPanel.getPreferredSize().height));
  //     gbc.gridx = 0;
  //     gbc.gridy = 0;
  //     gbc.anchor = GridBagConstraints.WEST;
  //     gbc.fill = GridBagConstraints.VERTICAL;
  //     gbc.weightx = 0;
  //     gbc.weighty = 1.0;
  //     add(leftPanel, gbc);

  //     // Right panel (3 vertical components)
  //     JPanel rightPanel = new JPanel(new GridLayout(3, 1, 10, 10));
  //     rightPanel.add(createPanel());
  //     rightPanel.add(createPanel());
  //     rightPanel.add(createPanel());
  //     rightPanel.setPreferredSize(new Dimension(200, rightPanel.getPreferredSize().height));
  //     gbc.gridx = 2;
  //     gbc.gridy = 0;
  //     gbc.anchor = GridBagConstraints.EAST;
  //     gbc.fill = GridBagConstraints.VERTICAL;
  //     gbc.weightx = 0;
  //     gbc.weighty = 1.0;
  //     add(rightPanel, gbc);

  //     // Center panel
  //     JPanel centerPanel = new BoardPnl();
  //     gbc.gridx = 1;
  //     gbc.gridy = 0;
  //     gbc.anchor = GridBagConstraints.CENTER;
  //     gbc.fill = GridBagConstraints.BOTH;
  //     gbc.weightx = 1.0;
  //     gbc.weighty = 1.0;
  //     add(centerPanel, gbc);
  // }

  // Utility method to create a placeholder panel with a label
  private static JPanel createPanel() {
    JPanel panel = new JPanel();
    panel.setBackground(Color.LIGHT_GRAY);
    return panel;
  }
}
