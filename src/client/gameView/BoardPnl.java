package client.gameView;

import client.animation.Animatable;
import client.animation.Animator;
import client.animation.EasingFunction;
import core.Card;
import core.Labyrinth;
import core.Player;
import core.utility.Position;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.ArrayList;
import javax.swing.JPanel;

public class BoardPnl extends JPanel implements MouseListener, Animatable {
  private static final int BOARD_ARC_RADIUS = 30;
  private static final int PADDING = 40;
  private static final int BOARD_DIMENSION = 7; // TODO: move to model
  private static final long ANIMATION_DURATION = 600;

  private ArrayList<Shape> arrowBoundsList;
  private int cellSize;
  private boolean cardAnimationInProgress = false;
  private boolean playerPositionInProgress = false;
  private Position playerAnimationPreviousPoint;
  private Position playerAnimationFuturePoint;
  private ArrayList<Position> playerAnimationPath;
  private Position lastCardInsertPosition;
  private int animationOffset = 0;
  private Animator animator;
  private Player currentPlayer;
  // TODO: use controller
  private Labyrinth model;
  private ArrayList<ArrayList<Card>> board;

  public BoardPnl() {
    setLayout(null);
    addMouseListener(this);
    this.animator =
        new Animator(this, ANIMATION_DURATION, EasingFunction.LINEAR, this::endAnimation);
    this.arrowBoundsList = new ArrayList<>();
    this.lastCardInsertPosition = new Position();



    // TODO: move to controller
    this.model = new Labyrinth();
    Player player1 = new Player();
    player1.setColor(Color.RED);
    this.model.addPlayer(player1);
    Player player2 = new Player();
    player2.setColor(Color.YELLOW);
    this.model.addPlayer(player2);
    model.initGame();
    this.board = model.getBoard();
    this.currentPlayer = model.getCurrentPlayer();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    arrowBoundsList.clear();

    g2.setColor(Color.LIGHT_GRAY); // bg color
    int size = Math.min(getWidth(), getHeight());
    // center the board
    int initialXPosition = (getWidth() - size) / 2;
    int initialYPosition = (getHeight() - size) / 2;
    g2.fillRoundRect(
        initialXPosition, initialYPosition, size, size, BOARD_ARC_RADIUS, BOARD_ARC_RADIUS);

    // Draw the board
    cellSize = (size - PADDING * 2) / BOARD_DIMENSION;
    initialXPosition += PADDING;
    initialYPosition += PADDING;
    for (int i = 0; i < BOARD_DIMENSION; i++) {
      for (int j = 0; j < BOARD_DIMENSION; j++) {
        Card card = board.get(i).get(j);
        // calc card position
        int posX = initialXPosition + j * cellSize;
        int posY = initialYPosition + i * cellSize;
        if (cardAnimationInProgress) {
          int[] newPosition = calcCardAnimationDrawPosition(posX, posY, card);
          posX = newPosition[0];
          posY = newPosition[1];
        }

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

        // paint arrow around the board
        // TODO: fix arrow position
        g2.setColor(Color.BLUE);
        if (i == 0 && j != 0 && j != BOARD_DIMENSION - 1) {
          paintArrow(
              g2,
              initialXPosition + i * cellSize - (PADDING - cellSize / 4) / 2,
              initialYPosition + j * cellSize + cellSize / 2,
              cellSize / 2,
              (float) Math.toRadians(-90));
        }
        if (i == BOARD_DIMENSION - 1 && j != 0 && j != BOARD_DIMENSION - 1) {
          paintArrow(
              g2,
              initialXPosition + i * cellSize + cellSize + (PADDING - cellSize / 4) / 2,
              initialYPosition + j * cellSize + cellSize / 2,
              cellSize / 2,
              (float) Math.toRadians(90));
        }
        if (j == 0 && i != 0 && i != BOARD_DIMENSION - 1) {
          paintArrow(
              g2,
              initialXPosition + i * cellSize + cellSize / 2,
              initialYPosition + j * cellSize - (PADDING - cellSize / 4) / 2,
              cellSize / 2,
              (float) Math.toRadians(0));
        }
        if (j == BOARD_DIMENSION - 1 && i != 0 && i != BOARD_DIMENSION - 1) {
          paintArrow(
              g2,
              initialXPosition + i * cellSize + cellSize / 2,
              initialYPosition + j * cellSize + cellSize + (PADDING - cellSize / 4) / 2,
              cellSize / 2,
              (float) Math.toRadians(180));
        }
      }
    }

    // Draw the player
    for (Player player : model.getPlayers()) {
      g2.setColor(player.getColor());
      int posX = initialXPosition;
      int posY = initialYPosition;
      if (playerPositionInProgress && player.equals(currentPlayer)) {
        posX += playerAnimationPreviousPoint.getCol() * cellSize;
        posY += playerAnimationPreviousPoint.getRow() * cellSize;
        if (playerAnimationPreviousPoint.getRow() == playerAnimationFuturePoint.getRow()) {
          int horizontalDirection =
              (playerAnimationFuturePoint.getCol() - playerAnimationPreviousPoint.getCol()) > 0 ? 1 : -1;
          posX += horizontalDirection * animationOffset;
        }
        if (playerAnimationPreviousPoint.getCol() == playerAnimationFuturePoint.getCol()) {
          int verticalDirection =
              (playerAnimationFuturePoint.getRow() - playerAnimationPreviousPoint.getRow()) > 0 ? 1 : -1;
          posY += verticalDirection * animationOffset;
        }
      } else {
        posX += player.getPosition().getCol() * cellSize;
        posY += player.getPosition().getRow() * cellSize;
        if (cardAnimationInProgress) {
          int[] newPosition =
              calcCardAnimationDrawPosition(
                  posX, posY, board.get(player.getPosition().getRow()).get(player.getPosition().getCol()));
          posX = newPosition[0];
          posY = newPosition[1];
        }
      }
      g2.fillOval(posX + cellSize / 3, posY + cellSize / 3, cellSize / 3, cellSize / 3);
    }
  }

