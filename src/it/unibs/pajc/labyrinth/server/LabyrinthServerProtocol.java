package it.unibs.pajc.labyrinth.server;

import com.google.gson.JsonObject;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class LabyrinthServerProtocol extends SocketCommunicationProtocol {
  private GameLobby game;
  private Player player;
  private static ArrayList<GameLobby> gameLobbies = new ArrayList<>();

  static {
    commandMap = new HashMap<>();

    commandMap.put(
        "@GET_STATUS",
        e -> {
          try {
            LabyrinthServerProtocol s = (LabyrinthServerProtocol) e.getSender();

            // implement the code here

          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
  }

  public LabyrinthServerProtocol(Player player, Socket client) {
    super(client);
    this.player = player;
    sendMsg(this, creteNewPlayerMsg());
  }

  private String creteNewPlayerMsg() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "new_player");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("player_id", this.player.getId().toString());

    msg.add("parameters", parameters);

    return msg.toString();
  }
}
