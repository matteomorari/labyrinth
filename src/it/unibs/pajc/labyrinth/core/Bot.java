package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.MyGson;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayList;
import java.util.HashMap;

public class Bot {

  Labyrinth model;
  Player player;
  MyGson myGson = new MyGson();

  public Bot(Labyrinth model, Player player) {
    this.model = model;
    // this.player;
  }

  // TODO! if the goal changes, the goal position in the users queu isn't updated
  // correctly
  public void calcMove() {
    // System.out.println(
    // "Searching for: " +
    // model.getCurrentPlayer().getCurrentGoal().getType().toString());

    ArrayList<Position> availableCardInsertionPoint = model.getAvailableCardInsertionPoint();
    HashMap<Move, PositionDistance> ClosestGoalPositionsMap = new HashMap<>();
    for (Position cardInsertionPosition : availableCardInsertionPoint) {
      for (int i = 0; i < Orientation.values().length; i++) {
        Labyrinth modelCopy = myGson.createCopy(model);
        modelCopy.getAvailableCard().rotate(i);
        modelCopy.insertCard(cardInsertionPosition);

        Player currentPlayerCopy = modelCopy.getCurrentPlayer();
        Position currentGoalPosition;

        if (currentPlayerCopy.getCurrentGoal() == null) {
          currentGoalPosition = currentPlayerCopy.getStartPosition();
        } else {
          currentGoalPosition = currentPlayerCopy.getCurrentGoal().getPosition();
        }

        if (currentGoalPosition.equals(new Position(-1, -1))) {
          // skip if the goal is on the available card
          continue;
        }

        ArrayList<Position> reachablePlayerPositions = modelCopy.findPath(currentPlayerCopy.getPosition(),
            currentGoalPosition);

        PositionDistance closestGoalPosition2 = findClosestGoalPosition(reachablePlayerPositions, currentGoalPosition);

        Move move2 = new Move(cardInsertionPosition, i);
        ClosestGoalPositionsMap.put(move2, closestGoalPosition2);
      }
    }

    Move bestMove = null;
    Position bestPosition = null;
    int minDistance = Integer.MAX_VALUE;
    for (Move move : ClosestGoalPositionsMap.keySet()) {
      PositionDistance closestGoalPosition = ClosestGoalPositionsMap.get(move);
      if (closestGoalPosition.distance < minDistance) {
        minDistance = closestGoalPosition.distance;
        bestMove = move;
        bestPosition = closestGoalPosition.position;
      }
    }

    if (bestMove != null && bestPosition != null) {
      this.model.getAvailableCard().rotate(bestMove.getCardRotateNumber());
      this.model.insertCard(bestMove.getInsertPosition());
      this.model.movePlayer(bestPosition.row, bestPosition.col);
    }

    model.goalObtained(model.getCurrentPlayer());
    // goalObtained();
    if (model.gameOver())
      System.out.println("game over");

  }

  class Move {
    Position insertPosition;
    int cardRotateNumber;

    Move(Position insertPosition, int CardRotateNumber) {
      this.insertPosition = insertPosition;
      this.cardRotateNumber = CardRotateNumber;
    }

    public void setCardRotateNumber(int cardRotateNumber) {
      this.cardRotateNumber = cardRotateNumber;
    }

    public void setInsertPosition(Position insertPosition) {
      this.insertPosition = insertPosition;
    }

    public int getCardRotateNumber() {
      return cardRotateNumber;
    }

    public Position getInsertPosition() {
      return insertPosition;
    }
  }

  class PositionDistance {
    Position position;
    int distance;

    PositionDistance(Position position, int distance) {
      this.position = position;
      this.distance = distance;
    }
  }

  private PositionDistance findClosestGoalPosition(
      ArrayList<Position> positions, Position goalPosition) {
    Position closestPosition = null;
    int minDistance = Integer.MAX_VALUE;

    for (Position position : positions) {
      int distance = calculateDistance(position, goalPosition);
      if (distance < minDistance) {
        minDistance = distance;
        closestPosition = position;
      }
    }

    return new PositionDistance(closestPosition, minDistance);
  }

  private int calculateDistance(Position p1, Position p2) {
    return Math.abs(p1.row - p2.row) + Math.abs(p1.col - p2.col);
  }
}
