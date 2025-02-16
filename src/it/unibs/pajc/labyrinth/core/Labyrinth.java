package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.NodeComparator;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Labyrinth extends BaseModel {
  ArrayDeque<Player> players;
  ArrayList<ArrayList<Card>> board;
  Card availableCard;
  Position lastInsertedCardPosition;
  ArrayList<Position> lastPlayerMovedPath;
  private static final int GOALS_FOR_PLAYER = 2;

  private int boardSize;

  private boolean hasCurrentPlayerMoved = false;
  private boolean hasCurrentPlayerInserted = false;

  public Labyrinth() {
    this(7);
  }

  public Labyrinth(int boardSize) {
    this.players = new ArrayDeque<>();
    this.board = new ArrayList<>();
    this.boardSize = boardSize;
    this.availableCard = null;
    this.lastInsertedCardPosition = null;
    this.lastPlayerMovedPath = new ArrayList<Position>();
  }

  public int getBoardSize() {
    return boardSize;
  }

  public void addPlayer(Player player) {
    int playerCount = players.size();
    if (playerCount > 4) {
      throw new IllegalArgumentException("Only 4 players are allowed");
    }

    switch (playerCount) {
      case 0:
        this.players.add(player);
        player.setStartPosition(0, 0);
        break;
      case 1:
        this.players.add(player);
        player.setStartPosition(boardSize - 1, boardSize - 1);
        break;
      case 2:
        this.players.add(player);
        player.setStartPosition(0, boardSize - 1);
        break;
      case 3:
        this.players.add(player);
        player.setStartPosition(boardSize - 1, 0);
        break;
      default:
        // This should never happen because of the check above
        throw new IllegalStateException("Unexpected player count: " + playerCount);
    }
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
  }

  public Player getCurrentPlayer() {
    return this.players.peek();
  }

  public Player nextPlayer() {
    this.players.add(this.players.poll());
    return this.getCurrentPlayer();
  }

  public void initGame() {
    this.initBoard();

    // shuffle goals
    List<Goal> goalsList = Arrays.asList(Goal.values());
    Collections.shuffle(goalsList);

    // assign goals for each player
    for (Player player : this.players) {
      for (int i = 0; i < GOALS_FOR_PLAYER; i++) {
        player.addGoal(goalsList.get(i));
      }
    }

    // assign goals to the card
    // TODO: improve
    for (Goal goal : goalsList) {
      boolean isAssigned = false;
      while (!isAssigned) {
        int x = (int) (Math.random() * this.boardSize);
        int y = (int) (Math.random() * this.boardSize);
        Card card = this.board.get(x).get(y);
        if (card.getGoal() == null) {
          card.setGoal(goal);
          isAssigned = true;
        }
      }
    }

    this.fireChangeListener();
  }

  public ArrayList<ArrayList<Card>> initBoard() {
    this.board.clear();

    for (int i = 0; i < this.boardSize; i++) {
      ArrayList<Card> row = new ArrayList<Card>();
      for (int j = 0; j < this.boardSize; j++) {
        // the card in the corner must by of the type L
        Card card = null;
        if ((i == 0 && j == 0)
            || (i == 0 && j == this.boardSize - 1)
            || (i == this.boardSize - 1 && j == 0)
            || (i == this.boardSize - 1 && j == this.boardSize - 1)) {
          card = new Card(CardType.L);
        } else {
          card = this.createRandomCard();
        }
        row.add(card);
        card.setPosition(new Position(i, j));
      }
      this.board.add(row);
    }

    // create the
    this.availableCard = this.createRandomCard();
    this.availableCard.setPosition(-1, -1);

    // set the right orientation for the corner cards
    this.board.get(0).get(0).rotate();
    this.board.get(this.boardSize - 1).get(0).rotate(2);
    this.board.get(0).get(this.boardSize - 1);
    this.board.get(this.boardSize - 1).get(this.boardSize - 1).rotate(3);

    // set the players to their start card
    for (Player player : this.players) {
      Card card =
          this.board
              .get(player.getStartPosition().getRow())
              .get(player.getStartPosition().getCol());
      card.addPlayer(player);
    }
    return this.board;
  }

  private Card createRandomCard() {
    CardType cardType = CardType.values()[(int) (Math.random() * CardType.values().length)];
    int randomAngle = (int) (Math.random() * Orientation.values().length);
    Card card = new Card(cardType).rotate(randomAngle);
    return card;
  }

  public ArrayDeque<Player> getPlayers() {
    return players;
  }

  public ArrayList<ArrayList<Card>> getBoard() {
    return board;
  }

  public Card getAvailableCard() {
    return availableCard;
  }

  public void setAvailableCard(Card availableCard) {
    this.availableCard = availableCard;
  }

  public void insertCard(Position insertPosition) {
    validatePosition(insertPosition);

    if (this.availableCard == null) {
      throw new IllegalStateException("No available card");
    }

    this.hasCurrentPlayerInserted = true;

    Position endPosition = getOppositePosition(insertPosition);

    // 1 if the cards are move from the top to the bottom or from the left to the right
    // -1 if the cards are move from the bottom to the top or from the right to the left
    // 0 if the row/col is the same and so the relative position won't change in the following cycle
    int rowDirection = Integer.compare(endPosition.getRow(), insertPosition.row);
    int colDirection = Integer.compare(endPosition.getCol(), insertPosition.col);

    Card nextAvailableCard = this.board.get(endPosition.row).get(endPosition.col);
    this.updateNextAvailableCard(nextAvailableCard);

    // move the cards
    moveCards(endPosition, rowDirection, colDirection);

    this.updateCardPosition(availableCard, insertPosition);
    this.availableCard = nextAvailableCard;

    this.lastInsertedCardPosition = insertPosition;
    this.fireChangeListener();
  }

  private void validatePosition(Position insertPosition) {
    if (insertPosition.row < 0
        || insertPosition.row >= this.boardSize
        || insertPosition.col < 0
        || insertPosition.col >= this.boardSize) {
      throw new IllegalArgumentException("Invalid position");
    }

    // you cant insert a card in opposite position of the last inserted card
    if (this.lastInsertedCardPosition != null
        && getOppositePosition(insertPosition).equals(this.lastInsertedCardPosition)) {
      throw new IllegalArgumentException("Illegal move");
    }
  }

  private Position getOppositePosition(Position insertPosition) {
    Position endPosition = new Position();
    if (insertPosition.row == 0) {
      endPosition.setPosition(this.boardSize - 1, insertPosition.col);
    } else if (insertPosition.row == this.boardSize - 1) {
      endPosition.setPosition(0, insertPosition.col);
    } else if (insertPosition.col == 0) {
      endPosition.setPosition(insertPosition.row, this.boardSize - 1);
    } else if (insertPosition.col == this.boardSize - 1) {
      endPosition.setPosition(insertPosition.row, 0);
    }
    return endPosition;
  }

  private void updateNextAvailableCard(Card card) {
    card.setPosition(-1, -1);
    card.shiftPlayersToNewCard(this.availableCard);
  }

  private void moveCards(Position endPosition, int rowDirection, int colDirection) {
    for (int i = 0; i < this.boardSize - 1; i++) {
      int currentRow = endPosition.row - (i + 1) * rowDirection;
      int currentCol = endPosition.col - (i + 1) * colDirection;
      Card cardToMove = this.board.get(currentRow).get(currentCol);
      Position newPosition =
          new Position(endPosition.row - i * rowDirection, endPosition.col - i * colDirection);
      this.updateCardPosition(cardToMove, newPosition);
    }
  }

  private void updateCardPosition(Card card, Position newPosition) {
    this.board.get(newPosition.getRow()).set(newPosition.getCol(), card);
    card.move(newPosition);
  }

  // using Dijkstra's algorithm
  // TODO: https://www.baeldung.com/java-solve-maze
  public ArrayList<Position> findPath(int startRow, int startCol, int endRow, int endCol) {
    PriorityQueue<Card> nodeDistanceQueue = new PriorityQueue<>(new NodeComparator());
    ArrayList<Position> path = new ArrayList<>();
    boolean found = false;

    // if the path is only the start node, then the player is already there
    if (startRow == endRow && startCol == endCol) {
      return path;
    }

    // reset the distance for each card
    for (ArrayList<Card> row : this.board) {
      for (Card card : row) {
        card.resetGraph();
      }
    }

    // initialize the start node
    Card startCard = this.board.get(startRow).get(startCol);
    startCard.setDistance(0);
    startCard.setFrom(null);
    nodeDistanceQueue.add(startCard);

    while (!nodeDistanceQueue.isEmpty() && !found) {
      Card currentNode = nodeDistanceQueue.poll();
      int currentRow = currentNode.getPosition().getRow();
      int currentCol = currentNode.getPosition().getCol();

      if (currentRow == endRow && currentCol == endCol) {
        found = true;
      }

      ArrayList<Orientation> openOrientation = currentNode.getOpenOrientation();

      // for each open orientation check if the neighbor card is also open
      for (Orientation orientation : openOrientation) {
        processNeighbor(currentNode, orientation, nodeDistanceQueue);
      }
    }

    if (found) {
      Card currentCard = this.board.get(endRow).get(endCol);
      while (currentCard.getFrom() != null) {
        path.add(currentCard.getPosition());
        currentCard = (Card) currentCard.getFrom();
      }
      path.add(currentCard.getPosition()); // get also the start node
      Collections.reverse(path);
    }

    return path;
  }

  private void processNeighbor(
      Card currentNode, Orientation orientation, PriorityQueue<Card> nodeDistanceQueue) {
    int currentRow = currentNode.getPosition().getRow();
    int currentCol = currentNode.getPosition().getCol();
    Card neighborCard = null;

    switch (orientation) {
      case NORD:
        if (this.validPosition(currentRow - 1, currentCol)) {
          neighborCard = this.board.get(currentRow - 1).get(currentCol);
          if (neighborCard.isSouthOpen()) {
            updateNeighbor(currentNode, neighborCard, nodeDistanceQueue);
          }
        }
        break;
      case EAST:
        if (this.validPosition(currentRow, currentCol + 1)) {
          neighborCard = this.board.get(currentRow).get(currentCol + 1);
          if (neighborCard.isWestOpen()) {
            updateNeighbor(currentNode, neighborCard, nodeDistanceQueue);
          }
        }
        break;
      case SOUTH:
        if (this.validPosition(currentRow + 1, currentCol)) {
          neighborCard = this.board.get(currentRow + 1).get(currentCol);
          if (neighborCard.isNordOpen()) {
            updateNeighbor(currentNode, neighborCard, nodeDistanceQueue);
          }
        }
        break;
      case WEST:
        if (this.validPosition(currentRow, currentCol - 1)) {
          neighborCard = this.board.get(currentRow).get(currentCol - 1);
          if (neighborCard.isEastOpen()) {
            updateNeighbor(currentNode, neighborCard, nodeDistanceQueue);
          }
        }
        break;
      default:
        break;
    }
  }

  private void updateNeighbor(
      Card currentNode, Card neighborCard, PriorityQueue<Card> nodeDistanceQueue) {
    currentNode.addCardConnected(neighborCard);
    if (neighborCard.getDistance() > currentNode.getDistance() + 1) {
      nodeDistanceQueue.add(neighborCard);
      neighborCard.setDistance(currentNode.getDistance() + 1);
      neighborCard.setFrom(currentNode);
    }
  }

  public boolean validPosition(int row, int col) {
    return row >= 0 && row < this.boardSize && col >= 0 && col < this.boardSize;
  }

  public void movePlayer(int row, int col) {
    Player currentPlayer = this.getCurrentPlayer();
    Card previousPlayerCard =
        this.board
            .get(currentPlayer.getPosition().getRow())
            .get(currentPlayer.getPosition().getCol());
    ArrayList<Position> path =
        this.findPath(
            currentPlayer.getPosition().getRow(), currentPlayer.getPosition().getCol(), row, col);
    if (!path.isEmpty()) {
      // TODO: use proper Card method
      this.hasCurrentPlayerMoved = true;
      previousPlayerCard.removePlayer(currentPlayer);
      this.board.get(row).get(col).addPlayer(currentPlayer);
      currentPlayer.setPosition(row, col);

      this.lastPlayerMovedPath = path;
      this.fireChangeListener();
    }
  }

  public ArrayList<Position> getLastPlayerMovedPath() {
    return lastPlayerMovedPath;
  }

  public Position lastInsertedCardPosition() {
    return lastInsertedCardPosition;
  }

  public void setHasCurrentPlayerInserted(boolean hasCurrentPlayerInserted) {
    this.hasCurrentPlayerInserted = hasCurrentPlayerInserted;
  }

  public void setHasCurrentPlayerMoved(boolean hasCurrentPlayerMoved) {
    this.hasCurrentPlayerMoved = hasCurrentPlayerMoved;
  }

  public void setLastInsertedCardPosition(Position lastInsertedCardPosition) {
    this.lastInsertedCardPosition = lastInsertedCardPosition;
  }

  public boolean hasCurrentPlayerInserted() {
    return hasCurrentPlayerInserted;
  }

  public boolean hasCurrentPlayerMoved() {
    return hasCurrentPlayerMoved;
  }
}
