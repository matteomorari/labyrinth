package it.unibs.pajc.labyrinth.client.gameView;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public enum ImageCntrl {
  // load images
  CARD_L("L.png"),
  CARD_I("I.png"),
  CARD_T("T.png"),
  GOAL_HELMET("helmet.png"),
  GOAL_SWORD("sword.png"),
  GOAL_SKULL("skull.png"),
  GOAL_GEM("gem.png"),
  GOAL_KEYS("keys.png"),
  GOAL_SCARAB("scarab.png"),
  GOAL_SWORD2("sword2.png"),
  GOAL_RING("ring.png"),
  GOAL_TREASURE("treasure.png"),
  GOAL_OWL("owl.png"),
  GOAL_BAT("bat.png"),
  GOAL_BEE("bee.png"),
  GOAL_BOOK("book.png"),
  GOAL_CANDELABRA("candelabra.png"),
  GOAL_COINS("coins.png"),
  GOAL_CROWN("crown.png"),
  GOAL_DRAGON("dragon.png"),
  GOAL_GHOST("ghost.png"),
  GOAL_GNOME("gnome.png"),
  GOAL_LAMP_GENIE("lamp_genie.png"),
  GOAL_LIZARD("lizard.png"),
  GOAL_MAP("map.png"),
  GOAL_MOUSE("mouse.png"),
  GOAL_FAIRY("fairy.png"),
  GOAL_CARD("goal.png");

  private final Path path;
  private BufferedImage image;

  ImageCntrl(String fileName) {
    this.path = Paths.get(System.getProperty("user.dir") + "/resource/images/" + fileName);
    this.image = loadImage(this.path);
  }

  // load image
  private BufferedImage loadImage(Path path) {
    try {
      return ImageIO.read(Files.newInputStream(path));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public BufferedImage getImage() {
    return image;
  }

  public BufferedImage rotateImage(double angle) {
    double radians = Math.toRadians(angle);
    double sin = Math.abs(Math.sin(radians));
    double cos = Math.abs(Math.cos(radians));
    int w = image.getWidth();
    int h = image.getHeight();
    int newWidth = (int) Math.floor(w * cos + h * sin);
    int newHeight = (int) Math.floor(h * cos + w * sin);

    BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = rotatedImage.createGraphics();
    g2d.setComposite(AlphaComposite.Src);
    AffineTransform at = new AffineTransform();
    at.translate((newWidth - w) / 2, (newHeight - h) / 2);
    at.rotate(radians, w / 2, h / 2);
    g2d.setTransform(at);
    g2d.drawImage(image, 0, 0, null);
    g2d.dispose();

    return rotatedImage;
  }
}
