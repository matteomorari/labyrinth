package it.unibs.pajc.labyrinth.client.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JButton;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

public class CircularButton extends JButton {
  private BufferedImage svgImage;

  public CircularButton(String svgFilePath) {
    setPreferredSize(new Dimension(45, 45)); // Set the button size to be square
    setContentAreaFilled(false);
    setFocusPainted(false);
    setBorderPainted(false);
    loadSVG(svgFilePath);
    setOpaque(false);
  }

  private void loadSVG(String svgFilePath) {
    try {
      TranscoderInput input = new TranscoderInput(new File(svgFilePath).toURI().toString());
      ImageTranscoder transcoder =
          new ImageTranscoder() {
            @Override
            public BufferedImage createImage(int width, int height) {
              return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }

            @Override
            public void writeImage(BufferedImage image, TranscoderOutput output) {
              svgImage = image;
            }
          };
      transcoder.transcode(input, null);
    } catch (TranscoderException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw the circular background
    g2.setColor(Color.LIGHT_GRAY);
    g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));

    // Draw the SVG icon
    if (svgImage != null) {
      int x = (getWidth() - svgImage.getWidth()) / 2;
      int y = (getHeight() - svgImage.getHeight()) / 2;
      g2.drawImage(svgImage, x, y, this);
    }

    g2.dispose();
  }

  @Override
  public boolean contains(int x, int y) {
    Ellipse2D ellipse = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
    return ellipse.contains(x, y);
  }
}
