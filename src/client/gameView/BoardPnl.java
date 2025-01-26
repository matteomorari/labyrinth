package client.gameView;

import javax.swing.*;

import core.CardType;
import core.Labyrinth;
import core.Player;
import core.Card;

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
  // TODO: use controller
  private Labyrinth model;
  private ArrayList<ArrayList<Card>> board;

  public BoardPnl() {
    setLayout(null);
    addMouseListener(this);
    arrowBoundsList = new ArrayList<Shape>();
    this.model = new Labyrinth();
    this.model.addPlayer(new Player());
    this.model.addPlayer(new Player());
    model.initGame();
    this.board = model.getBoard();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(Color.LIGHT_GRAY); // bg color
    int size = Math.min(getWidth(), getHeight());
    // center the board
    int x = (getWidth() - size) / 2;
    int y = (getHeight() - size) / 2;
    g2.fillRoundRect(x, y, size, size, BOARD_ARC_RADIUS, BOARD_ARC_RADIUS);

    // Draw the board
    int cellSize = (size - PADDING * 2) / BOARD_DIMENSION;
    x += PADDING;
    y += PADDING;
    for (int i = 0; i < BOARD_DIMENSION; i++) {
      for (int j = 0; j < BOARD_DIMENSION; j++) {
        Card card = board.get(i).get(j);
        ImageCntrl imageCntrl = ImageCntrl.valueOf("CARD_" + card.getType().name());
        int angles = card.getOrientation().ordinal() * 90;
        CardImage cardImage = new CardImage(imageCntrl).rotate(angles);
        cardImage.paintComponent(g2, x + i * cellSize, y + j * cellSize, cellSize, cellSize);

        // paint the card's goal
        if (card.getGoal() != null) {
          ImageCntrl goalImageCntrl = ImageCntrl.valueOf("GOAL_" + card.getGoal().name());
          CardImage goalImage = new CardImage(goalImageCntrl).rotate(angles);
          goalImage.paintComponent(g2, x + i * cellSize + cellSize / 4, y + j * cellSize + cellSize / 4, cellSize / 2,
              cellSize / 2);
        }

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

    // Draw the players
    g2.setColor(Color.RED);
    for (Player player : model.getPlayers()) {
      int[] position = player.getPosition();
      g2.fillOval(x + position[0] * cellSize + cellSize / 3, y + position[1] * cellSize + cellSize / 3, cellSize / 3,
        cellSize / 3);  
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
        System.out.println("Arrow clicked!: " + (int) Math.abs(col) + ", " + (int) Math.abs(row));
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