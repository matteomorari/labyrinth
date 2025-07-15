package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.CardInsertMove;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import it.unibs.pajc.labyrinth.core.utility.Turn;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class BotManager {
  Labyrinth model;
  CardInsertMove bestCardInsertMove = null;
  Position bestPosition = null;
  private AtomicInteger nodesVisited = new AtomicInteger(0);

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

  public Turn calcMove(Labyrinth model, int maxDepth) {
    long startTime = System.currentTimeMillis(); // Start timing
    Turn result = calcMove(model, 1, maxDepth, null);
    long endTime = System.currentTimeMillis(); // End timing

    setBestCardInsertMove(result.getCardInsertMove());
    setBestPosition(result.getPlayerPosition());

    System.out.println(
        "Best card insert move: " + getBestCardInsertMove().getCardInsertPosition().toString());
    System.out.println("Best position: " + getBestPosition().toString());

    System.out.println("Time needed to calcMove: " + (endTime - startTime) + " ms");
    if (model.getCurrentPlayer().getGoals().isEmpty()) {
      System.out.println("going at the base!!");
    } else {
      System.out.println(
          "Searching for: " + model.getCurrentPlayer().getCurrentGoal().getType().toString());
    }
    System.out.println("Nodes visited in calcMove: " + nodesVisited.get());
    nodesVisited.set(0);

    return result;
  }

  private Turn calcMove(Labyrinth model, int currentDepth, int maxDepth, Turn previousTurn) {
    nodesVisited.incrementAndGet();

    ArrayList<Position> availableCardInsertionPoint = model.getAvailableCardInsertionPoint();
    ArrayList<Turn> turnsList = new ArrayList<>();

    for (Position cardInsertionPosition : availableCardInsertionPoint) {
      for (int i = 0; i < Orientation.values().length; i++) {
        Position cardPosCopy = cardInsertionPosition;
        int orientation = i;
        Labyrinth modelCopy = LabyrinthGson.createCopy(model);

        modelCopy.getAvailableCard().rotate(orientation);
        modelCopy.insertCard(cardPosCopy);

        Player currentPlayerCopy = modelCopy.getCurrentPlayer();
        Position currentGoalPosition;

        if (currentPlayerCopy.getGoals().isEmpty()) {
          currentGoalPosition = currentPlayerCopy.getStartPosition();
        } else {
          currentGoalPosition = currentPlayerCopy.getCurrentGoal().getPosition();
        }

        if (currentGoalPosition.equals(new Position(-1, -1))) {
          continue;
        }

        CardInsertMove move = new CardInsertMove(cardPosCopy, orientation);

        // the method findPath returns always at least the current player position
        ArrayList<Position> reachablePlayerPositions =
            modelCopy.findPath(currentPlayerCopy.getPosition(), currentGoalPosition);

        if (reachablePlayerPositions.isEmpty()) {
          throw new IllegalStateException(
              "No reachable positions found for player: " + currentPlayerCopy.getColorName());
          // continue;
        }

        // we use the reversed list to prioritize the closest positions
        // in particular, if the goal is reachable, it will be the first one
        if (reachablePlayerPositions.contains(currentGoalPosition)) {
          if (previousTurn == null) {
            // this happend only on first iteration
            Turn turn = new Turn(move, currentGoalPosition, null);
            turn.setMinDistanceFromGoalFinded(0);
            return turn;
          } else {
            previousTurn.setMinDistanceFromGoalFinded(0);
            previousTurn.setDepthFromMinDistance(currentDepth);
            return previousTurn;
          }
        }

        for (Position newPlayerPosition : reachablePlayerPositions.reversed()) {
          nodesVisited.incrementAndGet();
          Turn turn = new Turn(move, newPlayerPosition, previousTurn);
          turn.setDepthFromMinDistance(currentDepth);

          if (currentDepth < maxDepth) {
            modelCopy.movePlayer(newPlayerPosition.getRow(), newPlayerPosition.getCol());
            Turn result = calcMove(modelCopy, currentDepth + 1, maxDepth, turn);
            turnsList.add(result);
          }

          if (currentDepth == maxDepth) {
            // turn.setNewGoalPosition(currentGoalPosition);
            turn.setMinDistanceFromGoalFinded(
                calculateDistance(newPlayerPosition, currentGoalPosition));
            turnsList.add(turn);
          }
        }
      }
    }

    Turn bestTurn = getBestMove(turnsList);

    if (bestTurn == null) {
      throw new IllegalStateException("No valid moves found in turnsList");
    }

    if (previousTurn == null) {
      // this happens only if maxDepth = 1
      bestTurn.setMinDistanceFromGoalFinded(0);
      bestTurn.setDepthFromMinDistance(currentDepth);
      return bestTurn;
    }

    Turn previousBestTurn = bestTurn.getPreviousTurn();
    previousBestTurn.setMinDistanceFromGoalFinded(bestTurn.getMinDistanceFromGoalFinded());
    previousBestTurn.setDepthFromMinDistance(bestTurn.getDepthFromMinDistance());

    return previousBestTurn;
  }

  private Turn getBestMove(ArrayList<Turn> turnsList) {
    Turn bestMove = null;
    for (Turn turn : turnsList) {
      if (turn == null) continue;
      int distance = turn.getMinDistanceFromGoalFinded();

      if (bestMove == null || distance < bestMove.getMinDistanceFromGoalFinded()) {
        bestMove = turn;
        // bestMove.setMinDistanceFromGoalFinded(distance);
        // bestMove.setDepthFromMinDistance(turn.getDepthFromMinDistance());
      }

      if (distance == bestMove.getMinDistanceFromGoalFinded()) {
        if (turn.getDepthFromMinDistance() < bestMove.getDepthFromMinDistance()) {
          bestMove = turn;
          // bestMove.setMinDistanceFromGoalFinded(distance);
          // bestMove.setDepthFromMinDistance(turn.getDepthFromMinDistance());
        }
      }
    }
    return bestMove;
  }

  // private Turn getNearestGoalTurn(ArrayList<Turn> turns, Position goalPosition) {
  //   Turn closestTurn = null;
  //   int minDistance = Integer.MAX_VALUE;

  //   for (Turn turn : turns) {
  //     int distance = calculateDistance(turn.getPlayerPosition(), goalPosition);
  //     if (distance < minDistance) {
  //       minDistance = distance;
  //       closestTurn = turn;
  //     }
  //   }

  //   return closestTurn;
  // }

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
