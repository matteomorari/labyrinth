package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Node;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayList;

public class Card extends Node {
  CardType type;
  private boolean isNordOpen;
  private boolean isEastOpen;
  private boolean isSouthOpen;
  private boolean isWestOpen;
  private Orientation orientation;
  private Goal goal;
  private ArrayList<Player> players;

  public Card(CardType type) {
    this.type = type;
    this.isNordOpen = type.isNordOpen();
    this.isEastOpen = type.isEastOpen();
    this.isSouthOpen = type.isSouthOpen();
    this.isWestOpen = type.isWestOpen();
    this.orientation = Orientation.NORD;
    this.players = new ArrayList<Player>();
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

  public ArrayList<Orientation> getOpenOrientation() {
    ArrayList<Orientation> openOrientation = new ArrayList<Orientation>();
    if (this.isNordOpen) {
      openOrientation.add(Orientation.NORD);
    }
    if (this.isEastOpen) {
      openOrientation.add(Orientation.EAST);
    }
    if (this.isSouthOpen) {
      openOrientation.add(Orientation.SOUTH);
    }
    if (this.isWestOpen) {
      openOrientation.add(Orientation.WEST);
    }
    return openOrientation;
  }

  public void addPlayer(Player player) {
    this.players.add(player);
  }

  public ArrayList<Player> getPlayers() {
    return this.players;
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
  }

  public void clearPlayers() {
    this.players.clear();
  }

  public void move(Position position) {
    this.getPosition().setPosition(position.row, position.col);
    this.updatePlayersPosition();
  }

  public void updatePlayersPosition() {
    for (Player player : this.players) {
      player.setPosition(this.getPosition().getRow(), this.getPosition().getCol());
    }
  }

  public void shiftPlayersToNewCard(Card card) {
    for (Player player : this.players) {
      player.setPosition(card.getPosition().getRow(), card.getPosition().getCol());
      card.addPlayer(player);
    }
    this.clearPlayers();
  }

  @Override
  public String toString() {
    return "Card [type="
        + type
        + ", isNordOpen="
        + isNordOpen
        + ", isEastOpen="
        + isEastOpen
        + ", isSouthOpen="
        + isSouthOpen
        + ", isWestOpen="
        + isWestOpen
        + ", orientation="
        + orientation
        + ", goal="
        + goal
        + "]";
  }
}
