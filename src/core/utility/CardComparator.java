package core.utility;

import java.util.Comparator;

import core.Card;

public class CardComparator implements Comparator<Card> {
  @Override
  public int compare(Card node1, Card node2) {
    return node1.getDistance() - node2.getDistance();
  }
  
}
