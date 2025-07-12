package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayDeque;
import java.util.UUID;

public class Player {
  private String id;
  private AvatarColor color;
  private Position startPosition;
  private Position position;
  private boolean isReadyToPlay = false;
  private boolean isBot = false;
  private ArrayDeque<Goal> goalsQueue;

  public Player(AvatarColor color, String id, boolean isBot) {
    this.goalsQueue = new ArrayDeque<>();
    this.position = new Position();
    this.startPosition = new Position();
    this.color = color;
    this.id = id;
    this.isBot = isBot;
  }

  public Player(AvatarColor color, String id) {
    this(color, id, false);
  }

  public Player(AvatarColor color) {
    this(color, UUID.randomUUID().toString(), false);
  }

  public Player() {
    this(null, UUID.randomUUID().toString(), false);
  }

  public Player(String id) {
    this(null, id, false);
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
    return this.color != null ? this.color.getColorName() : null;
  }

  public AvatarColor getAvatarColor() {
    return this.color;
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

  public void setColor(AvatarColor color) {
    this.color = color;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setIsReadyToPlay(boolean isReadyToPlay) {
    this.isReadyToPlay = isReadyToPlay;
  }

  public boolean isReadyToPlay() {
    return isReadyToPlay;
  }

  public boolean isBot() {
    return isBot;
  }

  public void setIsBot(boolean isBot) {
    this.isBot = isBot;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    Player other = (Player) obj;
    return id != null ? id.equals(other.id) : other.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }
}
