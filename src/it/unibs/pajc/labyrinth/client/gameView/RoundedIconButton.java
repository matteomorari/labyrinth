package it.unibs.pajc.labyrinth.client.gameView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JButton;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

public class RoundedIconButton extends JButton {
  private BufferedImage svgImage;
  private String label;
  private String svgFilePath; // Store the SVG file path for reloading

  // Configurable sizes
  private int iconDiameter = 45;
  private int labelExtraHeight = 15;

  // Border radius configuration (default is full circle)
  private int borderRadius = -1; // -1 means circular, any other value sets rounded corner radius

  public RoundedIconButton(String svgFilePath) {
    this.svgFilePath = svgFilePath;
    setPreferredSize(new Dimension(iconDiameter, iconDiameter));
    setContentAreaFilled(false);
    setFocusPainted(false);
    setBorderPainted(false);
    loadSVG(svgFilePath);
  }

  public RoundedIconButton(String svgFilePath, String label) {
    this(svgFilePath);
    this.label = label;
    // Increase button height to accommodate text.
    setPreferredSize(new Dimension(iconDiameter, iconDiameter + labelExtraHeight));
  }

  /**
   * Set border radius for the button.
   *
   * @param radius the corner radius in pixels, or -1 for a circular button
   */
  public void setBorderRadius(int radius) {
    this.borderRadius = radius;
    repaint();
  }

  /**
   * Get the current border radius.
   *
   * @return the border radius, or -1 if the button is circular
   */
  public int getBorderRadius() {
    return borderRadius;
  }

  public void setLabel(String label) {
    this.label = label;
    // Adjust preferred size based on label presence.
    if (label != null && !label.isEmpty()) {
      setPreferredSize(new Dimension(iconDiameter, iconDiameter + labelExtraHeight));
    } else {
      setPreferredSize(new Dimension(iconDiameter, iconDiameter));
    }
    revalidate();
    repaint();
  }

  public void setButtonSize(int iconDiameter, int labelExtraHeight) {
    this.iconDiameter = iconDiameter;
    this.labelExtraHeight = labelExtraHeight;
    if (label != null && !label.isEmpty()) {
      setPreferredSize(new Dimension(iconDiameter, iconDiameter + labelExtraHeight));
    } else {
      setPreferredSize(new Dimension(iconDiameter, iconDiameter));
    }
    
    // Reload the SVG at the new size
    if (svgFilePath != null) {
      loadSVG(svgFilePath);
    }
    
    revalidate();
    repaint();
  }

  private void loadSVG(String svgFilePath) {
    try {
        TranscoderInput input = new TranscoderInput(new File(svgFilePath).toURI().toString());
        ImageTranscoder transcoder = new ImageTranscoder() {
            @Override
            public BufferedImage createImage(int width, int height) {
                return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            }

            @Override
            public void writeImage(BufferedImage image, TranscoderOutput output) {
                svgImage = image;
            }
        };

        // Set the desired width and height for the SVG rendering
        // Use a slightly larger size for better quality
        float scaleFactor = 1.5f;  // Render at higher resolution for sharper display
        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) iconDiameter * scaleFactor);
        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) iconDiameter * scaleFactor);
        transcoder.transcode(input, null);
    } catch (TranscoderException e) {
        e.printStackTrace();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    int width = getWidth();

    // Draw the background in the icon area based on borderRadius setting
    g2.setColor(Color.WHITE);
    if (borderRadius < 0) {
      // Draw a circle if borderRadius is -1 (default)
      g2.fill(new Ellipse2D.Float(0, 0, width, iconDiameter));
    } else {
      // Draw a rounded rectangle with the specified border radius
      g2.fill(new RoundRectangle2D.Float(0, 0, width, iconDiameter, borderRadius, borderRadius));
    }

    // Draw the SVG icon centered in the icon area.
    if (svgImage != null) {
      int x = (width - svgImage.getWidth()) / 2;
      int y = (iconDiameter - svgImage.getHeight()) / 2;
      g2.drawImage(svgImage, x, y, this);
    }

    // If a label is provided, draw it in the area below the icon.
    if (label != null && !label.isEmpty()) {
      g2.setColor(getForeground());
      FontMetrics fm = g2.getFontMetrics();
      int textWidth = fm.stringWidth(label);
      int textX = (width - textWidth) / 2;
      int remainingHeight = getHeight() - iconDiameter;
      int textY = iconDiameter + ((remainingHeight - fm.getHeight()) / 2) + fm.getAscent();
      g2.drawString(label, textX, textY);
    }

    g2.dispose();
  }

  public void setSvgIconSize(int width, int height) {
    // If we have a stored path, reload at the new size for best quality
    if (svgFilePath != null) {
      try {
          TranscoderInput input = new TranscoderInput(new File(svgFilePath).toURI().toString());
          ImageTranscoder transcoder = new ImageTranscoder() {
              @Override
              public BufferedImage createImage(int w, int h) {
                  return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
              }

              @Override
              public void writeImage(BufferedImage image, TranscoderOutput output) {
                  svgImage = image;
              }
          };

          // Set the desired dimensions
          transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
          transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);

          transcoder.transcode(input, null);
          repaint();
      } catch (TranscoderException e) {
          e.printStackTrace();
      }
    } else if (svgImage != null) {
      // Fall back to scaling if path isn't available
      BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = scaledImage.createGraphics();
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.drawImage(svgImage, 0, 0, width, height, null);
      g2d.dispose();
      svgImage = scaledImage;
      repaint();
    }
  }

  @Override
  public boolean contains(int x, int y) {
    // Update the hit detection to consider the borderRadius
    if (label == null || label.isEmpty()) {
      if (borderRadius < 0) {
        // Circular hit detection
        Ellipse2D ellipse = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        return ellipse.contains(x, y);
      } else {
        // Rounded rectangle hit detection
        RoundRectangle2D roundRect =
            new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);
        return roundRect.contains(x, y);
      }
    } else {
      // With label - button has icon area and label area
      if (y > iconDiameter && y < getHeight() && x >= 0 && x <= getWidth()) {
        // Click in label area
        return true;
      }

      if (borderRadius < 0) {
        // Circular icon area hit detection
        Ellipse2D iconEllipse = new Ellipse2D.Float(0, 0, getWidth(), iconDiameter);
        return iconEllipse.contains(x, y);
      } else {
        // Rounded rectangle icon area hit detection
        RoundRectangle2D iconRoundRect =
            new RoundRectangle2D.Float(0, 0, getWidth(), iconDiameter, borderRadius, borderRadius);
        return iconRoundRect.contains(x, y);
      }
    }
  }
}