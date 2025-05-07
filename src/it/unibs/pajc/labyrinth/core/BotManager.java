package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayList;
import java.util.HashMap;

public class BotManager {
  Labyrinth model;
  CardMovement bestMove = null;
  Position bestPosition = null;
  
  public BotManager(Labyrinth model) {
    this.model = model;
  }
  class CardMovement {
    int cardRotateNumber;
    Position cardInsertPosition;
  
    CardMovement(Position insertPosition, int cardRotateNumber) {
      this.cardInsertPosition = insertPosition;
      this.cardRotateNumber = cardRotateNumber;
    }
  
    public void setCardRotateNumber(int cardRotateNumber) {
      this.cardRotateNumber = cardRotateNumber;
    }
  
    public void setCardInsertPosition(Position insertPosition) {
      this.cardInsertPosition = insertPosition;
    }
  
    public int getCardRotateNumber() {
      return cardRotateNumber;
    }
  
    public Position getCardInsertPosition() {
      return cardInsertPosition;
    }
  }
  
  class newPositionWithDistance {
    Position position;
    int distance;
  
    newPositionWithDistance(Position position, int distance) {
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

  public CardMovement getBestMove() {
    return bestMove;
  }

  public void setBestMove(CardMovement bestMove) {
    this.bestMove = bestMove;
  }

  public void calcMove() {
    if (getModel().getCurrentPlayer().getGoals().isEmpty()) {
      System.out.println("going at the base!!");
    } else {
      System.out.println(
          "Searching for: " + getModel().getCurrentPlayer().getCurrentGoal().getType().toString());
    }

    ArrayList<Position> availableCardInsertionPoint = getModel().getAvailableCardInsertionPoint();
    HashMap<CardMovement, newPositionWithDistance> closestGoalPositionsMap = new HashMap<>();
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
        CardMovement move = new CardMovement(cardInsertionPosition, i);

        ArrayList<Position> reachablePlayerPositions =
            modelCopy.findPath(currentPlayerCopy.getPosition(), currentGoalPosition);

        newPositionWithDistance closestGoalPosition =
            findClosestGoalPosition(reachablePlayerPositions, currentGoalPosition);

        closestGoalPositionsMap.put(move, closestGoalPosition);
      }
    }

    int minDistance = Integer.MAX_VALUE;
    for (CardMovement move : closestGoalPositionsMap.keySet()) {
      newPositionWithDistance closestGoalPosition = closestGoalPositionsMap.get(move);
      if (closestGoalPosition.distance < minDistance) {
        minDistance = closestGoalPosition.distance;
        setBestMove(move);
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
    getModel().getAvailableCard().rotate(getBestMove().getCardRotateNumber());
    getModel().insertCard(getBestMove().getCardInsertPosition());
    setBestMove(null);
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
