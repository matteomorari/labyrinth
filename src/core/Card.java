package core;

import java.awt.Point;
import java.util.ArrayList;

import core.utility.Orientation;

public class Card {
  CardType type;
  private boolean isNordOpen;
  private boolean isEastOpen;
  private boolean isSouthOpen;
  private boolean isWestOpen;
  private Orientation orientation;
  private Goal goal;

  // for Dijkstra
  private Point position;
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
    this.cardConnected = new ArrayList<Card>();
    this.distance = Integer.MAX_VALUE;
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

  public void setPosition(int x, int y) {
    this.position = new Point(x, y);
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

  public void setPosition(Point position) {
    this.position = position;
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

  public Point getPosition() {
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


  @Override
  public String toString() {
    return "Card [type=" + type + ", isNordOpen=" + isNordOpen + ", isEastOpen=" + isEastOpen + ", isSouthOpen="
        + isSouthOpen + ", isWestOpen=" + isWestOpen + ", orientation=" + orientation + ", goal=" + goal + "]";
  }
}
