package it.unibs.pajc.labyrinth.client.controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.LabyrinthController;
import it.unibs.pajc.labyrinth.core.OnlineGameManager;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.LobbyGson;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class LabyrinthClientController extends SocketCommunicationProtocol
    implements LabyrinthController {

  // ! TODO: what a shame to have more than one model
  Labyrinth labyrinthModel;
  OnlineGameManager onlineGameManager;
  Player localPlayer;
  private volatile boolean playerReceived = false;

  public LabyrinthClientController(OnlineGameManager onlineGameManager) {
    super(null);
    this.labyrinthModel = null;
    this.onlineGameManager = onlineGameManager;
    this.localPlayer = null;
    createCommandMap();
  }

  private void createCommandMap() {
    commandMap.put(
        "new_player",
        e -> {
          try {
            setLocalPlayer(e.getParameters().get("player_id").getAsString());
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
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
    commandMap.put(
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
    commandMap.put(
        "game_started",
        e -> {
          try {
            JsonElement labyrinthParameters = e.getParameters().get("labyrinth");
            JsonElement parsedLabyrinthData =
                JsonParser.parseString(labyrinthParameters.getAsString());

            Labyrinth labyrinth = LabyrinthGson.fromJson(parsedLabyrinthData.toString());
            this.labyrinthModel = labyrinth;
            onlineGameManager.getSelectedLobby().setModel(labyrinth);
            onlineGameManager.setGameInProgress();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });

    commandMap.put(
        "player_moved",
        e -> {
          try {
            int newRow = e.getParameters().get("row").getAsInt();
            int newCol = e.getParameters().get("col").getAsInt();

            labyrinthModel.movePlayer(newRow, newCol);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "card_inserted",
        e -> {
          try {
            int row = e.getParameters().get("row").getAsInt();
            int col = e.getParameters().get("col").getAsInt();

            labyrinthModel.insertCard(new Position(row, col));
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "turn_skipped",
        e -> {
          try {
            labyrinthModel.skipTurn();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "power_used",
        e -> {
          try {
            labyrinthModel.usePower();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "set_player_to_swap",
        e -> {
          try {
            String playerId = e.getParameters().get("player_id").getAsString();
            Player playerToSwap = labyrinthModel.getPlayerById(playerId);
            labyrinthModel.setPlayerToSwap(playerToSwap);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "set_goal_to_swap",
        e -> {
          try {
            int row = e.getParameters().get("goal_position_row").getAsInt();
            int col = e.getParameters().get("goal_position_col").getAsInt();
            Goal goal = labyrinthModel.getBoard().get(row).get(col).getGoal();
            labyrinthModel.setGoalToSwap(goal);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
  }

  public boolean connect(String serverAddress, int serverPort) {
    try {
      remoteHost = new Socket(serverAddress, serverPort);
      new Thread(this::run).start();
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  public void setLocalPlayer(String playerId) {
    this.localPlayer = new Player(playerId);
    this.playerReceived = true;
    synchronized (this) {
      this.notifyAll(); // Notify any waiting threads
    }
  }

  public void waitForPlayer() throws InterruptedException {
    synchronized (this) {
      while (!playerReceived) {
        this.wait();
      }
    }
  }

  public Player getLocalPlayer() {
    return localPlayer;
  }

  public void fetchLobby() {
    sendMsg(this, createMessage("fetch_lobbies", null));
  }

  public OnlineGameManager getOnlineGameManager() {
    return onlineGameManager;
  }

  public void joinLobby(String lobbyId) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "join_lobby");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("lobby_id", lobbyId);

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  public void togglePlayerReadyToPlay() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "toggle_player_ready");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("player_id", this.localPlayer.getId());
    parameters.addProperty("lobby_id", this.onlineGameManager.getSelectedLobby().getLobbyId());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  @Override
  public int getBoardSize() {
    return labyrinthModel.getBoardSize();
  }

  @Override
  public ArrayList<ArrayList<Card>> getBoard() {
    return labyrinthModel.getBoard();
  }

  @Override
  public Player getCurrentPlayer() {
    return labyrinthModel.getCurrentPlayer();
  }

  @Override
  public ArrayDeque<Player> getPlayers() {
    return labyrinthModel.getPlayers();
  }

  @Override
  public void movePlayer(int row, int col) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "move_player");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("row", row);
    parameters.addProperty("col", col);

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  @Override
  public void insertCard(Position position) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "insert_card");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("row", position.getRow());
    parameters.addProperty("col", position.getCol());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  @Override
  public ArrayList<Position> getLastPlayerMovedPath() {
    return labyrinthModel.getLastPlayerMovedPath();
  }

  @Override
  public Position lastInsertedCardPosition() {
    return labyrinthModel.lastInsertedCardPosition();
  }

  @Override
  public Card getAvailableCard() {
    return labyrinthModel.getAvailableCard();
  }

  @Override
  public void setPlayerToSwap(Player player) {
    // labyrinthModel.setPlayerToSwap(player);
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "set_player_to_swap");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("player_id", player.getId());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  @Override
  public void setGoalToSwap(Goal goal) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "set_goal_to_swap");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("goal_position_row", goal.getPosition().getRow());
    parameters.addProperty("goal_position_col", goal.getPosition().getCol());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  @Override
  public void skipTurn() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "skip_turn");

    msg.add("parameters", null);
    sendMsg(this, msg.toString());
  }

  @Override
  public void usePower() {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", "use_power");

    msg.add("parameters", null);
    sendMsg(this, msg.toString());
  }

  @Override
  public boolean getHasUsedPower() {
    return labyrinthModel.getHasUsedPower();
  }

  @Override
  public boolean getHasCurrentPlayerInserted() {
    return labyrinthModel.getHasCurrentPlayerInserted();
  }

  @Override
  public Player getPlayerToSwap() {
    return labyrinthModel.getPlayerToSwap();
  }

  @Override
  public Goal getGoalToSwap() {
    return labyrinthModel.getGoalToSwap();
  }

  public Labyrinth getLabyrinthModel() {
    return labyrinthModel;
  }
}
