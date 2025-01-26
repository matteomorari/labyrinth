package client.gameView;

import java.awt.*;

import javax.swing.JComponent;

public class CardImage extends JComponent {
  private ImageCntrl imageCntrl;
  private Image image;
  // private int posX;
  // private int posY;

  public CardImage(ImageCntrl imageCntrl) {
    this.imageCntrl = imageCntrl;
    this.image = imageCntrl.getImage();
  }

  public void draw(Graphics2D g2, int posX, int posY, int dimension) {
    this.draw(g2, posX, posY, dimension, dimension);
  }

  public void draw(Graphics2D g2, int posX, int posY, int width, int height) {
    Image image = this.image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    g2.drawImage(image, posX, posY, null);
  }

  public void paintComponent(Graphics g, int posX, int posY, int width, int height) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Image image = this.image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    g2.drawImage(image, posX, posY, null);
    setPreferredSize(new Dimension(width, height));
  }

  public void setImage(Image image) {
  }

  public CardImage rotate(double angle) {
    this.image = imageCntrl.rotateImage(angle);
    return this;
  }

}
