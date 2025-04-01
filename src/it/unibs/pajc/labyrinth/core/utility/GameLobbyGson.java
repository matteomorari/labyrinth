package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unibs.pajc.labyrinth.core.GameLobby;
import java.awt.Color;

// TODO: do same oop solution as in LabyrinthGson
public class GameLobbyGson {
  private static final Gson gson =
      new GsonBuilder().registerTypeAdapter(Color.class, new ColorAdapter()).create();

  public static Gson getGson() {
    return gson;
  }

  public static String toJson(Object object) {
    return gson.toJson(object);
  }

  public static GameLobby fromJson(String json) {
    return gson.fromJson(json, GameLobby.class);
  }
}
