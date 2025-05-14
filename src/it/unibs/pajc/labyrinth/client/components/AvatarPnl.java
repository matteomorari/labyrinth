package it.unibs.pajc.labyrinth.client.components;

import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

// TODO: add a remove method to remove the avatar from the lobby
public class AvatarPnl extends JPanel {
  private Player player;
  private static final Color bgColor = Color.WHITE;
  private BufferedImage avatarImage;

  public AvatarPnl() {
    setOpaque(false);
    setPreferredSize(new java.awt.Dimension(120, 120));
    setFocusable(true);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // bg
    g2.setColor(bgColor);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

    // Draw avatar image at center
    // Draw border
    if (player != null && player.getColor() != null) {
      g2.setStroke(new BasicStroke(3));
      g2.setColor(player.isReadyToPlay() ? Color.GREEN : Color.RED);
      g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
      avatarImage =
          ImageCntrl.valueOf(player.getColorName() + "_PLAYER_SPRITE").getStandingAnimationImage();
      avatarImage = ImageCntrl.scaleBufferedImage(avatarImage, 100, 100);
      int x = (getWidth() - avatarImage.getWidth()) / 2;
      int y = (getHeight() - avatarImage.getHeight()) / 2;
      g2.drawImage(avatarImage, x, y, this);
    } else {
      // TODO: get random
    }

    g2.dispose();
  }

  public void setAvatarImage(BufferedImage image) {
    this.avatarImage = image;
    repaint();
  }

  public BufferedImage getAvatarImage() {
    return avatarImage;
  }

  public void clearAvatarImage() {
    this.avatarImage = null;
    repaint();
  }

  public boolean hasAvatarImage() {
    return avatarImage != null;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }
}
