package it.unibs.pajc.labyrinth.client.gameView;

import it.unibs.pajc.labyrinth.client.animation.Animatable;
import it.unibs.pajc.labyrinth.client.animation.Animator;
import it.unibs.pajc.labyrinth.client.animation.EasingFunction;
import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.LabyrinthController;
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
  private static final long ANIMATION_DURATION = 600;

  private int boardSize;
  private ArrayList<ArrayList<Card>> board;
  private LabyrinthController controller;
  private Player currentPlayer;
  private ArrayList<Position> lastPlayerMovedPath;
  private ArrayList<Shape> arrowBoundsList;
  private int cellSize;
  private boolean cardAnimationInProgress = false;
  private boolean playerPositionInProgress = false;
  private Position playerAnimationPreviousPoint;
  private Position playerAnimationFuturePoint;
  private Position lastCardInsertPosition;
  private int animationOffset = 0;
  private Animator animator;

  public BoardPnl(LabyrinthController controller) {
    setLayout(null);
    addMouseListener(this);
    this.controller = controller;
    this.animator =
        new Animator(this, ANIMATION_DURATION, EasingFunction.LINEAR, this::endAnimation);
    this.arrowBoundsList = new ArrayList<>();
    this.lastCardInsertPosition = new Position();
    this.lastPlayerMovedPath = new ArrayList<>();

    boardSize = controller.getBoardSize();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    arrowBoundsList.clear();

    updateData();
    checkNewAnimation();

    drawBackground(g2);
    drawBoard(g2);
    drawPlayers(g2);
  }

  private void updateData() {
    this.currentPlayer = controller.getCurrentPlayer();
    this.board = controller.getBoard();
  }

  private void checkNewAnimation() {
    // TODO: fix animation concurrency due to bot move
    // if (cardAnimationInProgress || playerPositionInProgress) {
    //   return;
    // }

    if (controller.hasCurrentPlayerInserted()) {
      controller.setHasCurrentPlayerInserted(false);
      lastCardInsertPosition = controller.lastInsertedCardPosition();
      fireCardInsertAnimation();
    }

    if (controller.hasCurrentPlayerMoved()) {
      controller.setHasCurrentPlayerMoved(false);
      lastPlayerMovedPath = controller.getLastPlayerMovedPath();
      firePlayerMoveAnimation();
    }
  }

  // TODO: change name firePlayerMoveAnimation and initializeAnimation
  private void firePlayerMoveAnimation() {
    startAnimation();
    playerAnimationPreviousPoint = lastPlayerMovedPath.get(0);
    playerAnimationFuturePoint = lastPlayerMovedPath.get(1);
    playerPositionInProgress = true;
  }

  private void fireCardInsertAnimation() {
    cardAnimationInProgress = true;
    startAnimation();
  }

  private void startAnimation() {
    animator.initializeAnimation(new int[] {0}, new int[] {cellSize}).start();
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
    cellSize = (size - PADDING * 2) / boardSize;
    int initialXPosition = (getWidth() - size) / 2 + PADDING;
    int initialYPosition = (getHeight() - size) / 2 + PADDING;

    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {
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
      int[] newPosition = getCardAnimationPosition(posX, posY, card);
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
    if (i == 0 && j != 0 && j != boardSize - 1) {
      paintArrow(
          g2,
          initialXPosition + i * cellSize - (PADDING - cellSize / 4) / 2,
          initialYPosition + j * cellSize + cellSize / 2,
          cellSize / 2,
          (float) Math.toRadians(-90));
    }
    if (i == boardSize - 1 && j != 0 && j != boardSize - 1) {
      paintArrow(
          g2,
          initialXPosition + i * cellSize + cellSize + (PADDING - cellSize / 4) / 2,
          initialYPosition + j * cellSize + cellSize / 2,
          cellSize / 2,
          (float) Math.toRadians(90));
    }
    if (j == 0 && i != 0 && i != boardSize - 1) {
      paintArrow(
          g2,
          initialXPosition + i * cellSize + cellSize / 2,
          initialYPosition + j * cellSize - (PADDING - cellSize / 4) / 2,
          cellSize / 2,
          (float) Math.toRadians(0));
    }
    if (j == boardSize - 1 && i != 0 && i != boardSize - 1) {
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

    for (Player player : controller.getPlayers()) {
      g2.setColor(player.getColor());
      int[] playerPosition = getPlayerAnimationPosition(player, initialXPosition, initialYPosition);
      g2.fillOval(
          playerPosition[0] + cellSize / 3,
          playerPosition[1] + cellSize / 3,
          cellSize / 3,
          cellSize / 3);
    }
  }

  private int[] getPlayerAnimationPosition(
      Player player, int initialXPosition, int initialYPosition) {
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
            getCardAnimationPosition(
                posX,
                posY,
                board.get(player.getPosition().getRow()).get(player.getPosition().getCol()));
        posX = newPosition[0];
        posY = newPosition[1];
      }
    }

    return new int[] {posX, posY};
  }

  private int[] getCardAnimationPosition(int posX, int posY, Card card) {
    // TODO: improve
    int row = lastCardInsertPosition.getRow();
    int col = lastCardInsertPosition.getCol();
    int cardRow = card.getPosition().getRow();
    int cardCol = card.getPosition().getCol();

    if (row == 0 && cardCol == col) {
      posY += animationOffset - cellSize;
    } else if (row == boardSize - 1 && cardCol == col) {
      posY -= animationOffset - cellSize;
    } else if (col == 0 && cardRow == row) {
      posX += animationOffset - cellSize;
    } else if (col == boardSize - 1 && cardRow == row) {
      posX -= animationOffset - cellSize;
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

    if (isClickOnValidCell(e, xFirstCard, yFirstCard, row, col)) {
      handlePlayerMove(row, col);
    } else {
      handleArrowClick(e, row, col);
    }
  }

  private boolean isClickOnValidCell(
      MouseEvent e, int xFirstCard, int yFirstCard, int row, int col) {
    return e.getPoint().x > xFirstCard
        && e.getPoint().y > yFirstCard
        && row < boardSize
        && col < boardSize;
  }

  private void handlePlayerMove(int row, int col) {
    controller.movePlayer(row, col);
  }

  private void handleArrowClick(MouseEvent e, int row, int col) {
    for (Shape arrowBounds : arrowBoundsList) {
      if (arrowBounds.contains(e.getPoint())) {
        row = Math.min(Math.abs(row), boardSize - 1);
        col = Math.min(Math.abs(col), boardSize - 1);
        System.out.println("Arrow clicked at row: " + row + ", col: " + col);
        controller.insertCard(new Position(row, col));
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
      int index = lastPlayerMovedPath.indexOf(playerAnimationFuturePoint);
      if (index < lastPlayerMovedPath.size() - 1) {
        animator.initializeAnimation(new int[] {0}, new int[] {(int) cellSize}).start();
        playerAnimationPreviousPoint = playerAnimationFuturePoint;
        playerAnimationFuturePoint = lastPlayerMovedPath.get(index + 1);
      } else {
        playerPositionInProgress = false;
      }
    }
    animationOffset = 0;
  }
}
