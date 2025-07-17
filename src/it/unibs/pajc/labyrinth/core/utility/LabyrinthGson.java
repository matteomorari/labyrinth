package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unibs.pajc.labyrinth.core.BotManager;
import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;

public class LabyrinthGson {
  private static final Gson gson =
      new GsonBuilder().registerTypeAdapter(Color.class, new ColorAdapter()).create();

  public static Gson getGson() {
    return gson;
  }

  public static String toJson(Object object) {
    return gson.toJson(object);
  }

  public static Labyrinth fromJson(String json) {
    Labyrinth modelCopy = gson.fromJson(json, Labyrinth.class);

    for (Player player : modelCopy.getPlayers()) {
      Position playerPosition = player.getPosition();
      Card playerCard =
          modelCopy.getBoard().get(playerPosition.getRow()).get(playerPosition.getCol());
      playerCard.getPlayers().clear();
      playerCard.addPlayer(player);

      for (Goal currentGoal : player.getGoals()) {
        Position currentGoalPosition = currentGoal.getPosition();

        if (currentGoalPosition.equals(new Position(-1, -1))) {
          Card goalCard = modelCopy.getAvailableCard();
          goalCard.setGoal(currentGoal);
          continue;
        }

        Card goalCard =
            modelCopy
                .getBoard()
                .get(currentGoalPosition.getRow())
                .get(currentGoalPosition.getCol());
        goalCard.setGoal(currentGoal);
      }
    }

    modelCopy.setBotManager(new BotManager(modelCopy));
    modelCopy.createPowerActionsMap();
    return modelCopy;
  }

  public static Labyrinth createCopy(Labyrinth model) {
    String json = gson.toJson(model);
    Labyrinth modelCopy = fromJson(json);

    return modelCopy;
  }

  public static void saveToFile(Labyrinth model) {
    String json = gson.toJson(model);
    try (FileWriter writer = new FileWriter("modelCopy" + System.currentTimeMillis() + ".json")) {
      writer.write(json);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
