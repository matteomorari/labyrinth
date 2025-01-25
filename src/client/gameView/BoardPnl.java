package client.gameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;

public class BoardPnl extends JPanel implements MouseListener {
  private static final int BOARD_ARC_RADIUS = 30;
  private static final int PADDING = 40;

  private static final int BOARD_DIMENSION = 7; // TODO: move to model
  private ArrayList<Shape> arrowBoundsList;

  public BoardPnl() {
    setLayout(null);
    addMouseListener(this);
    arrowBoundsList = new ArrayList<Shape>();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(Color.LIGHT_GRAY);
    int size = Math.min(getWidth(), getHeight());
    // center the board
    int x = (getWidth() - size) / 2;
    int y = (getHeight() - size) / 2;
    g2.fillRoundRect(x, y, size, size, BOARD_ARC_RADIUS, BOARD_ARC_RADIUS); // Adjust the arc width and height as needed

    // Draw the board
    int cellSize = (size - PADDING * 2) / BOARD_DIMENSION;
    x += PADDING;
    y += PADDING;
    for (int i = 0; i < BOARD_DIMENSION; i++) {
      for (int j = 0; j < BOARD_DIMENSION; j++) {
        int[] angles = { 0, 90, 180, 270 };
        int randomAngle = angles[(int) (Math.random() * angles.length)];

        int randomNumber = 1 + (int) (Math.random() * 3);

        ImageCntrl imageCntrl;
        if (randomNumber == 1) {
          imageCntrl = ImageCntrl.CARD_L;
        } else if (randomNumber == 2) {
          imageCntrl = ImageCntrl.CARD_I;
        } else {
          imageCntrl = ImageCntrl.CARD_T;
        }

        Card card = new Card(imageCntrl).rotate(randomAngle);
        // card.draw(g2, x + i * cellSize, y + j * cellSize, cellSize);
        card.paintComponent(g2, x + i * cellSize, y + j * cellSize, cellSize, cellSize);
        this.add(card);

        // Image image = imageCntrl.rotateImage(
        // randomAngle).getImage().getScaledInstance(cellSize, cellSize,
        // Image.SCALE_SMOOTH);
        // g2.drawImage(image, x + i * cellSize, y + j * cellSize, this);

        // paint arrow around the board
        // TODO: fix arrow position
        g2.setColor(Color.BLUE);
        if (i == 0 && j != 0 && j != BOARD_DIMENSION - 1) {
          paintArrow(g2, x + i * cellSize - (PADDING - cellSize / 4) / 2, y + j * cellSize + cellSize / 2, cellSize / 2,
              (float) Math.toRadians(-90));
        }
        if (i == BOARD_DIMENSION - 1 && j != 0 && j != BOARD_DIMENSION - 1) {
          paintArrow(g2, x + i * cellSize + cellSize + (PADDING - cellSize / 4) / 2, y + j * cellSize + cellSize / 2,
              cellSize / 2, (float) Math.toRadians(90));
        }
        if (j == 0 && i != 0 && i != BOARD_DIMENSION - 1) {
          paintArrow(g2, x + i * cellSize + cellSize / 2, y + j * cellSize - (PADDING - cellSize / 4) / 2, cellSize / 2,
              (float) Math.toRadians(0));
        }
        if (j == BOARD_DIMENSION - 1 && i != 0 && i != BOARD_DIMENSION - 1) {
          paintArrow(g2, x + i * cellSize + cellSize / 2, y + j * cellSize + cellSize + (PADDING - cellSize / 4) / 2,
              cellSize / 2, (float) Math.toRadians(180));
        }
      }
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    int size = Math.min(getWidth(), getHeight());
    float cellSize = (size - PADDING * 2) / BOARD_DIMENSION;
    int x = (getWidth() - size) / 2 + PADDING;
    int y = (getHeight() - size) / 2 + PADDING;

    float row = (e.getY() - y) / cellSize;
    float col = (e.getX() - x) / cellSize;

    if (row >= 0 && row < BOARD_DIMENSION && col >= 0 && col < BOARD_DIMENSION) {
      System.out.println("Image clicked at row: " + (int) row + ", col: " + (int) col);
    }

    for (Shape arrowBounds : arrowBoundsList) {
      if (arrowBounds.contains(e.getPoint())) {
        System.out.println("Arrow clicked!: " + (int)Math.abs(col) + ", " + (int)Math.abs(row));
        // Handle arrow click
        break;
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  public void paintArrow(Graphics2D g2, int x, int y, int size, float angolo) {
    int[] xPoints = { 0, 1, -1, 0 };
    int[] yPoints = { 2, -2, -2, 2 };
    int nPoints = xPoints.length;

    // Create the Polygon object
    Polygon arrow = new Polygon(xPoints, yPoints, nPoints);
    Shape shape = new Area(arrow);

    AffineTransform at = new AffineTransform();
    at.translate(x, y);
    at.rotate(angolo);
    at.scale(size / 6.0, size / 6.0);
    shape = at.createTransformedShape(shape);

    arrowBoundsList.add(shape);
    g2.fill(shape);
  }
}