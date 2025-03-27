package it.unibs.pajc.labyrinth.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.GameLobby;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.LabyrinthController;
import it.unibs.pajc.labyrinth.core.OnlineGameManager;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import it.unibs.pajc.labyrinth.core.utility.GameLobbyGson;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class LabyrinthClientController extends SocketCommunicationProtocol
    implements LabyrinthController {

  // ! TODO: what a shame to have more than one model
  Labyrinth labyrinthModel;
  OnlineGameManager onlineGameManager;
  Player player;

  public LabyrinthClientController(OnlineGameManager onlineGameManager) {
    super(null);
    this.labyrinthModel = null;
    this.onlineGameManager = onlineGameManager;
    this.player = null;
    createCommandMap();
  }

  private void createCommandMap() {
    commandMap.put(
        "new_player",
        e -> {
          try {
            LabyrinthClientController cntrl = (LabyrinthClientController) e.getSender();
            setLocalPlayer(e.getParameters().get("player_id").toString());
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "send_lobbies",
        e -> {
          try {
            LabyrinthClientController cntrl = (LabyrinthClientController) e.getSender();
            ArrayList<GameLobby> availableLobbies = new ArrayList<>();
            JsonElement lobbiesParameters = e.getParameters().get("lobbies");

            JsonElement parsedLobbies = JsonParser.parseString(lobbiesParameters.getAsString());
            for (JsonElement gameLobbyElement : parsedLobbies.getAsJsonArray()) {
              GameLobby lobby = GameLobbyGson.fromJson(gameLobbyElement.toString());
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
            LabyrinthClientController cntrl = (LabyrinthClientController) e.getSender();
            JsonElement lobbyParameters = e.getParameters().get("lobby");
            JsonElement parsedLobbyData = JsonParser.parseString(lobbyParameters.getAsString());

            GameLobby lobby = GameLobbyGson.fromJson(parsedLobbyData.toString());
            onlineGameManager.setSelectedLobby(lobby);

          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "game_started",
        e -> {
          try {
            LabyrinthClientController cntrl = (LabyrinthClientController) e.getSender();
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
    this.player = new Player(playerId);
  }

  public void fetchLobbyOptions() {
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
    parameters.addProperty("player_id", this.player.getId().toString());
    parameters.addProperty("lobby_id", this.onlineGameManager.getSelectedLobby().getLOBBY_ID());

    msg.add("parameters", parameters);
    sendMsg(this, msg.toString());
  }

  @Override
  public void initGame() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'initGame'");
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'movePlayer'");
  }

  @Override
  public void insertCard(Position position) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertCard'");
  }

  @Override
  public ArrayList<Position> getLastPlayerMovedPath() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getLastPlayerMovedPath'");
  }

  @Override
  public Position lastInsertedCardPosition() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'lastInsertedCardPosition'");
  }

  @Override
  public Card getAvailableCard() {
    return labyrinthModel.getAvailableCard();
  }

  @Override
  public void setPlayerToSwap(Player player) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setPlayerToSwap'");
  }

  @Override
  public void setGoalToSwap(Goal goal) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setGoalToSwap'");
  }

  @Override
  public void nextPlayer() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'nextPlayer'");
  }

  @Override
  public void skipTurn() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'skipTurn'");
  }

  @Override
  public void usePower() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'usePower'");
  }

  @Override
  public boolean getHasUsedPower() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getHasUsedPower'");
  }

  @Override
  public boolean getHasCurrentPlayerInserted() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getHasCurrentPlayerInserted'");
  }

  @Override
  public Player getPlayerToSwap() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPlayerToSwap'");
  }

  @Override
  public Goal getGoalToSwap() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getGoalToSwap'");
  }
}
