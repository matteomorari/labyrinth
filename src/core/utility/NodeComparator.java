package core.utility;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
  @Override
  public int compare(Node node1, Node node2) {
    return node1.getDistance() - node2.getDistance();
  }
}
