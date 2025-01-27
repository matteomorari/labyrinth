package client.gameView;

import java.awt.*;

import javax.swing.JComponent;

import client.animation.Animatable;

public class CardImage implements Animatable {
  private ImageCntrl imageCntrl;
  Graphics2D g2;
  private Image image;
  private int posX;
  private int posY;

  public CardImage(ImageCntrl imageCntrl, Graphics2D g2) {
    this.imageCntrl = imageCntrl;
    this.image = imageCntrl.getImage();
    this.g2 = g2;
  }

  public void draw(int posX, int posY, int dimension) {
    this.draw(posX, posY, dimension, dimension);
  }

  public void draw(int posX, int posY, int width, int height) {
    Image image = this.image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    g2.drawImage(image, posX, posY, null);
  }

  public void paintComponent(Graphics g, int posX, int posY, int width, int height) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Image image = this.image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    g2.drawImage(image, posX, posY, null);
  }

  public void setImage(Image image) {
  }

  public CardImage rotate(double angle) {
    this.image = imageCntrl.rotateImage(angle);
    return this;
  }

  public CardImage setPosition(int posX, int posY) {
    this.posX = posX;
    this.posY = posY;
    return this;
  }

  @Override
  public void updateAnimation(int[] values) {
    this.setPosition(posX, posY);
    this.draw(posX, posY, values[0], values[1]);
  }
}
