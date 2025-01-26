package core;

import core.utility.Orientation;

public class Card {
  CardType type;
  private boolean isNordOpen;
  private boolean isEastOpen;
  private boolean isSouthOpen;
  private boolean isWestOpen;
  private Orientation orientation;
  private Goal goal;

  public Card(CardType type) {
    this.type = type;
    this.isNordOpen = type.isNordOpen();
    this.isEastOpen = type.isEastOpen();
    this.isSouthOpen = type.isSouthOpen();
    this.isWestOpen = type.isWestOpen();
    this.orientation = Orientation.NORD;
  }

  // Rotate the card 90 degrees clockwise
  public Card rotate() {
    this.orientation = this.orientation.next();

    // Rotate the card
    boolean temp = this.isNordOpen;
    this.isNordOpen = this.isWestOpen;
    this.isWestOpen = this.isSouthOpen;
    this.isSouthOpen = this.isEastOpen;
    this.isEastOpen = temp;

    return this;
  }

  public Card rotate(int times) {
    for (int i = 0; i < times; i++) {
      this.rotate();
    }
    return this;
  }

  public boolean isNordOpen() {
    return this.isNordOpen;
  }

  public boolean isEastOpen() {
    return this.isEastOpen;
  }

  public boolean isSouthOpen() {
    return this.isSouthOpen;
  }

  public boolean isWestOpen() {
    return this.isWestOpen;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public CardType getType() {
    return type;
  }

  public void setGoal(Goal goal) {
    this.goal = goal;
  }

  public Goal getGoal() {
    return this.goal;
  }
}
