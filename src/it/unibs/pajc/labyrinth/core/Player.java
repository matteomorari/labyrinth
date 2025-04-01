package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Position;
import java.awt.Color;
import java.util.ArrayDeque;
import java.util.UUID;

public class Player {
  private ArrayDeque<Goal> goalsQueue;
  private Position position;
  private Position startPosition;
  private PlayerColor color;
  private String id;
  private Boolean isReadyToPlay = false;

  public Player(PlayerColor color, String id) {
    this.goalsQueue = new ArrayDeque<>();
    this.position = new Position();
    this.startPosition = new Position();
    this.color = color;
    this.id = id;
  }

  public Player(PlayerColor color) {
    this(color, UUID.randomUUID().toString());
  }

  public Player() {
    this(null, UUID.randomUUID().toString());
  }

  public Player(String id) {
    this(null, id);
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
    // TODO: leaked reference
    return this.position;
  }

  public void setStartPosition(int x, int y) {
    this.startPosition.setPosition(x, y);
    this.setPosition(x, y);
  }

  public Position getStartPosition() {
    // TODO: leaked reference
    return this.startPosition;
  }

  public void setColor(PlayerColor color) {
    this.color = color;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setIsReadyToPlay(Boolean isReadyToPlay) {
    this.isReadyToPlay = isReadyToPlay;
  }

  public Boolean isReadyToPlay() {
    return isReadyToPlay;
  }
}
