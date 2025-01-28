package core;

import core.utility.CardComparator;
import core.utility.Orientation;
import core.utility.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class Labyrinth {
  ArrayDeque<Player> players;
  ArrayList<ArrayList<Card>> board;
  Card availableCard;
  private static final int GOALS_FOR_PLAYER = 2;

  private int boardSize;

  public Labyrinth() {
    this(7);
  }

  public Labyrinth(int boardSize) {
    this.players = new ArrayDeque<>();
    this.board = new ArrayList<>();
    this.boardSize = boardSize;
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

    availableCard = this.createRandomCard();

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

  public void insertCard(int x, int y) {
    if (x < 0 || x >= this.boardSize || y < 0 || y >= this.boardSize) {
      throw new IllegalArgumentException("Invalid position");
    }

    if (this.availableCard == null) {
      throw new IllegalStateException("No available card");
    }

    // TODO: HashMap?
    if (x == 0) {
      // shift the cards from top to bottom
      Card futureAvailableCard = this.board.get(this.boardSize - 1).get(y);
      for (int i = this.boardSize - 1; i > 0; i--) {
        this.board.get(i).set(y, this.board.get(i - 1).get(y));
        this.board.get(i).get(y).setPosition(i, y);
        for (Player player : this.board.get(i).get(y).getPlayers()) {
          player.setPosition(i, y);
        }
      }
      this.board.get(0).set(y, this.availableCard);
      this.board.get(0).get(y).setPosition(0, y);
      this.availableCard = futureAvailableCard;
    }

    if (x == this.boardSize - 1) {
      // shift the cards from bottom to top
      Card futureAvailableCard = this.board.get(0).get(y);
      for (int i = 0; i < this.boardSize - 1; i++) {
        this.board.get(i).set(y, this.board.get(i + 1).get(y));
        this.board.get(i).get(y).setPosition(i, y);
        for (Player player : this.board.get(i).get(y).getPlayers()) {
          player.setPosition(i, y);
        }
      }
      this.board.get(this.boardSize - 1).set(y, this.availableCard);
      this.board.get(this.boardSize - 1).get(y).setPosition(this.boardSize - 1, y);
      this.availableCard = futureAvailableCard;
    }

    if (y == 0) {
      // shift the cards from left to right
      Card futureAvailableCard = this.board.get(x).get(this.boardSize - 1);
      for (int i = this.boardSize - 1; i > 0; i--) {
        this.board.get(x).set(i, this.board.get(x).get(i - 1));
        this.board.get(x).get(i).setPosition(x, i);
        for (Player player : this.board.get(x).get(i).getPlayers()) {
          player.setPosition(x, i);
        }
      }
      this.board.get(x).set(0, this.availableCard);
      this.board.get(x).get(0).setPosition(x, 0);
      this.availableCard = futureAvailableCard;
    }

    if (y == this.boardSize - 1) {
      // shift the cards from right to left
      Card futureAvailableCard = this.board.get(x).get(0);
      for (int i = 0; i < this.boardSize - 1; i++) {
        this.board.get(x).set(i, this.board.get(x).get(i + 1));
        this.board.get(x).get(i).setPosition(x, i);
        for (Player player : this.board.get(x).get(i).getPlayers()) {
          player.setPosition(x, i);
        }
      }
      this.board.get(x).set(this.boardSize - 1, this.availableCard);
      this.board.get(x).get(this.boardSize - 1).setPosition(x, this.boardSize - 1);
      this.availableCard = futureAvailableCard;
    }
  }

  // using Dijkstra's algorithm
  // TODO: https://www.baeldung.com/java-solve-maze
  public ArrayList<Position> findPath(int startRow, int startCol, int endRow, int endCol) {
    PriorityQueue<Card> nodeDistanceQueue = new PriorityQueue<Card>(new CardComparator());
    ArrayList<Position> path = new ArrayList<Position>();
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
    // startCard.setPosition(startRow, startCol);
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

      ArrayList<Orientation> openOrientation =
          this.board
              .get(currentNode.getPosition().getRow())
              .get(currentNode.getPosition().getCol())
              .getOpenOrientation();

      // for each open orientation check if the neighbor card is also open
      for (Orientation orientation : openOrientation) {
        switch (orientation) {
          case Orientation.NORD:
            if (this.validPosition(currentRow - 1, currentCol)) {
              Card card = this.board.get(currentRow - 1).get(currentCol);
              if (card.isSouthOpen()) {
                currentNode.addCardConnected(card);
                if (card.getDistance() > currentNode.getDistance() + 1) {
                  nodeDistanceQueue.add(card);
                  card.setDistance(currentNode.getDistance() + 1);
                  card.setFrom(currentNode);
                }
              }
            }

            break;
          case Orientation.EAST:
            if (this.validPosition(currentRow, currentCol + 1)) {
              Card card = this.board.get(currentRow).get(currentCol + 1);
              if (card.isWestOpen()) {
                currentNode.addCardConnected(card);
                if (card.getDistance() > currentNode.getDistance() + 1) {
                  nodeDistanceQueue.add(card);
                  card.setDistance(currentNode.getDistance() + 1);
                  card.setFrom(currentNode);
                }
              }
            }

            break;
          case Orientation.SOUTH:
            if (this.validPosition(currentRow + 1, currentCol)) {
              Card card = this.board.get(currentRow + 1).get(currentCol);
              if (card.isNordOpen()) {
                currentNode.addCardConnected(card);
                if (card.getDistance() > currentNode.getDistance() + 1) {
                  nodeDistanceQueue.add(card);
                  card.setDistance(currentNode.getDistance() + 1);
                  card.setFrom(currentNode);
                }
              }
            }

            break;
          case Orientation.WEST:
            if (this.validPosition(currentRow, currentCol - 1)) {
              Card card = this.board.get(currentRow).get(currentCol - 1);
              if (card.isEastOpen()) {
                currentNode.addCardConnected(card);
                if (card.getDistance() > currentNode.getDistance() + 1) {
                  nodeDistanceQueue.add(card);
                  card.setDistance(currentNode.getDistance() + 1);
                  card.setFrom(currentNode);
                }
              }
            }

            break;

          default:
            break;
        }
      }
    }

    if (found) {
      Card currentCard = this.board.get(endRow).get(endCol);
      while (currentCard.getFrom() != null) {
        path.add(currentCard.getPosition());
        currentCard = currentCard.getFrom();
      }
      path.add(currentCard.getPosition()); // get also the start node
      Collections.reverse(path);
    }

    return path;
  }

  public boolean validPosition(int row, int col) {
    return row >= 0 && row < this.boardSize && col >= 0 && col < this.boardSize;
  }

  public ArrayList<Position> movePlayer(int row, int col) {
    Player currentPlayer = this.getCurrentPlayer();
    Card previousPlayerCard =
        this.board
            .get(currentPlayer.getPosition().getRow())
            .get(currentPlayer.getPosition().getCol());
    ArrayList<Position> path =
        this.findPath(
            currentPlayer.getPosition().getRow(), currentPlayer.getPosition().getCol(), row, col);
    if (!path.isEmpty()) {
      previousPlayerCard.removePlayer(currentPlayer);
      this.board.get(row).get(col).addPlayer(currentPlayer);
      currentPlayer.setPosition(row, col);
    }
    return path;
  }
}
