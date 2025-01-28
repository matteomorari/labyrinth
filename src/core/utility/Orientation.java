package core.utility;

public enum Orientation {
  // IMPORTANT: The order of the enum values is important, do NOT change it
  NORD,
  EAST,
  SOUTH,
  WEST;

  public Orientation rotate(int angle) {
    return values()[(ordinal() + angle) % 4];
  }

  public Orientation opposite() {
    return rotate(2);
  }

  public Orientation next() {
    return rotate(1);
  }

  public Orientation previous() {
    return rotate(3);
  }
}
