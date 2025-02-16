package it.unibs.pajc.labyrinth.core;

public enum Goal {
  HELMET,
  SWORD,
  SKULL,
  GEM;

  private Card card;

  public void setCard(Card card) {
    this.card = card;
  }

  public Card getCard() {
    return card;
  }
}
