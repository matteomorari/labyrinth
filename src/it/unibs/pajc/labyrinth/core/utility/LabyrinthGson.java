package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;

public class LabyrinthGson {
  private static final Gson gson = new GsonBuilder().registerTypeAdapter(Color.class, new ColorAdapter()).create();

  public static Gson getGson() {
    return gson;
  }

  public static String toJson(Object object) {
    return gson.toJson(object);
  }

  public static Labyrinth fromJson(String json) {
    Labyrinth modelCopy = gson.fromJson(json, Labyrinth.class);
    // TODO: hard solution, to improve
    // we need the goal class to be the same instance in the board and in the player
    // and the player card list the same instance of the model players
    for (int i = 0; i < modelCopy.getBoardSize(); i++) {
      for (int j = 0; j < modelCopy.getBoardSize(); j++) {
        Card card = modelCopy.getBoard().get(i).get(j);
        card.getPlayers().clear();

        for (Player player : modelCopy.getPlayers()) {
          // do the player staff
          if (player.getPosition().equals(card.getPosition())) {
            card.addPlayer(player);
          }

          // do the goal staff
          for (Goal playerGoal : player.getGoals()) {

            if (modelCopy.getAvailableCard().getGoal() != null) {
              if (playerGoal.getType().equals(modelCopy.getAvailableCard().getGoal().getType())) {
                modelCopy.getAvailableCard().setGoal(playerGoal);
              }
              continue;
            }

            Goal boardGoal = card.getGoal();
            if (boardGoal == null) {
              continue;
            }

            if (playerGoal.getType().equals(boardGoal.getType())) {
              modelCopy.getBoard().get(i).get(j).setGoal(playerGoal);
            }

          }
        }
      }
    }
    return modelCopy;
  }

  public static Labyrinth createCopy(Labyrinth model) {
    String json = gson.toJson(model);
    Labyrinth modelCopy = fromJson(json);

    boolean saveToFile = false;
    if (saveToFile) {
      try (FileWriter writer = new FileWriter("modelCopy.json")) {
        writer.write(json);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return modelCopy;
  }

  // TODO: use generics
  // public <T> T createCopy(T object, Class<T> classOfT) {
  // String json = gson.toJson(object);
  // return gson.fromJson(json, classOfT);
  // }
}
