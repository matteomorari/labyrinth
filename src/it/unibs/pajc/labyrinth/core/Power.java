package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Position;

public class Power {
  private PowerType type;
  private Position position;

  public Power(PowerType type, Position position) {
    this.type = type;
    this.position = position;
  }

  public Power(PowerType type) {
    this(type, null);
  }

  public PowerType getType() {
    return type;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public void setType(PowerType type) {
    this.type = type;
  }
}
