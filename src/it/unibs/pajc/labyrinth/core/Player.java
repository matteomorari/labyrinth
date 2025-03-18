package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Position;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.UUID;

public class Player {
  private ArrayDeque<Goal> goalsQueue;
  private Position position;
  private Position startPosition;
  private Color color;
  private String name;
  private String uniqueID;

  public Player() {
    this(null, UUID.randomUUID().toString());
  }

  public Player(String name, String uniqueID) {
    this.name = name;
    this.uniqueID = uniqueID;
    this.goalsQueue = new ArrayDeque<>();
    this.position = new Position();
    this.startPosition = new Position();
  }

  public void addGoal(Goal goal) {
    this.goalsQueue.add(goal);
  }

  public Goal getCurrentGoal() {
    return this.goalsQueue.peek();
  }

  public ArrayDeque<Goal> getGoals() {
    return goalsQueue;
  }

  public void setPosition(int x, int y) {
    this.position.setPosition(x, y);
  }

  public Position getPosition() {
    return this.position;
  }

  public void setStartPosition(int x, int y) {
    this.startPosition.setPosition(x, y);
    this.setPosition(x, y);
  }

  public Position getStartPosition() {
    return this.startPosition;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void setUniqueID(String uniqueID) {
    this.uniqueID = uniqueID;
  }

  public String getUniqueID() {
    return uniqueID;
  }

  public ArrayDeque<Goal> getGoalsQueue() {
    return goalsQueue;
  }
}
