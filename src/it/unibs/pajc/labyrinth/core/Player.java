package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Position;
import java.awt.Color;
import java.util.ArrayDeque;

public class Player {
  private ArrayDeque<Goal> goalsQueue;
  private Position position;
  private Position startPosition;
  private transient Color color;

  public Player() {
    this.goalsQueue = new ArrayDeque<Goal>();
    this.position = new Position();
    this.startPosition = new Position();
  }

  public void addGoal(Goal goal) {
    this.goalsQueue.add(goal);
  }

  public Goal getCurrentGoal() {
    return this.goalsQueue.peek();
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
}
