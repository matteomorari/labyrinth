package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Position;
import java.awt.Color;
import java.awt.PageAttributes.ColorType;
import java.util.ArrayDeque;

public class Player {
  private ArrayDeque<Goal> goalsQueue;
  private Position position;
  private Position startPosition;
  private PlayerColor color;

  public Player(PlayerColor color) {
    this.goalsQueue = new ArrayDeque<>();
    this.position = new Position();
    this.startPosition = new Position();
    this.color = color;
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

  public String getColorName() {
    return this.color.getColorName();
  }

  public Color getColor() {
    return this.color.getColor();
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

  public void setColor(PlayerColor color) {
    this.color = color;
  }
}
