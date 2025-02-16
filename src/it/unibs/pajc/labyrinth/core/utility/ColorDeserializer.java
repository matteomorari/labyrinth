package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.lang.reflect.Type;

public class ColorDeserializer implements JsonDeserializer<Color> {

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
