package it.unibs.pajc.labyrinth.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibs.pajc.labyrinth.core.GameLobby;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import it.unibs.pajc.labyrinth.core.utility.GameLobbyGson;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class LabyrinthServerProtocol extends SocketCommunicationProtocol {
  private volatile GameLobby selectedLobby;
  private Player player;
  private static final CopyOnWriteArrayList<GameLobby> gameLobbies = new CopyOnWriteArrayList<>();
  private static final ReentrantLock lobbyOperationsLock = new ReentrantLock();

  static {
    // TODO: to remove, this is just for testing purposes
    // Initialize the game lobbies
    gameLobbies.add(new GameLobby("Lobby 1"));
    gameLobbies.add(new GameLobby("Lobby 2"));
    gameLobbies.add(new GameLobby("Lobby 3"));
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
            sendAvailableLobbies();
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

            // Acquire lock to make lobby switching atomic
            lobbyOperationsLock.lock();
            try {
              // remove the player from the previous lobby
              if (this.selectedLobby != null) {
                this.selectedLobby.removePlayer(this.player);
                this.player.setIsReadyToPlay(false);
                // notify the other players in the lobby
                sendLobbyStateUpdate(this.selectedLobby);
              }

              // set new lobby
              GameLobby gameLobby = getLobbyById(lobbyId);
              gameLobby.addPlayer(player, this);
              this.selectedLobby = gameLobby;
              // send the new player message to all players in the lobby
              sendLobbyStateUpdate(this.selectedLobby);
            } finally {
              lobbyOperationsLock.unlock();
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "toggle_player_ready",
        e -> {
          try {
            LabyrinthServerProtocol cntrl = (LabyrinthServerProtocol) e.getSender();
            String lobbyId = e.getParameters().get("lobby_id").getAsString();
            String playerId = e.getParameters().get("player_id").getAsString();

            this.player.setIsReadyToPlay(!this.player.isReadyToPlay());
            sendLobbyStateUpdate(this.selectedLobby);
            checkGameBeStarted();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
  }

  private GameLobby getLobbyById(String lobbyId) {
    // find the lobby with the given ID
    for (GameLobby gameLobby : gameLobbies) {
      if (gameLobby.getLOBBY_ID().equals(lobbyId)) {
        return gameLobby;
      }
    }
    return null;
  }

  public synchronized void sendNewPlayerMsg() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "new_player");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("player_id", this.player.getId().toString());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  private void sendAvailableLobbies() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "send_lobbies");

    // Using CopyOnWriteArrayList provides thread-safe iteration
    JsonArray lobbyListJson = new JsonArray();
    for (GameLobby lobby : gameLobbies) {
      JsonObject gameLobbyJson =
          JsonParser.parseString(GameLobbyGson.toJson(lobby)).getAsJsonObject();
      lobbyListJson.add(gameLobbyJson);
    }

    JsonObject parameters = new JsonObject();
    parameters.addProperty("lobbies", lobbyListJson.toString());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  private void sendLobbyStateUpdate(GameLobby lobby) {
    // Create a snapshot of the message before iteration
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "update_lobby");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("lobby", GameLobbyGson.toJson(lobby));
    msg.add("parameters", parameters);

    final String messageToSend = msg.toString();

    // Use synchronized block when accessing shared collection
    synchronized (lobby) {
      for (SocketCommunicationProtocol playerSocket : lobby.getPlayersSockets().values()) {
        playerSocket.sendMsg(playerSocket, messageToSend);
      }
    }
  }

  public static synchronized boolean addLobby(GameLobby lobby) {
    return gameLobbies.add(lobby);
  }

  public static synchronized boolean removeLobby(GameLobby lobby) {
    return gameLobbies.remove(lobby);
  }

  private void checkGameBeStarted() {
    GameLobby lobby = this.selectedLobby;
    if (lobby.getPlayers().size() < 2) {
      return;
    }
    if (lobby.getPlayers().stream().allMatch(Player::isReadyToPlay)) {
      lobby.startGame();
      sendGameStartedMsg();
    }
  }

  private void sendGameStartedMsg() {
    GameLobby lobby = this.selectedLobby;

    JsonObject msg = new JsonObject();
    msg.addProperty("command", "game_started");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("labyrinth", LabyrinthGson.toJson(lobby.getModel()));
    msg.add("parameters", parameters);

    for (SocketCommunicationProtocol playerSocket : lobby.getPlayersSockets().values()) {
      playerSocket.sendMsg(playerSocket, msg.toString());
    }
  }
}
