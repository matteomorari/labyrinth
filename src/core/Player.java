package core;

import java.awt.Color;
import java.util.ArrayDeque;

public class Player {
  private ArrayDeque<Goal> goalsQueue;
  private int[] position;
  private int[] startPosition;
  private Color color;

  public Player() {
    this.goalsQueue = new ArrayDeque<Goal>();
    this.position = new int[2];
  }

  public void addGoal(Goal goal) {
    this.goalsQueue.add(goal);
  }

  public Goal getCurrentGoal() {
    return this.goalsQueue.peek();
  }

  public void setPosition(int x, int y) {
    this.position[0] = x;
    this.position[1] = y;
  }

  public int[] getPosition() {
    return this.position;
  }

  public void setStartPosition(int x, int y) {
    this.startPosition = new int[] { x, y };
    this.setPosition(x, y);
  }

  public int[] getStartPosition() {
    return this.startPosition;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }
}
