package it.unibs.pajc.labyrinth.core.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.Color;

public class MyGson {
  Gson gson;

  public MyGson() {
    gson =
        new GsonBuilder()
            // .registerTypeAdapter(Goal.class, new GoalAdapter())
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .setPrettyPrinting()
            .create();
  }

  public Gson getGson() {
    return gson;
  }

  public String toJson(Object object) {
    return gson.toJson(object);
  }

  public Labyrinth fromJson(String json) {
    Labyrinth modelCopy = gson.fromJson(json, Labyrinth.class);
    // TODO: hard solution, to improve
    // we need the goal class  to be the same instance in the board and in the player
    for (Player player : modelCopy.getPlayers()) {
      for (Goal playerGoal : player.getGoals()) {
        Goal boardGoal;

        if (modelCopy.getAvailableCard().getGoal() != null) {
          if (playerGoal.getType().equals(modelCopy.getAvailableCard().getGoal().getType())) {
            modelCopy.getAvailableCard().setGoal(playerGoal);
          }
          continue;
        }

        for (int i = 0; i < modelCopy.getBoardSize(); i++) {
          for (int j = 0; j < modelCopy.getBoardSize(); j++) {
            boardGoal = modelCopy.getBoard().get(i).get(j).getGoal();
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

  public Labyrinth createCopy(Labyrinth model) {
    String json = gson.toJson(model);
    Labyrinth modelCopy = fromJson(json);

    // try (FileWriter writer = new FileWriter("modelCopy.json")) {
    //   writer.write(json);
    // } catch (IOException e) {
    //   e.printStackTrace();
    // }

    return modelCopy;
  }
  // public <T> T createCopy(T object, Class<T> classOfT) {
  //   String json = gson.toJson(object);
  //   return gson.fromJson(json, classOfT);
  // }
}
