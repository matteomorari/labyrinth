package it.unibs.pajc.labyrinth.client.controllers.labyrinth;

import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class LabyrinthLocalController implements LabyrinthController {
  private Labyrinth labyrinth;

  public LabyrinthLocalController(Labyrinth model) {
    this.labyrinth = model;
  }

  public int getBoardSize() {
    return labyrinth.getBoardSize();
  }

  public ArrayList<ArrayList<Card>> getBoard() {
    return labyrinth.getBoard();
  }

  public Player getCurrentPlayer() {
    return labyrinth.getCurrentPlayer();
  }

  public ArrayDeque<Player> getPlayers() {
    return labyrinth.getPlayers();
  }

  public void movePlayer(int row, int col) {
    labyrinth.movePlayer(row, col);
  }

  public void insertCard(Position position) {
    labyrinth.insertCard(position);
  }

  public void rotateAvailableCard(int rotation) {
    labyrinth.rotateAvailableCard(rotation);
  }

  public ArrayList<Position> getLastPlayerMovedPath() {
    return labyrinth.getLastPlayerMovedPath();
  }

  public Position lastInsertedCardPosition() {
    return labyrinth.lastInsertedCardPosition();
  }

  public Card getAvailableCard() {
    return labyrinth.getAvailableCard();
  }

  public void setPlayerToSwap(Player player) {
    labyrinth.setPlayerToSwap(player);
  }

  public void setGoalToSwap(Goal goal) {
    labyrinth.setGoalToSwap(goal);
  }

  public void skipTurn() {
    labyrinth.skipTurn();
  }

  @Override
  public void usePower() {
    labyrinth.usePower();
  }

  @Override
  public boolean isPowerUsed() {
    return labyrinth.isPowerUsed();
  }

  @Override
  public boolean isCurrentPlayerInserted() {
    return labyrinth.isCurrentPlayerInserted();
  }

  public Player getPlayerToSwap() {
    return labyrinth.getPlayerToSwap();
  }

  public Goal getGoalToSwap() {
    return labyrinth.getGoalToSwap();
  }

  public void cardAnimationEnded() {
    labyrinth.cardAnimationEnded();
  }

  public void playerAnimationEnded() {
    labyrinth.playerAnimationEnded();
  }

  @Override
  public boolean isGameOver() {
    return labyrinth.isGameOver();
  }

  @Override
  public boolean isGameCrashed() {
    return labyrinth.isGameCrashed();
  }

  @Override
  public Player getPlayerForGoalDisplay() {
    // In local mode, we always show the current player's goal
    return getCurrentPlayer();
  }
}
