package it.unibs.pajc.labyrinth.core.utility;

import java.util.ArrayList;

public class Node {
  private Position position;
  private int distance;
  private transient Node from;
  private ArrayList<Node> cardConnected;

  public Node() {
    this.position = new Position();
    this.cardConnected = new ArrayList<Node>();
    this.distance = Integer.MAX_VALUE;
  }

  public void setPosition(int row, int col) {
    this.position.setPosition(row, col);
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public void setCardConnected(ArrayList<Node> cardConnected) {
    this.cardConnected = cardConnected;
  }

  public void setDistance(int distance) {
    this.distance = distance;
  }

  public void setFrom(Node from) {
    this.from = from;
  }

  public ArrayList<Node> getCardConnected() {
    return cardConnected;
  }

  public int getDistance() {
    return distance;
  }

  public Node getFrom() {
    return from;
  }

  public Position getPosition() {
    return position;
  }

  public void addCardConnected(Node cardConnected) {
    this.cardConnected.add(cardConnected);
  }

  public void resetGraph() {
    this.distance = Integer.MAX_VALUE;
    this.from = null;
    this.cardConnected = new ArrayList<Node>();
  }
}
