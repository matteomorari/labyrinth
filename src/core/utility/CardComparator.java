package core.utility;

import core.Card;
import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
  @Override
  public int compare(Card node1, Card node2) {
    return node1.getDistance() - node2.getDistance();
  }
}
