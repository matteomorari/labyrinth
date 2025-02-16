package it.unibs.pajc.labyrinth.core.utility;

import java.awt.Color;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ColorSerializer implements JsonSerializer<Color> {

  @Override
  public JsonElement serialize(Color color, Type colorType, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty("red", color.getRed());
    json.addProperty("green", color.getGreen());
    json.addProperty("blue", color.getBlue());
    json.addProperty("opacity", color.getAlpha());
    return json;
  }
}
