package it.unibs.pajc.labyrinth.client.controllers.lobby;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibs.pajc.labyrinth.client.controllers.ClientSocketProtocol;
import it.unibs.pajc.labyrinth.core.AvatarColor;
import it.unibs.pajc.labyrinth.core.BotManager;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import it.unibs.pajc.labyrinth.core.lobby.LobbyManager;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.LobbyGson;
import java.util.ArrayList;
import java.util.HashSet;

public class LobbyClientController implements LobbyController {
  private final ClientSocketProtocol connectionProtocol;
  private volatile boolean playerReceived = false;
  private Player localPlayer;
  private final LobbyManager onlineGameManager;

  public LobbyClientController(
      ClientSocketProtocol connectionProtocol, LobbyManager onlineGameManager) {
    this.localPlayer = null;
    this.playerReceived = false;
    this.connectionProtocol = connectionProtocol;
    this.onlineGameManager = onlineGameManager;
    createCommandMap();
  }

  public ClientSocketProtocol getConnectionProtocol() {
    return connectionProtocol;
  }

  private void createCommandMap() {
    connectionProtocol.addCommand(
        "new_player",
        e -> {
          try {
            setLocalPlayer(e.getParameters().get("player_id").getAsString());
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "available_lobbies",
        e -> {
          try {
            ArrayList<Lobby> availableLobbies = new ArrayList<>();
            JsonElement lobbiesParameters = e.getParameters().get("lobbies");

            JsonElement parsedLobbies = JsonParser.parseString(lobbiesParameters.getAsString());
            for (JsonElement gameLobbyElement : parsedLobbies.getAsJsonArray()) {
              Lobby lobby = LobbyGson.fromJson(gameLobbyElement.toString());
              availableLobbies.add(lobby);
            }
            onlineGameManager.setAvailableLobbies(availableLobbies);

          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "update_lobby",
        e -> {
          try {
            JsonElement lobbyParameters = e.getParameters().get("lobby");
            JsonElement parsedLobbyData = JsonParser.parseString(lobbyParameters.getAsString());

            Lobby lobby = LobbyGson.fromJson(parsedLobbyData.toString());
            onlineGameManager.setSelectedLobby(lobby);
            localPlayer = onlineGameManager.getSelectedLobby().getPlayerById(localPlayer.getId());

          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "remove_from_lobby",
        e -> {
          try {
            onlineGameManager.setSelectedLobby(null);

          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "game_started",
        e -> {
          try {
            JsonElement labyrinthParameters = e.getParameters().get("labyrinth");
            JsonElement parsedLabyrinthData =
                JsonParser.parseString(labyrinthParameters.getAsString());

            Labyrinth labyrinth = LabyrinthGson.fromJson(parsedLabyrinthData.toString());
            labyrinth.setEnvironmentType(Labyrinth.EnvironmentType.CLIENT);
            labyrinth.setBotManager(new BotManager(labyrinth));

            onlineGameManager.getSelectedLobby().setModel(labyrinth);
            onlineGameManager.setGameInProgress();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
  }

  public void waitForPlayer() throws InterruptedException {
    synchronized (this) {
      while (!playerReceived) {
        this.wait();
      }
    }
  }

  public void setLocalPlayer(String playerId) {
    this.localPlayer = new Player(playerId);
    this.playerReceived = true;
    synchronized (this) {
      // Notify any waiting threads
      this.notifyAll();
    }
  }

  public Player getLocalPlayer() {
    return localPlayer;
  }

  public void setPlayerReceived(boolean playerReceived) {
    this.playerReceived = playerReceived;
  }

  public void togglePlayerReadyToPlay() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "toggle_player_ready");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("player_id", this.localPlayer.getId());
    parameters.addProperty("lobby_id", this.onlineGameManager.getSelectedLobby().getLobbyId());

    msg.add("parameters", parameters);
    connectionProtocol.sendMsg(connectionProtocol, msg.toString());
  }

  public LobbyManager getOnlineGameManager() {
    return onlineGameManager;
  }

  public void fetchLobby() {
    connectionProtocol.sendMsg(
        connectionProtocol, SocketCommunicationProtocol.createMessage("fetch_lobbies", null));
  }

  @Override
  public Lobby getSelectedLobby() {
    return onlineGameManager.getSelectedLobby();
  }

  @Override
  public void setSelectedLobby(Lobby lobby) {
    onlineGameManager.setSelectedLobby(lobby);
  }

  @Override
  public HashSet<AvatarColor> getAvailableColors() {
    return onlineGameManager.getSelectedLobby().getAvailableColors();
  }

  @Override
  public void addPlayer(Player player) {
    onlineGameManager.addPlayerToLobby(onlineGameManager.getSelectedLobby(), player);
  }

  @Override
  public ArrayList<Player> getPlayers() {
    return onlineGameManager.getSelectedLobby().getPlayers();
  }

  @Override
  public void setPlayerColor(Player player, AvatarColor color) {
    if (player != null && color != null && onlineGameManager.getSelectedLobby() != null) {
      JsonObject msg = new JsonObject();
      msg.addProperty("command", "set_player_color");

      JsonObject parameters = new JsonObject();
      parameters.addProperty("player_id", player.getId());
      parameters.addProperty("color", color.name());

      msg.add("parameters", parameters);
      connectionProtocol.sendMsg(connectionProtocol, msg.toString());
    }
  }

  @Override
  public void startGame() {
    onlineGameManager.startGame();
  }

  public ArrayList<Lobby> getAvailableLobbies() {
    return onlineGameManager.getAvailableLobbies();
  }

  public Labyrinth getLabyrinth() {
    return onlineGameManager.getSelectedLobby().getLabyrinth();
  }

  public void createLobby(String lobbyName) {
    System.out.println("create lobby: " + lobbyName);
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "create_lobby");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("lobby_name", lobbyName);

    msg.add("parameters", parameters);
    connectionProtocol.sendMsg(connectionProtocol, msg.toString());
  }

  public void joinLobby(String lobbyId) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "join_lobby");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("lobby_id", lobbyId);

    msg.add("parameters", parameters);
    connectionProtocol.sendMsg(connectionProtocol, msg.toString());
  }

  public void addBotToLobby() {
    sendNewBotMsg();
  }

  private void sendNewBotMsg() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "request_add_bot");

    msg.add("parameters", null);
    connectionProtocol.sendMsg(connectionProtocol, msg.toString());
  }

  public void removePlayerFromSelectedLobby(Player player) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "remove_player");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("player_id", player.getId());

    msg.add("parameters", parameters);
    connectionProtocol.sendMsg(connectionProtocol, msg.toString());
  }
}
