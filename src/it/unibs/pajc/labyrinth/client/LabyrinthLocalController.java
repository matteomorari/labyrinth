package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.LabyrinthController;
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

  public void initGame() {
    model.initGame();
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

  public ArrayList<Position> getLastPlayerMovedPath() {
    return model.getLastPlayerMovedPath();
  }

  public Position lastInsertedCardPosition() {
    return model.lastInsertedCardPosition();
  }

  public Card getAvailableCard() {
    return model.getAvailableCard();
  }

  public void swapPlayers(Player player) {
    model.swapPlayers(player);
  }

  public void changeGoal(Goal goal) {
    model.changeGoal(goal);
  }

  public void changeSecondGoal() {
    model.changeSecondGoal();
  }

  public void nextPlayer() {
    model.nextPlayer();
  }

  public void skipTurn() {
    model.skipTurn();
  }

  @Override
  public void usePower() {
    model.usePower(getAvailableCard().getPower().getType());
  }

  @Override
  public boolean getHasUsedPower() {
    return model.getHasUsedPower();
  }
}
