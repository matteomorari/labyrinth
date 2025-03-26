package it.unibs.pajc.labyrinth.core;

import java.awt.Color;

public enum PlayerColor {
  RED("RED", Color.RED),
  BLUE("BLUE", Color.BLUE),
  GREEN("GREEN", Color.GREEN),
  YELLOW("YELLOW", Color.YELLOW),
  PINK("PINK", Color.pink),
  BLACK("BLACK", Color.darkGray),
  GRAY("GRAY", Color.GRAY),
  ORANGE("ORANGE", Color.ORANGE),
  WHITE("WHITE", Color.LIGHT_GRAY),
  MAGENTA("MAGENTA", Color.MAGENTA),
  BROWN("BROWN", new Color(139, 69, 19)),
  SKYBLUE("SKYBLUE", new Color(135, 206, 235)),;

  private final String colorName;
  private final Color color;

  PlayerColor(String colorName, Color color) {  
    this.colorName = colorName;
    this.color = color;
  }

  public String getColorName() {
    return this.colorName;
  }

  public Color getColor() {
    return this.color;
  }
  
}
