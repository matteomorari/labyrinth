package it.unibs.pajc.labyrinth.client.controllers.labyrinth;

import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;

public interface LabyrinthController {
  int getBoardSize();

  ArrayList<ArrayList<Card>> getBoard();

  Player getCurrentPlayer();

  ArrayDeque<Player> getPlayers();

  void movePlayer(int row, int col);

  void insertCard(Position position);

  void rotateAvailableCard(int rotation);

  ArrayList<Position> getLastPlayerMovedPath();

  Position lastInsertedCardPosition();

  Card getAvailableCard();

  void skipTurn();

  void usePower();

  boolean getHasUsedPower();

  boolean getHasCurrentPlayerInserted();

  void setPlayerToSwap(Player player);

  void setGoalToSwap(Goal goal);

  Player getPlayerToSwap();

  Goal getGoalToSwap();

  void cardAnimationEnded();

  void playerAnimationEnded();

  boolean isGameOver();

  boolean isGameCrashed();
}