  private int[] calcCardAnimationDrawPosition(int posX, int posY, Card card) {
    if (lastCardInsertPosition.getRow() == 0
        && card.getPosition().getCol() == lastCardInsertPosition.getCol()) {
      posY -= animationOffset;
    } else if (lastCardInsertPosition.getRow() == BOARD_DIMENSION - 1
        && card.getPosition().getCol() == lastCardInsertPosition.getCol()) {
      posY += animationOffset;
    } else if (lastCardInsertPosition.getCol() == 0
        && card.getPosition().getRow() == lastCardInsertPosition.getRow()) {
      posX -= animationOffset;
    } else if (lastCardInsertPosition.getCol() == BOARD_DIMENSION - 1
        && card.getPosition().getRow() == lastCardInsertPosition.getRow()) {
      posX += animationOffset;
    }
    return new int[] {posX, posY};
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (cardAnimationInProgress || playerPositionInProgress) {
      return;
    }

    int size = Math.min(getWidth(), getHeight());
    int xFirstCard = (getWidth() - size) / 2 + PADDING;
    int yFirstCard = (getHeight() - size) / 2 + PADDING;

    int row = (e.getY() - yFirstCard) / cellSize;
    int col = (e.getX() - xFirstCard) / cellSize;

    // we use e.getPoint().x and e.getPoint().y > 0 instead of row and col because
    // the row and col variable are int, so a -0.2 is considered as 0, and the click would be
    // considered valid
    if (e.getPoint().x > xFirstCard
        && e.getPoint().y > yFirstCard
        && row < BOARD_DIMENSION
        && col < BOARD_DIMENSION) {
      System.out.println("Image clicked at row: " + row + ", col: " + col);
      playerAnimationPath = model.movePlayer(row, col);
      if (playerAnimationPath.isEmpty()) {
        return;
      }
      playerPositionInProgress = true;
      animator.initializeAnimation(new int[] {0}, new int[] {cellSize}).start();
      playerAnimationPreviousPoint = playerAnimationPath.get(0);
      playerAnimationFuturePoint = playerAnimationPath.get(1);
      return;
    }

    for (Shape arrowBounds : arrowBoundsList) {
      if (arrowBounds.contains(e.getPoint())) {
        row = Math.min(Math.abs(row), BOARD_DIMENSION - 1);
        col = Math.min(Math.abs(col), BOARD_DIMENSION - 1);
        System.out.println("Arrow clicked at row: " + row + ", col: " + col);
        model.insertCard(new Position(row, col));
        cardAnimationInProgress = true;
        animator.initializeAnimation(new int[] {cellSize}, new int[] {0}).start();
        lastCardInsertPosition.setPosition(row, col);
        return;
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {}

  @Override
  public void mouseReleased(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  public void paintArrow(Graphics2D g2, int x, int y, int size, float angolo) {
    int[] xPoints = {0, 1, -1, 0};
    int[] yPoints = {2, -2, -2, 2};
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

  private void endAnimation() {
    cardAnimationInProgress = false;

    if (playerPositionInProgress) {
      int index = playerAnimationPath.indexOf(playerAnimationFuturePoint);
      if (index < playerAnimationPath.size() - 1) {
        animationOffset = 0;
        animator.initializeAnimation(new int[] {0}, new int[] {(int) cellSize}).start();
        playerAnimationPreviousPoint = playerAnimationFuturePoint;
        playerAnimationFuturePoint = playerAnimationPath.get(index + 1);
      } else {
        playerPositionInProgress = false;
      }
    }
  }
}
