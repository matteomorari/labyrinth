package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.core.utility.Orientation;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
  GOAL_CARD_BG("goal_card_bg.png"),
  RED_PLAYER_SPRITE("red_player_sprite.png"),
  BLACK_PLAYER_SPRITE("black_player_sprite.png"),
  PINK_PLAYER_SPRITE("pink_player_sprite.png"),
  GREEN_PLAYER_SPRITE("green_player_sprite.png"),
  ;

  private static final int IMAGE_SIZE = 72;
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

  public BufferedImage getSprite(int xGrid, int yGrid) {

    if (image == null) {
      System.out.println("invalid sprite sheet");
    }

    return image.getSubimage(xGrid * IMAGE_SIZE, yGrid * IMAGE_SIZE, IMAGE_SIZE, IMAGE_SIZE);
  }

  public HashMap<Orientation, BufferedImage[]> getAnimationSprite() {
    HashMap<Orientation, BufferedImage[]> playerAnimation =
        new HashMap<Orientation, BufferedImage[]>();

    BufferedImage[] down = {getSprite(0, 0), getSprite(1, 0), getSprite(2, 0), getSprite(3, 0)};
    playerAnimation.put(Orientation.SOUTH, down);

    BufferedImage[] left = {getSprite(0, 1), getSprite(1, 1), getSprite(2, 1), getSprite(3, 1)};
    playerAnimation.put(Orientation.WEST, left);

    BufferedImage[] right = {getSprite(0, 2), getSprite(1, 2), getSprite(2, 2), getSprite(3, 2)};
    playerAnimation.put(Orientation.EAST, right);

    BufferedImage[] up = {getSprite(0, 3), getSprite(1, 3), getSprite(2, 3), getSprite(3, 3)};
    playerAnimation.put(Orientation.NORD, up);

    return playerAnimation;
  }

  public BufferedImage getStandingAnimationImage() {
    return getSprite(1, 0);
  }

  public BufferedImage rotateImage(double angle) {
    double radians = Math.toRadians(angle);
    double sin = Math.abs(Math.sin(radians));
    double cos = Math.abs(Math.cos(radians));
    int w = image.getWidth();
    int h = image.getHeight();
    int newWidth = (int) Math.floor(w * cos + h * sin);
    int newHeight = (int) Math.floor(h * cos + w * sin);

    BufferedImage rotatedImage =
        new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
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

  /**
   * Scales a BufferedImage to the specified dimensions while maintaining quality.
   *
   * @param original The original BufferedImage to scale
   * @param targetWidth The desired width
   * @param targetHeight The desired height
   * @return A new BufferedImage scaled to the target dimensions
   */
  public BufferedImage scaleBufferedImage(
      BufferedImage original, int targetWidth, int targetHeight) {
    // Return original if invalid dimensions
    if (original == null || targetWidth <= 0 || targetHeight <= 0) {
      return original;
    }

    // Create a new BufferedImage with the target size
    BufferedImage scaledImage =
        new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);

    // Create graphics context for the new image
    Graphics2D g2d = scaledImage.createGraphics();

    // Set high quality rendering hints
    g2d.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw the original image scaled to the new dimensions
    g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
    g2d.dispose();

    return scaledImage;
  }

  public BufferedImage scaleBufferedImage(int targetWidth, int targetHeight) {
    return scaleBufferedImage(image, targetWidth, targetHeight);
  }
}
