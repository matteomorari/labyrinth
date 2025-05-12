package it.unibs.pajc.labyrinth.core.utility;

public class CardInsertMove {
  int cardRotateNumber;
  Position cardInsertPosition;

  public CardInsertMove(Position insertPosition, int cardRotateNumber) {
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