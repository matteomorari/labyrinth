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

public class GoalsPlayersPnl extends JPanel {
  private LabyrinthController controller;
  private final Font titleFont = new Font("Times New Roman", Font.BOLD, 25);
  private final Font playerFont = new Font("Times New Roman", Font.PLAIN, 16);

  public GoalsPlayersPnl(LabyrinthController controller) {
    this.controller = controller;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // draw background
    g2.setColor(Color.LIGHT_GRAY);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

    // Calculate dimensions
    int width = getWidth();
    int currentY = 30; // Starting Y position
    int lineHeight = 20;

    // Draw title
    g2.setFont(titleFont);
    g2.setColor(Color.DARK_GRAY);
    FontMetrics titleMetrics = g2.getFontMetrics();
    String titleLine1 = "CARTE";
    String titleLine2 = "RIMANENTI";

    int titleWidth1 = titleMetrics.stringWidth(titleLine1);
    int titleWidth2 = titleMetrics.stringWidth(titleLine2);

    g2.drawString(titleLine1, (width - titleWidth1) / 2, currentY);
    currentY += lineHeight;
    g2.drawString(titleLine2, (width - titleWidth2) / 2, currentY);
    currentY += lineHeight + 30; // Add some spacing after title

    // Draw player information
    g2.setFont(playerFont);
    FontMetrics playerMetrics = g2.getFontMetrics();

    if (controller != null && controller.getPlayers() != null) {
      for (Player player : controller.getPlayers()) {
        String playerText =
            "- " + player.getName() + " " + player.getGoals().size() + " goals left";
        int textWidth = playerMetrics.stringWidth(playerText);

        g2.drawString(playerText, (width - textWidth) / 2, currentY);
        currentY += lineHeight + 10;
      }
    }
    setPreferredSize(new Dimension(300, currentY));
  }
}
