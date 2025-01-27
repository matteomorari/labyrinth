package core;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayDeque;

public class Player {
  private ArrayDeque<Goal> goalsQueue;
  private Point position;
  private Point startPosition;
  private Color color;

  public Player() {
    this.goalsQueue = new ArrayDeque<Goal>();
    this.position = new Point();
    this.startPosition = new Point();
  }

  public void addGoal(Goal goal) {
    this.goalsQueue.add(goal);
  }

  public Goal getCurrentGoal() {
    return this.goalsQueue.peek();
  }

  public void setPosition(int x, int y) {
    this.position.setLocation(x, y);
  }

  public Point getPosition() {
    return this.position;
  }

  public void setStartPosition(int x, int y) {
    this.startPosition.setLocation(x, y);
    this.setPosition(x, y);
  }

  public Point getStartPosition() {
    return this.startPosition;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }
}
