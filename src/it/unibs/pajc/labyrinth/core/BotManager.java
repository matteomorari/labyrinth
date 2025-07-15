package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.utility.CardInsertMove;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import it.unibs.pajc.labyrinth.core.utility.Turn;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BotManager {
  Labyrinth model;
  CardInsertMove bestCardInsertMove = null;
  Position bestPosition = null;
  private AtomicInteger nodesVisited = new AtomicInteger(0);
  private AtomicInteger threadsUsed = new AtomicInteger(0);

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
    ExecutorService executor = Executors.newCachedThreadPool();
    long startTime = System.currentTimeMillis(); // Start timing
    Turn result = calcMove(model, currentDepth, maxDepth, executor);
    long endTime = System.currentTimeMillis(); // End timing
    System.out.println("Time needed to calcMove: " + (endTime - startTime) + " ms");
    executor.shutdown();
    return result;
  }

  private Turn calcMove(Labyrinth model, int currentDepth, int maxDepth, ExecutorService executor) {
    nodesVisited.incrementAndGet();

    ArrayList<Position> availableCardInsertionPoint = model.getAvailableCardInsertionPoint();
    ArrayList<Turn> parentTurns = new ArrayList<>();
    List<Future<Turn>> futures = new ArrayList<>();

    for (Position cardInsertionPosition : availableCardInsertionPoint) {
      for (int i = 0; i < Orientation.values().length; i++) {
        final Position cardPosCopy = cardInsertionPosition;
        final int orientation = i;
        final Labyrinth modelCopy = LabyrinthGson.createCopy(model);

        futures.add(
            executor.submit(
                () -> {
                  threadsUsed.incrementAndGet();
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
                    return null;
                  }
                  CardInsertMove move = new CardInsertMove(cardPosCopy, orientation);

                  ArrayList<Position> reachablePlayerPositions =
                      modelCopy.findPath(currentPlayerCopy.getPosition(), currentGoalPosition);

                  ArrayList<Turn> turns = new ArrayList<>();

                  for (Position newPlayerPosition : reachablePlayerPositions) {
                    nodesVisited.incrementAndGet();
                    Turn turn = new Turn(move, newPlayerPosition, currentGoalPosition);

                    if (newPlayerPosition.equals(currentGoalPosition)) {
                      turn.setMinDistanceFromGoalFinded(0);
                      turns.clear();
                      turns.add(turn);
                      break;
                    }

                    turn.setDepthFromMinDistance(currentDepth);
                    turns.add(turn);

                    if (currentDepth < maxDepth) {
                      modelCopy.movePlayer(newPlayerPosition.getRow(), newPlayerPosition.getCol());
                      calcMove(modelCopy, currentDepth + 1, maxDepth, executor);
                    }
                  }

                  return getNearestGoalTurn(turns, currentGoalPosition);
                }));
      }
    }

    for (Future<Turn> future : futures) {
      try {
        Turn t = future.get();
        if (t != null) parentTurns.add(t);
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    Turn bestMove = null;
    for (Turn turn : parentTurns) {
      if (turn == null) continue;
      int distance = calculateDistance(turn.getPlayerPosition(), turn.getNewGoalPosition());

      if (bestMove == null || distance < bestMove.getMinDistanceFromGoalFinded()) {
        bestMove = turn;
        bestMove.setMinDistanceFromGoalFinded(distance);
        bestMove.setDepthFromMinDistance(currentDepth);
      }

      if (distance == bestMove.getMinDistanceFromGoalFinded()) {
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
      System.out.println("Nodes visited in calcMove: " + nodesVisited.get());
      System.out.println("Threads used in calcMove: " + threadsUsed.get());
      nodesVisited.set(0);
      threadsUsed.set(0);

      setBestCardInsertMove(bestMove.getCardInsertMove());
      setBestPosition(bestMove.getPlayerPosition());
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
