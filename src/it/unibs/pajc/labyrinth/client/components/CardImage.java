package it.unibs.pajc.labyrinth.client.components;

import it.unibs.pajc.labyrinth.client.animation.Animatable;
import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

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
    this.image = this.image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    g2.drawImage(image, posX, posY, null);
  }

  public BufferedImage getImage() {
    return (BufferedImage) image;
  }

  public void setImage(Image image) {
    this.image = image;
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
