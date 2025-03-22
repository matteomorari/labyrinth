package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.server.LabyrinthServerProtocol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class GameLobby {
  private transient HashMap<Player, LabyrinthServerProtocol> players;
  private ArrayList<Player> playersList;
  private Labyrinth labyrinth;
  private String lobbyName;
  private String lobbyId;

  public GameLobby(Labyrinth labyrinth, String lobbyName) {
    this.lobbyName = lobbyName;
    this.labyrinth = labyrinth;
    this.lobbyId = UUID.randomUUID().toString();
    this.players = new HashMap<>();
    this.playersList = new ArrayList<>();
  }

  public void addPlayer(Player player, LabyrinthServerProtocol playerSocket) {
    this.players.put(player, playerSocket);
    this.playersList.add(player);
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
    this.playersList.remove(player);
  }

  public ArrayList<Player> getPlayers() {
    return new ArrayList<>(this.playersList);
  }

  public HashMap<Player, LabyrinthServerProtocol> getPlayersSockets() {
    return this.players;
  }

  public void setModel(Labyrinth labyrinth) {
    this.labyrinth = labyrinth;
  }

  public Labyrinth getModel() {
    return this.labyrinth;
  }

  public void setLobbyName(String lobbyName) {
    this.lobbyName = lobbyName;
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public void startGame() {
    for (Player player : this.players.keySet()) {
      this.labyrinth.addPlayer(player);
    }
    this.labyrinth.initGame();
  }

  public int getPlayerCount() {
    if (this.playersList == null) {
      return 0;
    }
    return this.playersList.size();
  }

  public String getLobbyId() {
    return lobbyId;
  }
}
