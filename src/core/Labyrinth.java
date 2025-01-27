package core;

import java.util.List;

import core.utility.Orientation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    this.players = new ArrayDeque<Player>();
    this.board = new ArrayList<ArrayList<Card>>();
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
        if ((i == 0 && j == 0) || (i == 0 && j == this.boardSize - 1) || (i == this.boardSize - 1 && j == 0)
            || (i == this.boardSize - 1 && j == this.boardSize - 1)) {
          row.add(new Card(CardType.L));
        } else {
          row.add(this.createRandomCard());
        }
      }
      this.board.add(row);
    }

    availableCard = this.createRandomCard();

    // set the right orientation for the corner cards
    this.board.get(0).get(0).rotate();
    this.board.get(this.boardSize - 1).get(0).rotate(2);
    this.board.get(0).get(this.boardSize - 1);
    this.board.get(this.boardSize - 1).get(this.boardSize - 1).rotate(3);
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
      System.out.println(futureAvailableCard.toString());
      for (int i = this.boardSize - 1; i > 0; i--) {
        this.board.get(i).set(y, this.board.get(i - 1).get(y));
      }
      this.board.get(0).set(y, this.availableCard);
      this.availableCard = futureAvailableCard;
    }

    if (x == this.boardSize - 1) {
      // shift the cards from bottom to top
      Card futureAvailableCard = this.board.get(0).get(y);
      for (int i = 0; i < this.boardSize - 1; i++) {
        this.board.get(i).set(y, this.board.get(i + 1).get(y));
      }
      this.board.get(this.boardSize - 1).set(y, this.availableCard);
      this.availableCard = futureAvailableCard;
    }

    if (y == 0) {
      // shift the cards from left to right
      Card futureAvailableCard = this.board.get(x).get(this.boardSize - 1);
      for (int i = this.boardSize - 1; i > 0; i--) {
        this.board.get(x).set(i, this.board.get(x).get(i - 1));
      }
      this.board.get(x).set(0, this.availableCard);
      this.availableCard = futureAvailableCard;
    }

    if (y == this.boardSize - 1) {
      // shift the cards from right to left
      Card futureAvailableCard = this.board.get(x).get(0);
      for (int i = 0; i < this.boardSize - 1; i++) {
        this.board.get(x).set(i, this.board.get(x).get(i + 1));
      }
      this.board.get(x).set(this.boardSize - 1, this.availableCard);
      this.availableCard = futureAvailableCard;
    }
  }
}