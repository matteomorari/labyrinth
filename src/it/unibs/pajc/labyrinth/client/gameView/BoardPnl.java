package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.client.animation.Animatable;
import it.unibs.pajc.labyrinth.client.animation.Animator;
import it.unibs.pajc.labyrinth.client.animation.EasingFunction;
import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.utility.Position;
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

    drawBackground(g2);
    drawBoard(g2);
    drawPlayers(g2);
  }

  private void drawBackground(Graphics2D g2) {
    g2.setColor(Color.LIGHT_GRAY); // bg color
    int size = Math.min(getWidth(), getHeight());
    int initialXPosition = (getWidth() - size) / 2;
    int initialYPosition = (getHeight() - size) / 2;
    g2.fillRoundRect(
        initialXPosition, initialYPosition, size, size, BOARD_ARC_RADIUS, BOARD_ARC_RADIUS);
  }

  private void drawBoard(Graphics2D g2) {
    int size = Math.min(getWidth(), getHeight());
    cellSize = (size - PADDING * 2) / BOARD_DIMENSION;
    int initialXPosition = (getWidth() - size) / 2 + PADDING;
    int initialYPosition = (getHeight() - size) / 2 + PADDING;

    for (int i = 0; i < BOARD_DIMENSION; i++) {
      for (int j = 0; j < BOARD_DIMENSION; j++) {
        drawCard(g2, i, j, initialXPosition, initialYPosition);
        drawArrows(g2, i, j, initialXPosition, initialYPosition);
      }
    }
  }

  private void drawCard(Graphics2D g2, int i, int j, int initialXPosition, int initialYPosition) {
    Card card = board.get(i).get(j);
    int posX = initialXPosition + j * cellSize;
    int posY = initialYPosition + i * cellSize;
    if (cardAnimationInProgress) {
      int[] newPosition = determineCardAnimationPosition(posX, posY, card);
      posX = newPosition[0];
      posY = newPosition[1];
    }

    ImageCntrl imageCntrl = ImageCntrl.valueOf("CARD_" + card.getType().name());
    int angles = card.getOrientation().ordinal() * 90;
    CardImage cardImage = new CardImage(imageCntrl, g2).rotate(angles);
    cardImage.draw(posX, posY, cellSize, cellSize);

    if (card.getGoal() != null) {
      ImageCntrl goalImageCntrl = ImageCntrl.valueOf("GOAL_" + card.getGoal().name());
      CardImage goalImage = new CardImage(goalImageCntrl, g2).rotate(angles);
      goalImage.draw(posX + cellSize / 4, posY + cellSize / 4, cellSize / 2, cellSize / 2);
    }
  }

  private void drawArrows(Graphics2D g2, int i, int j, int initialXPosition, int initialYPosition) {
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

  private void drawPlayers(Graphics2D g2) {
    int size = Math.min(getWidth(), getHeight());
    int initialXPosition = (getWidth() - size) / 2 + PADDING;
    int initialYPosition = (getHeight() - size) / 2 + PADDING;

    for (Player player : model.getPlayers()) {
      g2.setColor(player.getColor());
      int[] playerPosition = calculatePlayerPosition(player, initialXPosition, initialYPosition);
      g2.fillOval(
          playerPosition[0] + cellSize / 3,
          playerPosition[1] + cellSize / 3,
          cellSize / 3,
          cellSize / 3);
    }
  }

  private int[] calculatePlayerPosition(Player player, int initialXPosition, int initialYPosition) {
    int posX = initialXPosition;
    int posY = initialYPosition;

    if (playerPositionInProgress && player.equals(currentPlayer)) {
      posX += playerAnimationPreviousPoint.getCol() * cellSize;
      posY += playerAnimationPreviousPoint.getRow() * cellSize;
      posX +=
          Integer.compare(
                  playerAnimationFuturePoint.getCol(), playerAnimationPreviousPoint.getCol())
              * animationOffset;
      posY +=
          Integer.compare(
                  playerAnimationFuturePoint.getRow(), playerAnimationPreviousPoint.getRow())
              * animationOffset;
    } else {
      posX += player.getPosition().getCol() * cellSize;
      posY += player.getPosition().getRow() * cellSize;
      if (cardAnimationInProgress) {
        int[] newPosition =
            determineCardAnimationPosition(
                posX,
                posY,
                board.get(player.getPosition().getRow()).get(player.getPosition().getCol()));
        posX = newPosition[0];
        posY = newPosition[1];
      }
    }

    return new int[] {posX, posY};
  }

  private int[] determineCardAnimationPosition(int posX, int posY, Card card) {
    // TODO: improve
    int row = lastCardInsertPosition.getRow();
    int col = lastCardInsertPosition.getCol();
    int cardRow = card.getPosition().getRow();
    int cardCol = card.getPosition().getCol();

    if (row == 0 && cardCol == col) {
      posY -= animationOffset;
    } else if (row == BOARD_DIMENSION - 1 && cardCol == col) {
      posY += animationOffset;
    } else if (col == 0 && cardRow == row) {
      posX -= animationOffset;
    } else if (col == BOARD_DIMENSION - 1 && cardRow == row) {
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

    if (isValidClick(e, xFirstCard, yFirstCard, row, col)) {
      handlePlayerMove(row, col);
    } else {
      handleArrowClick(e, row, col);
    }
  }

  private boolean isValidClick(MouseEvent e, int xFirstCard, int yFirstCard, int row, int col) {
    return e.getPoint().x > xFirstCard
        && e.getPoint().y > yFirstCard
        && row < BOARD_DIMENSION
        && col < BOARD_DIMENSION;
  }

  private void handlePlayerMove(int row, int col) {
    playerAnimationPath = model.movePlayer(row, col);
    if (playerAnimationPath.isEmpty()) {
      return;
    }
    playerPositionInProgress = true;
    animator.initializeAnimation(new int[] {0}, new int[] {cellSize}).start();
    playerAnimationPreviousPoint = playerAnimationPath.get(0);
    playerAnimationFuturePoint = playerAnimationPath.get(1);
  }

  private void handleArrowClick(MouseEvent e, int row, int col) {
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
