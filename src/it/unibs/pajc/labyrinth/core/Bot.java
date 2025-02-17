package it.unibs.pajc.labyrinth.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unibs.pajc.labyrinth.core.utility.ColorDeserializer;
import it.unibs.pajc.labyrinth.core.utility.ColorSerializer;
import it.unibs.pajc.labyrinth.core.utility.Orientation;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

public class Bot {

  Labyrinth model;
  Player player;

  public Bot(Labyrinth model, Player player) {
    this.model = model;
    this.player = player;
  }

  // TODO! what if the goal is on the available card?
  // TODO! seems that if the goal is reachable without any card insertion, dosn't work
  //TODO! when the goal card change position, seems that the goal position stay the old one
  public void calcMove() {
    ArrayList<Position> availableCardInsertionPoint = model.getAvailableCardInsertionPoint();
    HashMap<Move, PositionDistance> ClosestGoalPositionsMap = new HashMap<>();
    for (Position cardInsertionPosition : availableCardInsertionPoint) {
      for (int i = 0; i < Orientation.values().length; i++) {
        Labyrinth modelCopy = createModelCopy();
        modelCopy.getAvailableCard().rotate(i);
        modelCopy.insertCard(cardInsertionPosition);
        ArrayList<Position> reachablePlayerPositions =
            modelCopy.findPath(
                player.getPosition(), player.getCurrentGoal().getCard().getPosition());

        PositionDistance closestGoalPosition =
            findClosestGoalPosition(
                reachablePlayerPositions, player.getCurrentGoal().getCard().getPosition());

        Move move = new Move(cardInsertionPosition, i);
        ClosestGoalPositionsMap.put(move, closestGoalPosition);
      }
    }

    // TODO! what if the plaer is already on the goal the bestMove will be null. Should be zero
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

    System.out.println(model.getCurrentPlayer().getCurrentGoal().toString());
    model.getAvailableCard().rotate(bestMove.getCardRotateNumber());
    model.insertCard(bestMove.getInsertPosition());
    model.movePlayer(bestPosition.row, bestPosition.col);
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

  private Labyrinth createModelCopy() {
    // Gson gson = new Gson();
    Gson gson =
        new GsonBuilder()
            .registerTypeAdapter(Color.class, new ColorSerializer())
            .registerTypeAdapter(Color.class, new ColorDeserializer())
            .setPrettyPrinting()
            .create();
    String deepCopy = gson.toJson(model);
    // System.out.println(deepCopy);
    Labyrinth modelCopy = gson.fromJson(deepCopy, Labyrinth.class);
    return modelCopy;
  }
}
