package it.unibs.pajc.labyrinth.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibs.pajc.labyrinth.core.GameLobby;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import it.unibs.pajc.labyrinth.core.utility.GameLobbyGson;
import java.net.Socket;
import java.util.ArrayList;

public class LabyrinthServerProtocol extends SocketCommunicationProtocol {
  private GameLobby selectedLobby;
  private Player player;
  private static ArrayList<GameLobby> gameLobbies = new ArrayList<>();

  static {
    // TODO: to remove, this is just for testing purposes
    // Initialize the game lobbies
    gameLobbies.add(new GameLobby(null, "Lobby 1"));
    gameLobbies.add(new GameLobby(null, "Lobby 2"));
    gameLobbies.add(new GameLobby(null, "Lobby 3"));
  }

  public LabyrinthServerProtocol(Player player, Socket client) {
    super(client);
    this.player = player;
    createCommandMap();
  }

  private void createCommandMap() {
    commandMap.put(
        "fetch_lobbies",
        e -> {
          try {
            LabyrinthServerProtocol cntrl = (LabyrinthServerProtocol) e.getSender();
            sendLobbiesMsg();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "join_lobby",
        e -> {
          try {
            LabyrinthServerProtocol cntrl = (LabyrinthServerProtocol) e.getSender();
            String lobbyId = e.getParameters().get("lobby_id").getAsString();

            // remove the player from the previous lobby
            if (this.selectedLobby != null) {
              this.selectedLobby.removePlayer(this.player);
              // notify the other players in the lobby
              updateSelectedLobby();
            }

            // find the lobby with the given ID
            for (GameLobby gameLobby : gameLobbies) {
              if (gameLobby.getLobbyId().equals(lobbyId)) {
                gameLobby.addPlayer(player, this);
                this.selectedLobby = gameLobby;
                // send the new player message to all players in the lobby
                updateSelectedLobby();
                break;
              }
            }

          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
  }

  public void sendNewPlayerMsg() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "new_player");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("player_id", this.player.getId().toString());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  private void sendLobbiesMsg() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "send_lobbies");

    // for each lobby, in gameLobbies serilialize it
    JsonArray lobbyListJson = new JsonArray();
    gameLobbies.forEach(
        lobby -> {
          JsonObject gameLobbyJson =
              JsonParser.parseString(GameLobbyGson.toJson(lobby)).getAsJsonObject();
          lobbyListJson.add(gameLobbyJson);
        });

    JsonObject parameters = new JsonObject();
    parameters.addProperty("lobbies", lobbyListJson.toString());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  private void updateSelectedLobby() {
    for (SocketCommunicationProtocol playerSocket : getConnectedPlayers()) {
      JsonObject msg = new JsonObject();
      msg.addProperty("command", "update_lobby");
      System.out.println(playerSocket);

      JsonObject parameters = new JsonObject();
      parameters.addProperty("lobby", GameLobbyGson.toJson(selectedLobby));

      msg.add("parameters", parameters);
      playerSocket.sendMsg(playerSocket, msg.toString());
    }
  }
}
