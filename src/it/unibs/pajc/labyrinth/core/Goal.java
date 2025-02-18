package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Position;

public class Goal {

  private GoalType type;
  private Position position;

  public Goal(GoalType type, Position position) {
    this.type = type;
    this.position = position;
  }

  public Goal(GoalType type) {
    this(type, null);
  }

  public GoalType getType() {
    return type;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public void setType(GoalType type) {
    this.type = type;
  }
  
}
