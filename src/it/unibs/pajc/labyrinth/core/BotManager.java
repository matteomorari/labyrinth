package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.CardInsertMove;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayList;
import java.util.HashMap;

public class BotManager {
  Labyrinth model;
  CardInsertMove bestCardInsertMove = null;
  Position bestPosition = null;

  public BotManager(Labyrinth model) {
    this.model = model;
  }

  public class newPositionWithDistance {
    Position position;
    int distance;

    public newPositionWithDistance(Position position, int distance) {
      this.position = position;
      this.distance = distance;
    }
  }

  public Labyrinth getModel() {
    return model;
  }

  public void setModel(Labyrinth model) {
    this.model = model;
  }

  public void setBestPosition(Position bestPosition) {
    this.bestPosition = bestPosition;
  }

  public Position getBestPosition() {
    return bestPosition;
  }

  public CardInsertMove getBestCardInsertMove() {
    return bestCardInsertMove;
  }

  public void setBestCardInsertMove(CardInsertMove bestMove) {
    this.bestCardInsertMove = bestMove;
  }

  public void calcMove() {
    if (getModel().getCurrentPlayer().getGoals().isEmpty()) {
      System.out.println("going at the base!!");
    } else {
      System.out.println(
          "Searching for: " + getModel().getCurrentPlayer().getCurrentGoal().getType().toString());
    }

    ArrayList<Position> availableCardInsertionPoint = getModel().getAvailableCardInsertionPoint();
    HashMap<CardInsertMove, newPositionWithDistance> closestGoalPositionsMap = new HashMap<>();
    for (Position cardInsertionPosition : availableCardInsertionPoint) {
      for (int i = 0; i < Orientation.values().length; i++) {
        Labyrinth modelCopy = LabyrinthGson.createCopy(getModel());
        modelCopy.getAvailableCard().rotate(i);
        modelCopy.insertCard(cardInsertionPosition);

        Player currentPlayerCopy = modelCopy.getCurrentPlayer();
        Position currentGoalPosition;

        if (currentPlayerCopy.getGoals().isEmpty()) {
          // if the player has already found all the goals, to win must reach the start position
          currentGoalPosition = currentPlayerCopy.getStartPosition();
        } else {
          currentGoalPosition = currentPlayerCopy.getCurrentGoal().getPosition();
        }

        if (currentGoalPosition.equals(new Position(-1, -1))) {
          // skip if the goal is on the available card
          continue;
        }
        CardInsertMove move = new CardInsertMove(cardInsertionPosition, i);

        ArrayList<Position> reachablePlayerPositions =
            modelCopy.findPath(currentPlayerCopy.getPosition(), currentGoalPosition);

        newPositionWithDistance closestGoalPosition =
            findClosestGoalPosition(reachablePlayerPositions, currentGoalPosition);

        closestGoalPositionsMap.put(move, closestGoalPosition);
      }
    }

    int minDistance = Integer.MAX_VALUE;
    for (CardInsertMove move : closestGoalPositionsMap.keySet()) {
      newPositionWithDistance closestGoalPosition = closestGoalPositionsMap.get(move);
      if (closestGoalPosition.distance < minDistance) {
        minDistance = closestGoalPosition.distance;
        setBestCardInsertMove(move);
        setBestPosition(closestGoalPosition.position);
      }
    }
  }

  private newPositionWithDistance findClosestGoalPosition(
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

    return new newPositionWithDistance(closestPosition, minDistance);
  }

  private int calculateDistance(Position p1, Position p2) {
    return Math.abs(p1.row - p2.row) + Math.abs(p1.col - p2.col);
  }

  public void applyCardInsertion() {
    getModel().getAvailableCard().rotate(getBestCardInsertMove().getCardRotateNumber());
    getModel().insertCard(getBestCardInsertMove().getCardInsertPosition());
    setBestCardInsertMove(null);
  }

  public void applyPlayerMovement() {
    if (getModel().getCurrentPlayer().getPosition().equals(getBestPosition())) {
      System.out.println("I'm already in the best position");
      setBestPosition(null);
      getModel().skipTurn();
    } else {
      getModel().movePlayer(getBestPosition().getRow(), getBestPosition().getCol());
      setBestPosition(null);
    }
  }
}
