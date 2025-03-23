package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;

public interface LabyrinthController {
  void initGame();

  int getBoardSize();

  ArrayList<ArrayList<Card>> getBoard();

  Player getCurrentPlayer();

  ArrayDeque<Player> getPlayers();

  void movePlayer(int row, int col);

  void insertCard(Position position);

  ArrayList<Position> getLastPlayerMovedPath();

  Position lastInsertedCardPosition();

  Card getAvailableCard();

  void swapPlayers(Player player);

  void changeGoal(Goal goal);

  void changeSecondGoal();

  void nextPlayer();

  void skipTurn();

  void usePower();

  boolean getHasUsedPower();

  boolean getHasCurrentPlayerInserted();

  // boolean getHasCurrentPlayerMoved();
}
