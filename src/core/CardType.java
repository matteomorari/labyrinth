package core;

public enum CardType {
  I(true, false, true, false),
  L(true, true, false, false),
  T(true, true, false, true);

  private boolean isNordOpen;
  private boolean isEastOpen;
  private boolean isSouthOpen;
  private boolean isWestOpen;

  CardType(boolean isNordOpen, boolean isEastOpen, boolean isSouthOpen, boolean isWestOpen) {
    this.isNordOpen = isNordOpen;
    this.isEastOpen = isEastOpen;
    this.isSouthOpen = isSouthOpen;
    this.isWestOpen = isWestOpen;
  }

  public boolean isNordOpen() {
    return this.isNordOpen;
  }

  public boolean isEastOpen() {
    return this.isEastOpen;
  }

  public boolean isSouthOpen() {
    return this.isSouthOpen;
  }

  public boolean isWestOpen() {
    return this.isWestOpen;
  }
}
