package client.gameView;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import java.awt.event.MouseEvent;

public class Card extends JComponent{
  private ImageCntrl imageCntrl;
  private Image image;
  // private int posX;
  // private int posY;

  // The MouseListener that handles the click, etc.
  private MouseListener listener = new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
      System.out.println("Clicked");
    }
  };

  public Card(ImageCntrl imageCntrl) {
    this.imageCntrl = imageCntrl;
    this.image = imageCntrl.getImage();
    addMouseListener(listener);
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

  public Card rotate(double angle) {
    this.image = imageCntrl.rotateImage(angle).getImage();
    return this;
  }

}
