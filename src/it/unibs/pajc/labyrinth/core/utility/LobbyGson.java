package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import java.awt.Color;

public class LobbyGson {
  private static final Gson gson =
      new GsonBuilder().registerTypeAdapter(Color.class, new ColorAdapter()).create();

  // Private constructor to prevent instantiation
  private LobbyGson() {}

  public static Gson getGson() {
    return gson;
  }

  public static String toJson(Object object) {
    return gson.toJson(object);
  }

  public static Lobby fromJson(String json) {
    return gson.fromJson(json, Lobby.class);
  }
}
