package it.unibs.pajc.labyrinth.core.enums;

import java.awt.Color;

public enum MyColors {
  MAIN_BG_COLOR(new Color(217, 217, 217));

  Color color;

  MyColors(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }
}
