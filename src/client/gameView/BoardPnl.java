package client.gameView;

import javax.swing.*;

import client.animation.Animatable;
import client.animation.Animator;
import client.animation.EasingFunction;
import core.Labyrinth;
import core.Player;
import core.Card;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;

public class BoardPnl extends JPanel implements MouseListener, Animatable {
  private static final int BOARD_ARC_RADIUS = 30;
  private static final int PADDING = 40;
  private static final int BOARD_DIMENSION = 7; // TODO: move to model

  private ArrayList<Shape> arrowBoundsList;
  private boolean cardAnimationInProgress = false;
  private Point lastCardInsertPosition = new Point();
  private int animationOffset = 0;
  private Animator animator;
  // TODO: use controller
  private Labyrinth model;
  private ArrayList<ArrayList<Card>> board;

  public BoardPnl() {
    setLayout(null);
    addMouseListener(this);
    this.animator = new Animator(this, 1500, EasingFunction.EASE_OUT_BOUNCE, () -> cardAnimationInProgress = false);
    arrowBoundsList = new ArrayList<Shape>();
    this.model = new Labyrinth();
    // TODO: move to controller
    Player player1 = new Player();
    player1.setColor(Color.RED);
    this.model.addPlayer(player1);
    Player player2 = new Player();
    player2.setColor(Color.YELLOW);
    this.model.addPlayer(player2);
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
    int initialXPosition = (getWidth() - size) / 2;
    int initialYPosition = (getHeight() - size) / 2;
    g2.fillRoundRect(initialXPosition, initialYPosition, size, size, BOARD_ARC_RADIUS, BOARD_ARC_RADIUS);

    // Draw the board
    int cellSize = (size - PADDING * 2) / BOARD_DIMENSION;
    initialXPosition += PADDING;
    initialYPosition += PADDING;
    for (int i = 0; i < BOARD_DIMENSION; i++) {
      for (int j = 0; j < BOARD_DIMENSION; j++) {
        // calc card position
        int posX = initialXPosition + j * cellSize;
        int posY = initialYPosition + i * cellSize;
        if (cardAnimationInProgress) {
          if (lastCardInsertPosition.getX() == 0 && j == lastCardInsertPosition.getY()) {
            posY -= animationOffset;
          } else if (lastCardInsertPosition.getX() == BOARD_DIMENSION - 1 && j == lastCardInsertPosition.getY()) {
            posY += animationOffset;
          } else if (lastCardInsertPosition.getY() == 0 && i == lastCardInsertPosition.getX()) {
            posX -= animationOffset;
          } else if (lastCardInsertPosition.getY() == BOARD_DIMENSION - 1 && i == lastCardInsertPosition.getX()) {
            posX += animationOffset;
          }
        }

        Card card = board.get(i).get(j);
        ImageCntrl imageCntrl = ImageCntrl.valueOf("CARD_" + card.getType().name());
        int angles = card.getOrientation().ordinal() * 90;
        CardImage cardImage = new CardImage(imageCntrl, g2).rotate(angles);
        cardImage.draw(posX, posY, cellSize, cellSize);

        // paint the card's goal
        if (card.getGoal() != null) {
          ImageCntrl goalImageCntrl = ImageCntrl.valueOf("GOAL_" + card.getGoal().name());
          CardImage goalImage = new CardImage(goalImageCntrl, g2).rotate(angles);
          goalImage.draw(posX + cellSize / 4, posY + cellSize / 4, cellSize / 2, cellSize / 2);
        }

        // Draw the players
        for (Player player : card.getPlayers()) {
          g2.setColor(player.getColor());
          g2.fillOval(posX + cellSize / 3,  posY + cellSize / 3, cellSize / 3, cellSize / 3);
        }

        // paint arrow around the board
        // TODO: fix arrow position
        g2.setColor(Color.BLUE);
        if (i == 0 && j != 0 && j != BOARD_DIMENSION - 1) {
          paintArrow(g2, initialXPosition + i * cellSize - (PADDING - cellSize / 4) / 2,
              initialYPosition + j * cellSize + cellSize / 2, cellSize / 2,
              (float) Math.toRadians(-90));
        }
        if (i == BOARD_DIMENSION - 1 && j != 0 && j != BOARD_DIMENSION - 1) {
          paintArrow(g2, initialXPosition + i * cellSize + cellSize + (PADDING - cellSize / 4) / 2,
              initialYPosition + j * cellSize + cellSize / 2,
              cellSize / 2, (float) Math.toRadians(90));
        }
        if (j == 0 && i != 0 && i != BOARD_DIMENSION - 1) {
          paintArrow(g2, initialXPosition + i * cellSize + cellSize / 2,
              initialYPosition + j * cellSize - (PADDING - cellSize / 4) / 2, cellSize / 2,
              (float) Math.toRadians(0));
        }
        if (j == BOARD_DIMENSION - 1 && i != 0 && i != BOARD_DIMENSION - 1) {
          paintArrow(g2, initialXPosition + i * cellSize + cellSize / 2,
              initialYPosition + j * cellSize + cellSize + (PADDING - cellSize / 4) / 2,
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
      model.movePlayer((int) row, (int) col);
      repaint();
      return;
    }

    for (Shape arrowBounds : arrowBoundsList) {
      if (arrowBounds.contains(e.getPoint())) {
        row = Math.min((int) Math.abs(row), BOARD_DIMENSION - 1);
        col = Math.min((int) Math.abs(col), BOARD_DIMENSION - 1);
        System.out.println("Arrow clicked at row: " + (int) row + ", col: " + (int) col);
        model.insertCard((int) row, (int) col);
        cardAnimationInProgress = true;
        animator.initializeAnimation(new int[] { (int) cellSize }, new int[] { 0 }).start();
        lastCardInsertPosition.setLocation((int) row, (int) col);
        // repaint();
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

  @Override
  public void updateAnimation(int[] values) {
    animationOffset = values[0];
    repaint();
  }
}