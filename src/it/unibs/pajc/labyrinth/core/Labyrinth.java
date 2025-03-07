package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.NodeComparator;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.UUID;

public class Labyrinth extends BaseModel {
  private static final int GOALS_FOR_PLAYER = 2;
  private final transient Random random = new Random();

  private int boardSize;
  private ArrayDeque<Player> players;
  private ArrayList<ArrayList<Card>> board;
  private Card availableCard;
  private Position lastInsertedCardPosition;
  private ArrayList<Position> lastPlayerMovedPath;
  // TODO: to move to the player class?
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
    this.lastPlayerMovedPath = new ArrayList<>();
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

  public void skipTurn() {
    nextPlayer();
  }

  public Player nextPlayer() {
    this.players.add(this.players.poll());
    this.hasCurrentPlayerInserted = false;
    return this.getCurrentPlayer();
  }

  public void initGame() {
    this.initBoard();

    // shuffle goals
    ArrayList<GoalType> goalsList = new ArrayList<>(Arrays.asList(GoalType.values()));
    Collections.shuffle(goalsList);

    // assign goals for each player
    for (Player player : this.players) {
      for (int i = 0; i < GOALS_FOR_PLAYER; i++) {
        player.addGoal(new Goal(goalsList.getFirst()));
        goalsList.removeFirst();
      }
    }

    // assign goals to the card
    // TODO: improve
    for (Player player : this.players) {
      for (Goal goal : player.getGoals()) {
        boolean isAssigned = false;
        while (!isAssigned) {
          int row = random.nextInt(this.boardSize);
          int col = random.nextInt(this.boardSize);
          Card card = this.board.get(row).get(col);
          if (card.getGoal() == null && isGoalPositionValid(row, col)) {
            card.setGoal(goal);
            goal.setPosition(card.getPosition());
            isAssigned = true;
          }
        }
      }
    }
    this.fireChangeListener();
  }

  private boolean isGoalPositionValid(int row, int col) {
    return !isCornerPosition(row, col);
  }

  private boolean isCornerPosition(int row, int col) {
    return (row == 0 && col == 0) || (row == 0 && col == this.boardSize - 1) || (row == this.boardSize - 1 && col == 0)
        || (row == this.boardSize - 1 && col == this.boardSize - 1);
  }

