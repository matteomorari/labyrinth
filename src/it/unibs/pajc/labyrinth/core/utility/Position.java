package it.unibs.pajc.labyrinth.core.utility;

public class Position {

  public int row;
  public int col;

  public Position(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public Position(Position position) {
    this.row = position.row;
    this.col = position.col;
  }

  public Position() {
    this.row = 0;
    this.col = 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Position position = (Position) obj;
    return row == position.row && col == position.col;
  }

  // TODO: to compare with ==
  // @Override
  // public int hashCode() {
  //   return 31 * row + col;
  // }

  public void setPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public void setCol(int col) {
    this.col = col;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public void set(Position position) {
    this.row = position.row;
    this.col = position.col;
  }

  public int getCol() {
    return col;
  }

  public int getRow() {
    return row;
  }

  @Override
  public String toString() {
    return "Position{" + "row=" + row + ", col=" + col + '}';
  }
}
