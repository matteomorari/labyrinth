package it.unibs.pajc.labyrinth.core.lobby;

import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.AvatarColor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class Lobby {
  private volatile Labyrinth labyrinth;
  private volatile String lobbyName;
  private ArrayList<Player> playersList;
  private final String LOBBY_ID;
  private Boolean isGameInProgress;
  private HashSet<AvatarColor> availableColors;

  public Lobby(String lobbyName, Labyrinth.EnvironmentType environmentType) {
    this.playersList = new ArrayList<>();
    this.LOBBY_ID = UUID.randomUUID().toString();
    this.lobbyName = lobbyName;
    this.isGameInProgress = false;
    this.labyrinth = new Labyrinth(7, environmentType);
    this.availableColors = new HashSet<>();
    availableColors = new HashSet<>(java.util.Arrays.asList(AvatarColor.values()));
  }

  public void addPlayer(Player player) {
    if (getPlayers().contains(player)) {
      throw new IllegalArgumentException("Player already exists in the lobby.");
    }

    if (getPlayers().size() >= Labyrinth.MAX_PLAYERS) {
      throw new IllegalStateException("Lobby is full. Cannot add more players.");
    }
    if (isGameInProgress) {
      throw new IllegalStateException("Game is already in progress. Cannot add more players.");
    }

    getPlayers().add(player);
    setPlayerRandomColor(player);
  }

  public Player getPlayerById(String playerId) {
    for (Player player : getPlayers()) {
      if (player.getId().equals(playerId)) {
        return player;
      }
    }
    return null; // Player not found
  }

  public void removePlayer(Player player) {
    removePlayerColor(player);
    getPlayers().remove(player);
  }

  public ArrayList<Player> getPlayers() {
    return this.playersList;
  }

  public void setModel(Labyrinth labyrinth) {
    this.labyrinth = labyrinth;
  }

  public Labyrinth getModel() {
    return labyrinth;
  }

  public void setLobbyName(String lobbyName) {
    this.lobbyName = lobbyName;
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public void startGame() {
    for (Player player : getPlayers()) {
      labyrinth.addPlayer(player);
    }
    labyrinth.initGame();
    isGameInProgress = true;
  }

  public int getPlayerCount() {
    if (getPlayers() == null) {
      return 0;
    }
    return getPlayers().size();
  }

  public String getLobbyId() {
    return LOBBY_ID;
  }

  public void setIsGameInProgress(Boolean gameInProgress) {
    this.isGameInProgress = gameInProgress;
  }

  public Boolean isGameInProgress() {
    return isGameInProgress;
  }

  public Labyrinth getLabyrinth() {
    return labyrinth;
  }

  public void setPlayerColor(Player player, AvatarColor color) {
    if (!getPlayers().contains(player)) {
      throw new IllegalArgumentException("Player not found in the lobby.");
    }

    removePlayerColor(player);

    // get the player from the list and set the color
    for (Player p : getPlayers()) {
      if (p.equals(player)) {
        p.setColor(color);
        // remove the color from the available colors
        availableColors.remove(color);
        break;
      }
    }
  }

  public void setPlayerRandomColor(Player player) {
    if (!getPlayers().contains(player)) {
      throw new IllegalArgumentException("Player not found in the lobby.");
    }

    AvatarColor color = getFreeColor();
    if (color == null) {
      throw new IllegalStateException("No more colors available.");
    }

    setPlayerColor(player, color);
  }

  private AvatarColor getFreeColor() {
    if (availableColors.isEmpty()) {
      return null; // No more colors available
    }
    AvatarColor randomColor = availableColors.iterator().next();
    availableColors.remove(randomColor);
    return randomColor;
  }

  public HashSet<AvatarColor> getAvailableColors() {
    return availableColors;
  }

  public void removePlayerColor(Player player) {
    // add the previous color from the available colors
    if (player.getAvatarColor() != null) {
      availableColors.add(player.getAvatarColor());
      player.setColor(null);
    }
  }
}
