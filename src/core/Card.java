package core;

import core.utility.Orientation;
import core.utility.Position;
import java.util.ArrayList;

public class Card {
  CardType type;
  private boolean isNordOpen;
  private boolean isEastOpen;
  private boolean isSouthOpen;
  private boolean isWestOpen;
  private Orientation orientation;
  private Goal goal;
  private ArrayList<Player> players;

  // for Dijkstra
  private Position position;
  private int distance;
  private Card from;
  private ArrayList<Card> cardConnected;

  public Card(CardType type) {
    this.type = type;
    this.isNordOpen = type.isNordOpen();
    this.isEastOpen = type.isEastOpen();
    this.isSouthOpen = type.isSouthOpen();
    this.isWestOpen = type.isWestOpen();
    this.orientation = Orientation.NORD;
    this.position = new Position();
    
    this.cardConnected = new ArrayList<Card>();
    this.distance = Integer.MAX_VALUE;
    this.players = new ArrayList<Player>();
  }

  // Rotate the card 90 degrees clockwise
  public Card rotate() {
    this.orientation = this.orientation.next();

    // Rotate the card
    boolean temp = this.isNordOpen;
    this.isNordOpen = this.isWestOpen;
    this.isWestOpen = this.isSouthOpen;
    this.isSouthOpen = this.isEastOpen;
    this.isEastOpen = temp;

    return this;
  }

  public Card rotate(int times) {
    for (int i = 0; i < times; i++) {
      this.rotate();
    }
    return this;
  }

  public boolean isNordOpen() {
    return this.isNordOpen;
  }

  public boolean isEastOpen() {
    return this.isEastOpen;
  }

  public boolean isSouthOpen() {
    return this.isSouthOpen;
  }

  public boolean isWestOpen() {
    return this.isWestOpen;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public CardType getType() {
    return type;
  }

  public void setGoal(Goal goal) {
    this.goal = goal;
  }

  public Goal getGoal() {
    return this.goal;
  }

  public ArrayList<Orientation> getOpenOrientation() {
    ArrayList<Orientation> openOrientation = new ArrayList<Orientation>();
    if (this.isNordOpen) {
      openOrientation.add(Orientation.NORD);
    }
    if (this.isEastOpen) {
      openOrientation.add(Orientation.EAST);
    }
    if (this.isSouthOpen) {
      openOrientation.add(Orientation.SOUTH);
    }
    if (this.isWestOpen) {
      openOrientation.add(Orientation.WEST);
    }
    return openOrientation;
  }

  public void setPosition(int row, int col) {
    this.position.setPosition(row, col);
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public void setCardConnected(ArrayList<Card> cardConnected) {
    this.cardConnected = cardConnected;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public void setFrom(Card from) {
    this.from = from;
  }

  public ArrayList<Card> getCardConnected() {
    return cardConnected;
  }

  public int getDistance() {
    return distance;
  }

  public Card getFrom() {
    return from;
  }

  public Position getPosition() {
    return position;
  }

  public void addCardConnected(Card cardConnected) {
    this.cardConnected.add(cardConnected);
  }

  public void resetGraph() {
    this.distance = Integer.MAX_VALUE;
    this.from = null;
    this.cardConnected = new ArrayList<Card>();
  }

  public void addPlayer(Player player) {
    this.players.add(player);
  }

  public ArrayList<Player> getPlayers() {
    return this.players;
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
  }

  public void clearPlayers() {
    this.players.clear();
  }

  @Override
  public String toString() {
    return "Card [type=" + type + ", isNordOpen=" + isNordOpen + ", isEastOpen=" + isEastOpen + ", isSouthOpen="
        + isSouthOpen + ", isWestOpen=" + isWestOpen + ", orientation=" + orientation + ", goal=" + goal + "]";
  }

  
}
