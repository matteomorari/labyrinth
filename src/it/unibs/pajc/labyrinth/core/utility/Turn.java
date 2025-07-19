package it.unibs.pajc.labyrinth.core.utility;

public class Turn {
  private CardInsertMove cardInsertMove;
  private Position playerPosition;
  private int minDistanceFromGoalFound = Integer.MAX_VALUE;
  private int depthFromMinDistance = 0;
  // due to inserted card, the goal position may change
  // if the goal does not change, this will be the same as the player's position
  private Turn previousTurn;

  public Turn(CardInsertMove cardInsertMove, Position playerPosition, Turn previousTurn) {
    this.cardInsertMove = cardInsertMove;
    this.playerPosition = playerPosition;
    this.previousTurn = previousTurn;
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

  public int getMinDistanceFromGoalFound() {
    return minDistanceFromGoalFound;
  }

  public int getDepthFromMinDistance() {
    return depthFromMinDistance;
  }

  public void setMinDistanceFromGoalFound(int minDistanceToGoalFound) {
    this.minDistanceFromGoalFound = minDistanceToGoalFound;
  }

  public void setDepthFromMinDistance(int depthFromMinDistance) {
    this.depthFromMinDistance = depthFromMinDistance;
  }

  public Turn getPreviousTurn() {
    return previousTurn;
  }

  public void setPreviousTurn(Turn previousTurn) {
    this.previousTurn = previousTurn;
  }
}
