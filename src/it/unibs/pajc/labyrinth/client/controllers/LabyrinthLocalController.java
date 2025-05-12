package it.unibs.pajc.labyrinth.client.controllers;

import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class LabyrinthLocalController implements LabyrinthController {
  private Labyrinth model;

  public LabyrinthLocalController(Labyrinth model) {
    this.model = model;
  }

  public int getBoardSize() {
    return model.getBoardSize();
  }

  public ArrayList<ArrayList<Card>> getBoard() {
    return model.getBoard();
  }

  public Player getCurrentPlayer() {
    return model.getCurrentPlayer();
  }

  public ArrayDeque<Player> getPlayers() {
    return model.getPlayers();
  }

  public void movePlayer(int row, int col) {
    model.movePlayer(row, col);
  }

  public void insertCard(Position position) {
    model.insertCard(position);
  }

  public void rotateAvailableCard(int rotation) {
    model.getAvailableCard().rotate(rotation);
  }

  public ArrayList<Position> getLastPlayerMovedPath() {
    return model.getLastPlayerMovedPath();
  }

  public Position lastInsertedCardPosition() {
    return model.lastInsertedCardPosition();
  }

  public Card getAvailableCard() {
    return model.getAvailableCard();
  }

  public void setPlayerToSwap(Player player) {
    model.setPlayerToSwap(player);
  }

  public void setGoalToSwap(Goal goal) {
    model.setGoalToSwap(goal);
  }

  public void skipTurn() {
    model.skipTurn();
  }

  @Override
  public void usePower() {
    model.usePower();
  }

  @Override
  public boolean getHasUsedPower() {
    return model.getHasUsedPower();
  }

  @Override
  public boolean getHasCurrentPlayerInserted() {
    return model.getHasCurrentPlayerInserted();
  }

  public Player getPlayerToSwap() {
    return model.getPlayerToSwap();
  }

  public Goal getGoalToSwap() {
    return model.getGoalToSwap();
  }

  public void cardAnimationEnded() {
    model.cardAnimationEnded();
  }

  public void playerAnimationEnded() {
    model.playerAnimationEnded();
  }
}
