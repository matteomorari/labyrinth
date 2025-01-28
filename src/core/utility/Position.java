package core.utility;

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

  public boolean equals(Position position) {
    return this.row == position.row && this.col == position.col;
  }

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

  public Position getPosition() {
    return this;
  }

  public int getCol() {
    return col;
  }

  public int getRow() {
    return row;
  }
}
