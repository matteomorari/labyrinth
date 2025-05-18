package it.unibs.pajc.labyrinth.client.components;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LogoPanel extends JPanel {
  private final JLabel logoLabel;
  private final BufferedImage logo;
  private static final int LEFT_RIGHT_PADDING = 50;
  private static final int TOP_PADDING = 20;
  private static final int INITIAL_PANEL_WIDTH = 400;

  public LogoPanel(BufferedImage logo) {
    this.logo = logo;
    setLayout(new BorderLayout());
    setOpaque(false);
    setBorder(
        javax.swing.BorderFactory.createEmptyBorder(
            TOP_PADDING, LEFT_RIGHT_PADDING, 0, LEFT_RIGHT_PADDING));
    logoLabel = new JLabel();
    logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    add(logoLabel, BorderLayout.CENTER);

    updateLogoIcon();

    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            updateLogoIcon();
          }
        });
  }

  private void updateLogoIcon() {
    if (logo == null) return;
    int panelWidth = getWidth();
    if (panelWidth == 0) panelWidth = INITIAL_PANEL_WIDTH; // fallback for initial layout

    int availableWidth = Math.max(0, panelWidth - LEFT_RIGHT_PADDING * 2);

    int logoWidth = logo.getWidth();
    int logoHeight = logo.getHeight();
    int newWidth = Math.min(logoWidth, availableWidth);
    int newHeight = (int) ((double) logoHeight * newWidth / logoWidth);

    Image scaledLogo = logo.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    logoLabel.setIcon(new ImageIcon(scaledLogo));
  }
}