  public ArrayList<ArrayList<Card>> initBoard() {
    this.board.clear();

    for (int i = 0; i < this.boardSize; i++) {
      ArrayList<Card> row = new ArrayList<>();
      for (int j = 0; j < this.boardSize; j++) {
        // the card in the corner must be of the type L
        Card card = null;
        if ((i == 0 && j == 0) || (i == 0 && j == this.boardSize - 1) || (i == this.boardSize - 1 && j == 0)
            || (i == this.boardSize - 1 && j == this.boardSize - 1)) {
          card = new Card(CardType.L, UUID.randomUUID().toString());
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
    this.board.get(0).get(this.boardSize - 1).rotate(2);
    this.board.get(boardSize - 1).get(0);
    this.board.get(this.boardSize - 1).get(this.boardSize - 1).rotate(3);

    // set the players to their start card
    for (Player player : this.players) {
      Card card = this.board.get(player.getStartPosition().getRow()).get(player.getStartPosition().getCol());
      card.addPlayer(player);
    }
    return this.board;
  }

  private Card createRandomCard() {
    CardType cardType = CardType.values()[random.nextInt(CardType.values().length)];
    int randomAngle = random.nextInt(Orientation.values().length);
    return new Card(cardType, UUID.randomUUID().toString()).rotate(randomAngle);
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

  public Card getPlayerCard(Player player) {
    Position playerPosition = player.getPosition();
    if (availableCard.getPosition().equals(playerPosition)) {
      return availableCard;
    }

    return this.board.get(player.getPosition().getRow()).get(player.getPosition().getCol());
  }

  public void insertCard(Position insertPosition) {
    validatePosition(insertPosition);

    if (this.availableCard == null) {
      throw new IllegalStateException("No available card");
    }

    // you cant insert a card in opposite position of the last inserted card
    if (this.lastInsertedCardPosition != null
        && getOppositePosition(insertPosition).equals(this.lastInsertedCardPosition)) {
      System.out.println("Illegal move");
      return;
    }

    if (hasCurrentPlayerInserted) {
      return;
    }

    this.hasCurrentPlayerInserted = true;

    Position endPosition = getOppositePosition(insertPosition);

    // 1 if the cards are moved from the top to the bottom or from the left to the right
    // -1 if the cards are moved from the bottom to the top or from the right to the left
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
    // if the player can't move, go to the next player immediately
    if (getCardOpenDirection(getPlayerCard(getCurrentPlayer())).isEmpty()) {
      nextPlayer();
    }
    // print from player
    // for (Player player : this.getPlayers()) {
    // for (Goal goal : player.getGoals()) {
    // Goal playerGoal = goal;
    // Goal boardGoal;

    // for (int i = 0; i < boardSize; i++) {
    // for (int j = 0; j < boardSize; j++) {
    // boardGoal = board.get(i).get(j).getGoal();
    // if (boardGoal != null) {
    // if (playerGoal.getType().equals(boardGoal.getType())) {
    // System.out.println(playerGoal.getType());
    // System.out.println("Player: " + playerGoal.getPosition());
    // System.out.println("Goal: " + boardGoal.getPosition());
    // System.out.println(
    // "Hash: " + (playerGoal == boardGoal));
    // System.out.println();
    // }
    // }
    // }
    // }
    // }
    // }
    this.fireChangeListener();
  }

  // TODO: there is a duplicate
  private void validatePosition(Position insertPosition) {
    if (insertPosition.row < 0 || insertPosition.row >= this.boardSize || insertPosition.col < 0
        || insertPosition.col >= this.boardSize) {
      throw new IllegalArgumentException("Invalid position");
    }
  }

  public ArrayList<Position> getAvailableCardInsertionPoint() {
    ArrayList<Position> availableCardInsertionPoint = new ArrayList<>();
    for (int i = 1; i < this.boardSize - 1; i++) {
      availableCardInsertionPoint.add(new Position(0, i));
      availableCardInsertionPoint.add(new Position(this.boardSize - 1, i));
      availableCardInsertionPoint.add(new Position(i, 0));
      availableCardInsertionPoint.add(new Position(i, this.boardSize - 1));
    }

    if (lastInsertedCardPosition != null) {
      availableCardInsertionPoint.remove(getOppositePosition(lastInsertedCardPosition));
    }
    return availableCardInsertionPoint;
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
    // TODO: to improve e put together with the others
    if (card.getGoal() != null) {
      card.getGoal().setPosition(new Position(-1, -1));
    }
  }

  private void moveCards(Position endPosition, int rowDirection, int colDirection) {
    for (int i = 0; i < this.boardSize - 1; i++) {
      int currentRow = endPosition.row - (i + 1) * rowDirection;
      int currentCol = endPosition.col - (i + 1) * colDirection;
      Card cardToMove = this.board.get(currentRow).get(currentCol);
      Position newPosition = new Position(endPosition.row - i * rowDirection, endPosition.col - i * colDirection);
      this.updateCardPosition(cardToMove, newPosition);
    }
  }

  // ! seems to not reflect the actual state of the board
  private ArrayList<Orientation> getCardOpenDirection(Card card) {
    ArrayList<Orientation> openOrientation = new ArrayList<>();
    for (Orientation orientation : Orientation.values()) {
      Position neighborPosition = getNeighborPosition(card.getPosition(), orientation);
      // Check if the neighbor position is within the board boundaries
      // TODO: move in the getNeighborPosition, if out of bounds return Exception
      if (isPositionWithinBounds(neighborPosition.getRow(), neighborPosition.getCol())) {
        Boolean isOpen = isPathOpenBetweenCards(card,
            this.board.get(neighborPosition.getRow()).get(neighborPosition.getCol()), orientation);
        if (isOpen) {
          openOrientation.add(orientation);
        }
      }
    }
    return openOrientation;
  }

  private void updateCardPosition(Card card, Position newPosition) {
    this.board.get(newPosition.getRow()).set(newPosition.getCol(), card);
    card.move(newPosition);
    if (card.getGoal() != null) {
      card.getGoal().setPosition(newPosition);
    }
  }

  // using Dijkstra's algorithm
  // TODO: https://www.baeldung.com/java-solve-maze
  public ArrayList<Position> findPath(Position startPosition, Position endPosition) {
    PriorityQueue<Card> nodeDistanceQueue = new PriorityQueue<>(new NodeComparator());
    ArrayList<Position> path = new ArrayList<>();
    ArrayList<Position> visitedPositions = new ArrayList<>();
    boolean found = false;

    // if the path is only the start node, then the player is already there
    if (startPosition.row == endPosition.row && startPosition.col == endPosition.col) {
      visitedPositions.add(startPosition);
      return visitedPositions;
    }

    // reset the distance for each card
    for (ArrayList<Card> row : this.board) {
      for (Card card : row) {
        card.resetGraph();
      }
    }

    // initialize the start node
    Card startCard = this.board.get(startPosition.row).get(startPosition.col);
    startCard.setDistance(0);
    startCard.setFrom(null);
    nodeDistanceQueue.add(startCard);
    visitedPositions.add(startCard.getPosition());

    while (!nodeDistanceQueue.isEmpty() && !found) {
      Card currentNode = nodeDistanceQueue.poll();
      int currentRow = currentNode.getPosition().getRow();
      int currentCol = currentNode.getPosition().getCol();

      if (currentRow == endPosition.row && currentCol == endPosition.col) {
        found = true;
      }

      ArrayList<Orientation> openOrientation = currentNode.getOpenOrientation();

      // for each open orientation check if the neighbor card is also open
      for (Orientation orientation : openOrientation) {
        processNeighbor(currentNode, orientation, nodeDistanceQueue, visitedPositions);
      }
    }

    if (found) {
      Card currentCard = this.board.get(endPosition.row).get(endPosition.col);
      while (currentCard.getFrom() != null) {
        path.add(currentCard.getPosition());
        currentCard = (Card) currentCard.getFrom();
      }
      path.add(currentCard.getPosition()); // get also the start node
      Collections.reverse(path);
      return path;
    }

    return visitedPositions;
  }

  private void processNeighbor(Card currentNode, Orientation orientation, PriorityQueue<Card> nodeDistanceQueue,
      ArrayList<Position> visitedPositions) {

    // Calculate neighbor position based on orientation
    Position neighborPosition = getNeighborPosition(currentNode.getPosition(), orientation);
    int neighborRow = neighborPosition.getRow();
    int neighborCol = neighborPosition.getCol();

    // Check if position is valid
    if (!isPositionWithinBounds(neighborRow, neighborCol)) {
      return;
    }

    Card neighborCard = board.get(neighborRow).get(neighborCol);

    // Check if a path exists between current node and neighbor
    if (isPathOpenBetweenCards(currentNode, neighborCard, orientation)
        && !visitedPositions.contains(neighborPosition)) {
      updateNeighborDistance(currentNode, neighborCard, nodeDistanceQueue);
      visitedPositions.add(neighborPosition);
    }
  }

  // Helper method to determine neighbor coordinates based on orientation
  private Position getNeighborPosition(Position currentPosition, Orientation orientation) {
    int row = currentPosition.getRow();
    int col = currentPosition.getCol();

    switch (orientation) {
    case NORD:
      return new Position(row - 1, col);
    case EAST:
      return new Position(row, col + 1);
    case SOUTH:
      return new Position(row + 1, col);
    case WEST:
      return new Position(row, col - 1);
    default:
      return new Position(row, col); // This should never happen
    }
  }

  // Helper method to check if the path between two cards is open
  private boolean isPathOpenBetweenCards(Card fromCard, Card toCard, Orientation direction) {
    switch (direction) {
    case NORD:
      return fromCard.isNordOpen() && toCard.isSouthOpen();
    case EAST:
      return fromCard.isEastOpen() && toCard.isWestOpen();
    case SOUTH:
      return fromCard.isSouthOpen() && toCard.isNordOpen();
    case WEST:
      return fromCard.isWestOpen() && toCard.isEastOpen();
    default:
      return false;
    }
  }

  private void updateNeighborDistance(Card currentNode, Card neighborCard, PriorityQueue<Card> nodeDistanceQueue) {
    currentNode.addCardConnected(neighborCard);
    if (neighborCard.getDistance() > currentNode.getDistance() + 1) {
      nodeDistanceQueue.add(neighborCard);
      neighborCard.setDistance(currentNode.getDistance() + 1);
      neighborCard.setFrom(currentNode);
    }
  }

  private boolean isPositionWithinBounds(int row, int col) {
    return row >= 0 && row < this.boardSize && col >= 0 && col < this.boardSize;
  }

  public void movePlayer(int row, int col) {
    Player currentPlayer = this.getCurrentPlayer();
    Position currentPosition = currentPlayer.getPosition();
    ArrayList<Position> path = this.findPath(currentPosition, new Position(row, col));

    if (!path.contains(new Position(row, col)) || path.size() < 2 || !hasCurrentPlayerInserted) {
      return;
    }

    // TODO: use proper Card method
    Card previousPlayerCard = this.board.get(currentPlayer.getPosition().getRow())
        .get(currentPlayer.getPosition().getCol());
    previousPlayerCard.removePlayer(currentPlayer);
    this.board.get(row).get(col).addPlayer(currentPlayer);
    currentPlayer.setPosition(row, col);

    this.lastPlayerMovedPath = path;
    isGoalFound(currentPlayer);
    nextPlayer();
    isGameFinished();
    this.fireChangeListener();
  }

  public ArrayList<Position> getLastPlayerMovedPath() {
    return lastPlayerMovedPath;
  }

  public Position lastInsertedCardPosition() {
    return lastInsertedCardPosition;
  }

  public void setLastInsertedCardPosition(Position lastInsertedCardPosition) {
    this.lastInsertedCardPosition = lastInsertedCardPosition;
  }

  public boolean isGameFinished() {
    for (Player player : players) {
      if (player.getGoals().isEmpty() && player.getPosition().equals(player.getStartPosition())) {
        System.out.println("game over");
        return true;
      }
    }
    return false;
  }

  public boolean isGoalFound(Player player) {
    if (player == null || player.getGoals().isEmpty()) {
      return false;
    }

    if (player.getCurrentGoal().getPosition().equals(player.getPosition())) {
      System.out.println(player.getCurrentGoal().getType().toString() + " has been taken");
      player.getGoals().removeFirst();
      return true;
    }

    return false;
  }
}
