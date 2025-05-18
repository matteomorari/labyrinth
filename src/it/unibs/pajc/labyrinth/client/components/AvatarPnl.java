package it.unibs.pajc.labyrinth.client.components;

import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.client.controllers.lobby.LobbyController;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class AvatarPnl extends JPanel {
  private LobbyController controller;
  private Player player;
  private static final Color bgColor = Color.WHITE;
  private BufferedImage avatarImage;
  private Boolean canBeRemoved;

  public AvatarPnl(LobbyController controller, Boolean canBeRemoved) {
    this.controller = controller;
    this.canBeRemoved = canBeRemoved;
    setPreferredSize(new java.awt.Dimension(120, 120));
    setFocusable(true);
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // bg
    g2.setColor(bgColor);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

    if (player == null || player.getColor() == null) {
      return;
    }

    g2.setStroke(new BasicStroke(3));
    g2.setColor(player.isReadyToPlay() ? Color.GREEN : Color.RED);
    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
    avatarImage =
        ImageCntrl.valueOf(player.getColorName() + "_PLAYER_SPRITE").getStandingImage();
    avatarImage = ImageCntrl.scaleBufferedImage(avatarImage, 100, 100);
    int x = (getWidth() - avatarImage.getWidth()) / 2;
    int y = (getHeight() - avatarImage.getHeight()) / 2;
    g2.drawImage(avatarImage, x, y, this);

    String playerTypeIconPath =
        player.isBot() ? "resource\\icons\\bot.svg" : "resource\\icons\\player.svg";

    SvgIconButton playerTypeButton = new SvgIconButton(playerTypeIconPath);
    playerTypeButton.setBgColor(Color.LIGHT_GRAY);
    playerTypeButton.setBorderRadius(-1);
    playerTypeButton.setButtonSize(35, 0);
    playerTypeButton.setSvgIconSize(25, 25);
    playerTypeButton.setBounds(
        x + (avatarImage.getWidth() - playerTypeButton.getIconDiameter()) / 2,
        getHeight() - playerTypeButton.getIconDiameter() - 5,
        playerTypeButton.getIconDiameter(),
        playerTypeButton.getIconDiameter());
    add(playerTypeButton);

    if(canBeRemoved != null && canBeRemoved) {
      SvgIconButton removePlayerButton = new SvgIconButton("resource\\icons\\delete.svg");
      removePlayerButton.setBgColor(new Color(255, 66, 66));
      removePlayerButton.setBorderRadius(-1);
      removePlayerButton.setButtonSize(35, 0);
      removePlayerButton.setSvgIconSize(25, 25);
      // place at the top right corner
      removePlayerButton.setBounds(
          getWidth() - playerTypeButton.getIconDiameter() - 5,
          5,
          playerTypeButton.getIconDiameter(),
          playerTypeButton.getIconDiameter());
      removePlayerButton.addActionListener(
          e -> {
            handleRemovePlayer();
          });
      add(removePlayerButton);
    }
  }

  private void handleRemovePlayer() {
    controller.removePlayerFromSelectedLobby(player);
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

  public void setCanBeRemoved(Boolean canBeRemoved) {
    this.canBeRemoved = canBeRemoved;
    repaint();
  }
}
