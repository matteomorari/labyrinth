package it.unibs.pajc.labyrinth.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unibs.pajc.labyrinth.core.Avatar;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.LabyrinthEvent;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import it.unibs.pajc.labyrinth.core.utility.BotMoveCalcListener;
import it.unibs.pajc.labyrinth.core.utility.CardInsertMove;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import it.unibs.pajc.labyrinth.core.utility.LobbyGson;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

// ! TODO: delete a lobby when the game end (and also when the is no player in the lobby??)
// TODO: separate the lobby and game as in the client?
public class LabyrinthServerProtocol extends SocketCommunicationProtocol
    implements BotMoveCalcListener {
  private volatile ServerLobby currentLobby;
  private Player player;
  private static final CopyOnWriteArrayList<ServerLobby> gameLobbies = new CopyOnWriteArrayList<>();
  private static final ReentrantLock lobbyOperationsLock = new ReentrantLock();
  private static final String COMMAND_KEY = "command";
  private static final String PARAMETERS_KEY = "parameters";
  private static final String PLAYER_ID_KEY = "player_id";

  static {
    // Initialize the game lobbies
    addLobby(new ServerLobby("Lobby 1", Labyrinth.EnvironmentType.SERVER));
    addLobby(new ServerLobby("Lobby 2", Labyrinth.EnvironmentType.SERVER));
    addLobby(new ServerLobby("Lobby 3", Labyrinth.EnvironmentType.SERVER));
  }

  public LabyrinthServerProtocol(Player player, Socket client) {
    super(client);
    this.player = player;
    createCommandMap();
  }

  private void createCommandMap() {
    commandMap.put(
        "fetch_lobbies",
        (LabyrinthEvent e) -> {
          try {
            sendAvailableLobbies();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "create_lobby",
        (LabyrinthEvent e) -> {
          try {
            String lobbyName = e.getParameters().get("lobby_name").getAsString();
            ServerLobby newLobby = new ServerLobby(lobbyName, Labyrinth.EnvironmentType.SERVER);
            addLobby(newLobby);
            sendAvailableLobbies();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "join_lobby",
        (LabyrinthEvent e) -> {
          try {
            String lobbyId = e.getParameters().get("lobby_id").getAsString();

            // Acquire lock to make lobby switching atomic
            lobbyOperationsLock.lock();
            try {
              // remove the player from the previous lobby
              if (this.currentLobby != null) {
                currentLobby.removePlayer(this.player);
                this.player.setIsReadyToPlay(false);
                // notify the other players in the lobby
                sendLobbyStateUpdate(this.currentLobby);
              }

              // set new lobby
              currentLobby = getLobbyById(lobbyId);
              currentLobby.addPlayer(player, this);
              // send the new player message to all players in the lobby
              sendLobbyStateUpdate(this.currentLobby);
            } finally {
              lobbyOperationsLock.unlock();
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "request_add_bot",
        (LabyrinthEvent e) -> {
          try {
            Player newBot = new Player();
            newBot.setIsReadyToPlay(true);
            newBot.setIsBot(true);
            currentLobby.addPlayer(newBot);
            sendLobbyStateUpdate(currentLobby);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "remove_player",
        (LabyrinthEvent e) -> {
          try {
            String playerId = e.getParameters().get(PLAYER_ID_KEY).getAsString();
            Player playerToRemove = currentLobby.getPlayerById(playerId);
            if (!playerToRemove.isBot()) {
              sendPlayerRemoveFromLobbyMsg(currentLobby.getPlayerSocket(playerToRemove));
            }
            currentLobby.removePlayer(playerToRemove);
            sendLobbyStateUpdate(currentLobby);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "set_player_color",
        (LabyrinthEvent e) -> {
          try {
            String playerId = e.getParameters().get(PLAYER_ID_KEY).getAsString();
            String colorName = e.getParameters().get("color").getAsString();

            Player targetPlayer = currentLobby.getPlayerById(playerId);
            Avatar color = Avatar.valueOf(colorName);

            if (targetPlayer != null && color != null) {
              currentLobby.setPlayerColor(targetPlayer, color);
              sendLobbyStateUpdate(currentLobby);
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "toggle_player_ready",
        (LabyrinthEvent e) -> {
          try {
            this.player.setIsReadyToPlay(!this.player.isReadyToPlay());
            sendLobbyStateUpdate(this.currentLobby);
            checkGameBeStarted();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "move_player",
        (LabyrinthEvent e) -> {
          try {
            LabyrinthServerProtocol sender = (LabyrinthServerProtocol) e.getSender();
            int newRow = e.getParameters().get("row").getAsInt();
            int newCol = e.getParameters().get("col").getAsInt();

            Labyrinth labyrinthModel = currentLobby.getModel();

            // check if the sender is the current player
            if (sender.player.equals(labyrinthModel.getCurrentPlayer())) {
              labyrinthModel.movePlayer(newRow, newCol);
              sendPlayerMoveNotification(newRow, newCol);
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "rotate_available_card",
        (LabyrinthEvent e) -> {
          try {
            LabyrinthServerProtocol sender = (LabyrinthServerProtocol) e.getSender();
            int rotation = e.getParameters().get("rotation").getAsInt();

            Labyrinth labyrinthModel = currentLobby.getModel();

            // check if the sender is the current player
            if (sender.player.equals(labyrinthModel.getCurrentPlayer())) {
              labyrinthModel.rotateAvailableCard(rotation);
              sendCardAvailableRotatedMsg(rotation);
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "insert_card",
        (LabyrinthEvent e) -> {
          try {
            LabyrinthServerProtocol sender = (LabyrinthServerProtocol) e.getSender();
            int row = e.getParameters().get("row").getAsInt();
            int col = e.getParameters().get("col").getAsInt();

            Labyrinth labyrinthModel = currentLobby.getModel();

            // check if the sender is the current player
            if (sender.player.equals(labyrinthModel.getCurrentPlayer())) {
              labyrinthModel.insertCard(new Position(row, col));
              sendPlayerCardInsertNotification(row, col);
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "card_animation_ended",
        (LabyrinthEvent e) -> {
          try {
            Labyrinth labyrinthModel = currentLobby.getModel();

            if (currentLobby.getModel().isWaitingForCardAnimation()) {
              currentLobby.getModel().setWaitingForCardAnimation(false);
              labyrinthModel.cardAnimationEnded();
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "player_animation_ended",
        (LabyrinthEvent e) -> {
          try {
            Labyrinth labyrinthModel = currentLobby.getModel();

            if (currentLobby.getModel().isWaitingForPlayerAnimation()) {
              currentLobby.getModel().setWaitingForPlayerAnimation(false);
              labyrinthModel.playerAnimationEnded();
              if (labyrinthModel.isGameOver()) {
                removeLobby(currentLobby);
              }
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "skip_turn",
        (LabyrinthEvent e) -> {
          try {
            LabyrinthServerProtocol sender = (LabyrinthServerProtocol) e.getSender();
            Labyrinth labyrinthModel = currentLobby.getModel();

            // check if the sender is the current player
            if (sender.player.equals(labyrinthModel.getCurrentPlayer())) {
              sendTurnSkippedNotification();
              labyrinthModel.skipTurn();
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "use_power",
        (LabyrinthEvent e) -> {
          try {
            LabyrinthServerProtocol sender = (LabyrinthServerProtocol) e.getSender();
            Labyrinth labyrinthModel = currentLobby.getModel();

            // check if the sender is the current player
            if (sender.player.equals(labyrinthModel.getCurrentPlayer())) {
              labyrinthModel.usePower();
              sendPowerUsedNotification();
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "set_player_to_swap",
        (LabyrinthEvent e) -> {
          try {
            LabyrinthServerProtocol sender = (LabyrinthServerProtocol) e.getSender();
            Labyrinth labyrinthModel = currentLobby.getModel();

            // check if the sender is the current player
            if (sender.player.equals(labyrinthModel.getCurrentPlayer())) {
              String playerId = e.getParameters().get(PLAYER_ID_KEY).getAsString();
              Player playerToSwap = labyrinthModel.getPlayerById(playerId);
              labyrinthModel.setPlayerToSwap(playerToSwap);
              sendPlayerSwapNotification(playerId);
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    commandMap.put(
        "set_goal_to_swap",
        (LabyrinthEvent e) -> {
          try {
            LabyrinthServerProtocol sender = (LabyrinthServerProtocol) e.getSender();
            Labyrinth labyrinthModel = currentLobby.getModel();

            // check if the sender is the current player
            if (sender.player.equals(labyrinthModel.getCurrentPlayer())) {
              int row = e.getParameters().get("goal_position_row").getAsInt();
              int col = e.getParameters().get("goal_position_col").getAsInt();
              Goal goal = labyrinthModel.getBoard().get(row).get(col).getGoal();
              System.out.println(goal.getType().toString());
              labyrinthModel.setGoalToSwap(goal);
              sendGoalSwap(goal);
            }
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
  }

  private void sendPlayerRemoveFromLobbyMsg(SocketCommunicationProtocol socket) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "remove_from_lobby");

    msg.add(PARAMETERS_KEY, null);
    socket.sendMsg(socket, msg.toString());
  }

  private void sendCardAvailableRotatedMsg(int rotation) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "card_available_rotated");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("rotation", rotation);

    msg.add(PARAMETERS_KEY, parameters);

    sendMessageToLobbyPlayers(msg);
  }

  private void sendMessageToLobbyPlayers(JsonObject msg) {
    for (SocketCommunicationProtocol playerSocket : currentLobby.getPlayersSockets().values()) {
      playerSocket.sendMsg(playerSocket, msg.toString());
    }
  }

  private ServerLobby getLobbyById(String lobbyId) {
    // find the lobby with the given ID
    for (ServerLobby gameLobby : gameLobbies) {
      if (gameLobby.getLobbyId().equals(lobbyId)) {
        return gameLobby;
      }
    }
    return null;
  }

  public synchronized void sendNewPlayerMsg() {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "new_player");

    JsonObject parameters = new JsonObject();
    parameters.addProperty(PLAYER_ID_KEY, this.player.getId());

    msg.add(PARAMETERS_KEY, parameters);
    sendMsg(this, msg.toString());
  }

  private void sendAvailableLobbies() {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "available_lobbies");

    // Using CopyOnWriteArrayList provides thread-safe iteration
    JsonArray lobbyListJson = new JsonArray();
    for (ServerLobby lobby : gameLobbies) {
      if (lobby.isGameInProgress()) {
        continue;
      }
      JsonObject gameLobbyJson = JsonParser.parseString(LobbyGson.toJson(lobby)).getAsJsonObject();
      lobbyListJson.add(gameLobbyJson);
    }

    JsonObject parameters = new JsonObject();
    parameters.addProperty("lobbies", lobbyListJson.toString());

    msg.add(PARAMETERS_KEY, parameters);

    for (SocketCommunicationProtocol playerSocket : getConnectedUsers()) {
      playerSocket.sendMsg(playerSocket, msg.toString());
    }
  }

  private void sendLobbyStateUpdate(ServerLobby lobby) {
    // Create a snapshot of the message before iteration
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "update_lobby");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("lobby", LobbyGson.toJson(lobby));
    msg.add(PARAMETERS_KEY, parameters);

    sendMessageToLobbyPlayers(msg);
  }

  public static synchronized boolean addLobby(ServerLobby lobby) {
    return gameLobbies.add(lobby);
  }

  public static synchronized boolean removeLobby(ServerLobby lobby) {
    return gameLobbies.remove(lobby);
  }

  private void checkGameBeStarted() {
    ServerLobby lobby = this.currentLobby;
    if (lobby.getPlayers().size() < 2) {
      return;
    }
    if (lobby.getPlayers().stream().allMatch(Player::isReadyToPlay)) {
      lobby.startGame();
      lobby.getModel().setBotMoveListener(this);
      sendGameStartedMsg();
      // If the first player is a bot, start its turn immediately
      if (lobby.getModel().getCurrentPlayer().isBot()) {
        lobby.getModel().startBotPlayerTurn();
      }
    }
  }

  private void sendGameStartedMsg() {
    ServerLobby lobby = this.currentLobby;

    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "game_started");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("labyrinth", LabyrinthGson.toJson(lobby.getModel()));
    msg.add(PARAMETERS_KEY, parameters);

    sendAvailableLobbies();
    sendMessageToLobbyPlayers(msg);
  }

  private void sendPlayerMoveNotification(int newRow, int newCol) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "player_moved");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("row", newRow);
    parameters.addProperty("col", newCol);

    msg.add(PARAMETERS_KEY, parameters);

    sendMessageToLobbyPlayers(msg);
  }

  private void sendPlayerCardInsertNotification(int row, int col) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "card_inserted");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("row", row);
    parameters.addProperty("col", col);

    msg.add(PARAMETERS_KEY, parameters);

    sendMessageToLobbyPlayers(msg);
  }

  private void sendTurnSkippedNotification() {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "turn_skipped");

    msg.add(PARAMETERS_KEY, null);
    sendMessageToLobbyPlayers(msg);
  }

  private void sendPowerUsedNotification() {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "power_used");

    msg.add(PARAMETERS_KEY, null);
    sendMessageToLobbyPlayers(msg);
  }

  private void sendPlayerSwapNotification(String playerToSwapId) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "set_player_to_swap");

    JsonObject parameters = new JsonObject();
    parameters.addProperty(PLAYER_ID_KEY, playerToSwapId);

    msg.add(PARAMETERS_KEY, parameters);
    sendMessageToLobbyPlayers(msg);
  }

  private void sendGoalSwap(Goal goal) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "set_goal_to_swap");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("goal_position_row", goal.getPosition().getRow());
    parameters.addProperty("goal_position_col", goal.getPosition().getCol());

    msg.add(PARAMETERS_KEY, parameters);
    sendMessageToLobbyPlayers(msg);
  }

  @Override
  public void onBotMoveCalc(CardInsertMove insertedCard, Position futureBotPosition) {
    sendBotMove(insertedCard, futureBotPosition);
  }

  private void sendBotMove(CardInsertMove insertedCard, Position futureBotPosition) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "bot_move_calculated");

    JsonObject parameters = new JsonObject();
    parameters.addProperty("card_rotate_number", insertedCard.getCardRotateNumber());
    parameters.addProperty("card_row", insertedCard.getCardInsertPosition().getRow());
    parameters.addProperty("card_col", insertedCard.getCardInsertPosition().getCol());
    parameters.addProperty("bot_row", futureBotPosition.getRow());
    parameters.addProperty("bot_col", futureBotPosition.getCol());

    msg.add(PARAMETERS_KEY, parameters);
    sendMessageToLobbyPlayers(msg);
  }

  @Override
  public void disconnectUser() {
    // Handle disconnection first
    handleDisconnection();

    // Then call the parent class disconnect method to clean up resources
    super.disconnectUser();
  }

  private void handleDisconnection() {
    lobbyOperationsLock.lock();
    try {
      if (this.player == null || this.currentLobby == null) {
        return;
      }

      // Remove player from current lobby
      currentLobby.removePlayer(player);
      // If there's only one human player left, end the game
      long humanPlayersCount = currentLobby.getPlayers().stream().filter(p -> !p.isBot()).count();

      // If the player was in a game in progress, end the game
      if (currentLobby.isGameInProgress()) {
        removeLobby(currentLobby);
        sendPlayerDisconnectedNotification(player.getId());
      } else {
        // If lobby is empty after player left, remove it
        if (humanPlayersCount == 0) {
          removeLobby(currentLobby);
        }
        sendLobbyStateUpdate(currentLobby);
      }

      currentLobby = null;
    } finally {
      lobbyOperationsLock.unlock();
    }
  }

  private void sendPlayerDisconnectedNotification(String id) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, "player_disconnected");

    JsonObject parameters = new JsonObject();
    parameters.addProperty(PLAYER_ID_KEY, id);

    msg.add(PARAMETERS_KEY, parameters);
    sendMessageToLobbyPlayers(msg);
  }
}
