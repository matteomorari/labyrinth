package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;

public class TurnGson {
  private static final Gson gson =
      new GsonBuilder().registerTypeAdapter(Color.class, new ColorAdapter()).create();

  // Private constructor to prevent instantiation
  private TurnGson() {}

  public static Gson getGson() {
    return gson;
  }

  public static String toJson(Object object) {
    return gson.toJson(object);
  }

  public static Turn fromJson(String json) {
    return gson.fromJson(json, Turn.class);
  }

  public static Turn createCopy(Object object) {
    String json = gson.toJson(object);
    return gson.fromJson(json, Turn.class);
  }
}
