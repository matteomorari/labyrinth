package it.unibs.pajc.labyrinth.client.controllers.labyrinth;

import com.google.gson.JsonObject;
import it.unibs.pajc.labyrinth.client.controllers.ClientSocketProtocol;
import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.LabyrinthEvent;
import it.unibs.pajc.labyrinth.core.utility.CardInsertMove;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class LabyrinthClientController implements LabyrinthController {
  private Labyrinth labyrinth;
  private String localPlayerId;
  private final ClientSocketProtocol connectionProtocol;
  private String lastDisconnectedPlayer;

  private static final String COMMAND_PROPERTY = "command";
  private static final String PARAMETERS_PROPERTY = "parameters";
  private static final String PLAYER_ID_PROPERTY = "player_id";

  public LabyrinthClientController(
      ClientSocketProtocol connectionProtocol, Labyrinth labyrinth, String localPlayerId) {
    this.connectionProtocol = connectionProtocol;
    this.labyrinth = labyrinth;
    this.localPlayerId = localPlayerId;
    createCommandMap();
  }

  private void createCommandMap() {

    connectionProtocol.addCommand(
        "player_moved",
        (LabyrinthEvent e) -> {
          try {
            int newRow = e.getParameters().get("row").getAsInt();
            int newCol = e.getParameters().get("col").getAsInt();

            labyrinth.movePlayer(newRow, newCol);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "bot_move_calculated",
        (LabyrinthEvent e) -> {
          try {
            int cardRotateNumber = e.getParameters().get("card_rotate_number").getAsInt();
            int cardRow = e.getParameters().get("card_row").getAsInt();
            int cardCol = e.getParameters().get("card_col").getAsInt();
            int playerRow = e.getParameters().get("bot_row").getAsInt();
            int playerCol = e.getParameters().get("bot_col").getAsInt();
            Position cardPosition = new Position(cardRow, cardCol);
            Position playerPosition = new Position(playerRow, playerCol);

            CardInsertMove move = new CardInsertMove(cardPosition, cardRotateNumber);

            labyrinth.getBotManager().setBestCardInsertMove(move);
            labyrinth.getBotManager().setBestPosition(playerPosition);
            labyrinth.getBotManager().applyCardInsertion();

          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "card_available_rotated",
        (LabyrinthEvent e) -> {
          try {
            int rotation = e.getParameters().get("rotation").getAsInt();
            labyrinth.rotateAvailableCard(rotation);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "card_inserted",
        (LabyrinthEvent e) -> {
          try {
            int row = e.getParameters().get("row").getAsInt();
            int col = e.getParameters().get("col").getAsInt();

            labyrinth.insertCard(new Position(row, col));
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "turn_skipped",
        (LabyrinthEvent e) -> {
          try {
            labyrinth.skipTurn();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "power_used",
        (LabyrinthEvent e) -> {
          try {
            labyrinth.usePower();
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "set_player_to_swap",
        (LabyrinthEvent e) -> {
          try {
            String playerId = e.getParameters().get(PLAYER_ID_PROPERTY).getAsString();
            Player playerToSwap = labyrinth.getPlayerById(playerId);
            labyrinth.setPlayerToSwap(playerToSwap);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "set_goal_to_swap",
        (LabyrinthEvent e) -> {
          try {
            int row = e.getParameters().get("goal_position_row").getAsInt();
            int col = e.getParameters().get("goal_position_col").getAsInt();
            Goal goal = labyrinth.getBoard().get(row).get(col).getGoal();
            labyrinth.setGoalToSwap(goal);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
    connectionProtocol.addCommand(
        "player_disconnected",
        (LabyrinthEvent e) -> {
          try {
            String playerId = e.getParameters().get(PLAYER_ID_PROPERTY).getAsString();
            lastDisconnectedPlayer = labyrinth.getPlayerById(playerId).getColorName();
            labyrinth.setGameCrashed(true);
          } catch (Exception exc) {
            exc.printStackTrace();
          }
        });
  }

  public Player getLocalPlayer() {
    return labyrinth.getPlayerById(localPlayerId);
  }

  @Override
  public int getBoardSize() {
    return labyrinth.getBoardSize();
  }

  @Override
  public ArrayList<ArrayList<Card>> getBoard() {
    return labyrinth.getBoard();
  }

  @Override
  public Player getCurrentPlayer() {
    return labyrinth.getCurrentPlayer();
  }

  @Override
  public ArrayDeque<Player> getPlayers() {
    return labyrinth.getPlayers();
  }

  @Override
  public void movePlayer(int row, int col) {
    if (getLocalPlayer().equals(labyrinth.getCurrentPlayer())) {
      JsonObject msg = new JsonObject();
      msg.addProperty(COMMAND_PROPERTY, "move_player");

      JsonObject parameters = new JsonObject();
      parameters.addProperty("row", row);
      parameters.addProperty("col", col);

      msg.add(PARAMETERS_PROPERTY, parameters);
      connectionProtocol.sendMsg(connectionProtocol, msg.toString());
    }
  }

  @Override
  public void insertCard(Position position) {
    if (getLocalPlayer().equals(labyrinth.getCurrentPlayer())) {
      JsonObject msg = new JsonObject();
      msg.addProperty(COMMAND_PROPERTY, "insert_card");

      JsonObject parameters = new JsonObject();
      parameters.addProperty("row", position.getRow());
      parameters.addProperty("col", position.getCol());

      msg.add(PARAMETERS_PROPERTY, parameters);
      connectionProtocol.sendMsg(connectionProtocol, msg.toString());
    }
  }

  @Override
  public void rotateAvailableCard(int rotation) {
    if (getLocalPlayer().equals(labyrinth.getCurrentPlayer())) {
      JsonObject msg = new JsonObject();
      msg.addProperty(COMMAND_PROPERTY, "rotate_available_card");

      JsonObject parameters = new JsonObject();
      parameters.addProperty("rotation", rotation);

      msg.add(PARAMETERS_PROPERTY, parameters);
      connectionProtocol.sendMsg(connectionProtocol, msg.toString());
    }
  }

  @Override
  public ArrayList<Position> getLastPlayerMovedPath() {
    return labyrinth.getLastPlayerMovedPath();
  }

  @Override
  public Position lastInsertedCardPosition() {
    return labyrinth.lastInsertedCardPosition();
  }

  @Override
  public Card getAvailableCard() {
    return labyrinth.getAvailableCard();
  }

  @Override
  public void setPlayerToSwap(Player player) {
    if (player == null) {
      labyrinth.setPlayerToSwap(null);
      return;
    }

    if (getLocalPlayer().equals(labyrinth.getCurrentPlayer())) {
      JsonObject msg = new JsonObject();
      msg.addProperty(COMMAND_PROPERTY, "set_player_to_swap");

      JsonObject parameters = new JsonObject();
      parameters.addProperty(PLAYER_ID_PROPERTY, player.getId());

      msg.add(PARAMETERS_PROPERTY, parameters);
      connectionProtocol.sendMsg(connectionProtocol, msg.toString());
    }
  }

  @Override
  public void setGoalToSwap(Goal goal) {
    if (getLocalPlayer().equals(labyrinth.getCurrentPlayer())) {
      JsonObject msg = new JsonObject();
      msg.addProperty(COMMAND_PROPERTY, "set_goal_to_swap");

      JsonObject parameters = new JsonObject();
      parameters.addProperty("goal_position_row", goal.getPosition().getRow());
      parameters.addProperty("goal_position_col", goal.getPosition().getCol());

      msg.add(PARAMETERS_PROPERTY, parameters);
      connectionProtocol.sendMsg(connectionProtocol, msg.toString());
    }
  }

  @Override
  public void skipTurn() {
    if (getLocalPlayer().equals(labyrinth.getCurrentPlayer())) {
      JsonObject msg = new JsonObject();
      msg.addProperty(COMMAND_PROPERTY, "skip_turn");

      msg.add(PARAMETERS_PROPERTY, null);
      connectionProtocol.sendMsg(connectionProtocol, msg.toString());
    }
  }

  @Override
  public void usePower() {
    if (getLocalPlayer().equals(labyrinth.getCurrentPlayer())) {
      JsonObject msg = new JsonObject();
      msg.addProperty(COMMAND_PROPERTY, "use_power");

      msg.add(PARAMETERS_PROPERTY, null);
      connectionProtocol.sendMsg(connectionProtocol, msg.toString());
    }
  }

  @Override
  public boolean isPowerUsed() {
    return labyrinth.isPowerUsed();
  }

  @Override
  public boolean isCurrentPlayerInserted() {
    return labyrinth.isCurrentPlayerInserted();
  }

  @Override
  public Player getPlayerToSwap() {
    return labyrinth.getPlayerToSwap();
  }

  @Override
  public Goal getGoalToSwap() {
    return labyrinth.getGoalToSwap();
  }

  @Override
  public void cardAnimationEnded() {
    labyrinth.cardAnimationEnded();
    sendCardAnimationEndedMsg();
  }

  private void sendCardAnimationEndedMsg() {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_PROPERTY, "card_animation_ended");

    msg.add(PARAMETERS_PROPERTY, null);
    connectionProtocol.sendMsg(connectionProtocol, msg.toString());
  }

  @Override
  public void playerAnimationEnded() {
    labyrinth.playerAnimationEnded();
    sendPlayerAnimationEndedMsg();
  }

  private void sendPlayerAnimationEndedMsg() {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_PROPERTY, "player_animation_ended");

    msg.add(PARAMETERS_PROPERTY, null);
    connectionProtocol.sendMsg(connectionProtocol, msg.toString());
  }

  public Labyrinth getLabyrinth() {
    return labyrinth;
  }

  @Override
  public boolean isGameOver() {
    return labyrinth.isGameOver();
  }

  @Override
  public boolean isGameCrashed() {
    return labyrinth.isGameCrashed();
  }

  @Override
  public Player getPlayerForGoalDisplay() {
    // In online mode, we always show the local player's goal
    return labyrinth.getPlayerById(getLocalPlayer().getId());
  }

  @Override
  public String getLastDisconnectedPlayer() {
    return lastDisconnectedPlayer;
  }
}
