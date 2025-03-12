package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.core.LabyrinthController;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class GoalStatusPnl extends JPanel {
  private LabyrinthController controller;
  private final Font titleFont = new Font("Times New Roman", Font.BOLD, 20);
  private final Font playerFont = new Font("Times New Roman", Font.PLAIN, 16);

  public GoalStatusPnl(LabyrinthController controller) {
    this.controller = controller;
    setBackground(Color.LIGHT_GRAY);
    setPreferredSize(new Dimension(300, 100));
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Calculate dimensions
    int width = getWidth();
    int currentY = 15; // Starting Y position
    int lineHeight = 20;

    // Draw title
    g2d.setFont(titleFont);
    FontMetrics titleMetrics = g2d.getFontMetrics();
    String titleLine1 = "CARTE";
    String titleLine2 = "RIMANENTI";

    int titleWidth1 = titleMetrics.stringWidth(titleLine1);
    int titleWidth2 = titleMetrics.stringWidth(titleLine2);

    g2d.drawString(titleLine1, (width - titleWidth1) / 2, currentY);
    currentY += lineHeight;
    g2d.drawString(titleLine2, (width - titleWidth2) / 2, currentY);
    currentY += lineHeight + 5; // Add some spacing after title

    // Draw player information
    g2d.setFont(playerFont);
    FontMetrics playerMetrics = g2d.getFontMetrics();

    if (controller != null && controller.getPlayers() != null) {
      for (Player player : controller.getPlayers()) {
        String playerText =
            "- " + player.getName() + " " + player.getGoals().size() + " goals left";
        int textWidth = playerMetrics.stringWidth(playerText);

        g2d.drawString(playerText, (width - textWidth) / 2, currentY);
        currentY += lineHeight;
      }
    }
  }
}
