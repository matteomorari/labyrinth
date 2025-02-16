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

  void setHasCurrentPlayerInserted(boolean hasCurrentPlayerInserted);

  void setHasCurrentPlayerMoved(boolean hasCurrentPlayerMoved);

  boolean hasCurrentPlayerInserted();

  boolean hasCurrentPlayerMoved();
}
