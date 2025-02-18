package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.awt.Color;
import java.lang.reflect.Type;

public class ColorAdapter implements JsonDeserializer<Color>, JsonSerializer<Color> {

    @Override
  public JsonElement serialize(Color color, Type colorType, JsonSerializationContext context) {
    JsonObject json = new JsonObject();
    json.addProperty("red", color.getRed());
    json.addProperty("green", color.getGreen());
    json.addProperty("blue", color.getBlue());
    json.addProperty("opacity", color.getAlpha());
    return json;
  }

  @Override
  public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
    JsonObject json = jsonElement.getAsJsonObject();
    float red = json.get("red").getAsFloat();
    float green = json.get("green").getAsFloat();
    float blue = json.get("blue").getAsFloat();
    float opacity = json.get("opacity").getAsFloat();
    return new Color(clamp(red), clamp(green), clamp(blue), clamp(opacity));
  }

  private float clamp(float value) {
    return Math.max(0.0f, Math.min(1.0f, value));
  }
}
