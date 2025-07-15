package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.CardInsertMove;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import it.unibs.pajc.labyrinth.core.utility.Turn;
import java.util.ArrayList;

public class BotManager {
  Labyrinth model;
  CardInsertMove bestCardInsertMove = null;
  Position bestPosition = null;
  private int nodesVisited = 0; // Counter for debugging

  public BotManager(Labyrinth model) {
    this.model = model;
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

  public Turn calcMove(Labyrinth model, int currentDepth, int maxDepth) {
    nodesVisited++; // Increment node visit counter

    ArrayList<Position> availableCardInsertionPoint = model.getAvailableCardInsertionPoint();
    ArrayList<Turn> parentTurns = new ArrayList<>();
    for (Position cardInsertionPosition : availableCardInsertionPoint) {
      for (int i = 0; i < Orientation.values().length; i++) {
        Labyrinth modelCopy = LabyrinthGson.createCopy(model);
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

        ArrayList<Turn> turns = new ArrayList<>();

        for (Position newPlayerPosition : reachablePlayerPositions) {
          nodesVisited++; // Increment for each reachable position considered
          Turn turn = new Turn(move, newPlayerPosition, currentGoalPosition);

          // check if the new position is equals to the goal position
          if (newPlayerPosition.equals(currentGoalPosition)) {
            turn.setMinDistanceFromGoalFinded(0);
            turns.clear();
            turns.add(turn);
            break;
          }

          // if it's not the goal position, continue normally
          turn.setDepthFromMinDistance(currentDepth);
          turns.add(turn);

          if (currentDepth < maxDepth) {
            modelCopy.movePlayer(newPlayerPosition.getRow(), newPlayerPosition.getCol());
            calcMove(modelCopy, currentDepth + 1, maxDepth);
          }
        }

        Turn closestGoalPosition = getNearestGoalTurn(turns, currentGoalPosition);

        parentTurns.add(closestGoalPosition);
      }
    }

    Turn bestMove = null;
    for (Turn turn : parentTurns) {
      int distance = calculateDistance(turn.getPlayerPosition(), turn.getNewGoalPosition());

      if (bestMove == null || distance < bestMove.getMinDistanceFromGoalFinded()) {
        bestMove = turn;
        bestMove.setMinDistanceFromGoalFinded(distance);
        bestMove.setDepthFromMinDistance(currentDepth);
      }

      if (distance == bestMove.getMinDistanceFromGoalFinded()) {
        // se la migliore distanza è uguale, prendo quella con la profondità minore
        if (turn.getDepthFromMinDistance() < bestMove.getDepthFromMinDistance()) {
          bestMove = turn;
          bestMove.setMinDistanceFromGoalFinded(distance);
          bestMove.setDepthFromMinDistance(currentDepth);
        }
      }
    }

    if (currentDepth == 1) {
      if (getModel().getCurrentPlayer().getGoals().isEmpty()) {
        System.out.println("going at the base!!");
      } else {
        System.out.println(
            "Searching for: "
                + getModel().getCurrentPlayer().getCurrentGoal().getType().toString());
      }
      setBestCardInsertMove(bestMove.getCardInsertMove());
      setBestPosition(bestMove.getPlayerPosition());
      System.out.println(
          "Nodes visited in calcMove: " + nodesVisited); // Print node count for debugging
      nodesVisited = 0; // Reset for next top-level call
    }

    return bestMove;
  }

  private Turn getNearestGoalTurn(ArrayList<Turn> turns, Position goalPosition) {
    Turn closestTurn = null;
    int minDistance = Integer.MAX_VALUE;

    for (Turn turn : turns) {
      int distance = calculateDistance(turn.getPlayerPosition(), goalPosition);
      if (distance < minDistance) {
        minDistance = distance;
        closestTurn = turn;
      }
    }

    return closestTurn;
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
