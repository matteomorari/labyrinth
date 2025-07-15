package it.unibs.pajc.labyrinth.core.utility;

public class Turn {
  private CardInsertMove cardInsertMove;
  private Position playerPosition;
  private int minDistanceFromGoalFinded = Integer.MAX_VALUE;
  private int depthFromMinDistance = 0;
  // due to inserted card, the goal position may change
  // if the goal does not change, this will be the same as the player's position
  private Position newGoalPosition; 

  public Turn(CardInsertMove cardInsertMove, Position playerPosition, Position newGoalPosition) {
    this.cardInsertMove = cardInsertMove;
    this.playerPosition = playerPosition;
    this.newGoalPosition = newGoalPosition;
  }

  public CardInsertMove getCardInsertMove() {
    return cardInsertMove;
  }

  public Position getPlayerPosition() {
    return playerPosition;
  }

  public void setCardInsertMove(CardInsertMove cardInsertMove) {
    this.cardInsertMove = cardInsertMove;
  }

  public void setPlayerPosition(Position playerPosition) {
    this.playerPosition = playerPosition;
  }

  public int getMinDistanceFromGoalFinded() {
    return minDistanceFromGoalFinded;
  }

  public int getDepthFromMinDistance() {
    return depthFromMinDistance;
  }

  public void setMinDistanceFromGoalFinded(int minDistanceFromGoalFinded) {
    this.minDistanceFromGoalFinded = minDistanceFromGoalFinded;
  }

  public void setDepthFromMinDistance(int depthFromMinDistance) {
    this.depthFromMinDistance = depthFromMinDistance;
  }

  public Position getNewGoalPosition() {
    return newGoalPosition;
  }

  public void setNewGoalPosition(Position newGoalPosition) {
    this.newGoalPosition = newGoalPosition;
  }
}
