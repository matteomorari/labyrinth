package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.server.LabyrinthServerProtocol;
import java.awt.Color;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class GameLobby {
  private final transient ReentrantLock lock = new ReentrantLock();
  private transient ConcurrentHashMap<Player, LabyrinthServerProtocol> players;
  private CopyOnWriteArrayList<Player> playersList;
  private volatile Labyrinth labyrinth;
  private volatile String lobbyName;
  private final String LOBBY_ID;
  private Boolean gameInProgress;

  public GameLobby(String lobbyName) {
    this.players = new ConcurrentHashMap<>();
    this.playersList = new CopyOnWriteArrayList<>();
    this.LOBBY_ID = UUID.randomUUID().toString();
    this.lobbyName = lobbyName;
    this.labyrinth = new Labyrinth();
    this.gameInProgress = false;
  }

  public synchronized void addPlayer(Player player, LabyrinthServerProtocol playerSocket) {
    this.players.put(player, playerSocket);
    this.playersList.add(player);
  }

  public synchronized void removePlayer(Player player) {
    this.players.remove(player);
    this.playersList.remove(player);
  }

  public ArrayList<Player> getPlayers() {
    return new ArrayList<>(this.playersList);
  }

  public ConcurrentHashMap<Player, LabyrinthServerProtocol> getPlayersSockets() {
    return this.players;
  }

  public synchronized void setModel(Labyrinth labyrinth) {
    this.labyrinth = labyrinth;
  }

  public Labyrinth getModel() {
    return this.labyrinth;
  }

  public synchronized void setLobbyName(String lobbyName) {
    this.lobbyName = lobbyName;
  }

  public String getLobbyName() {
    return lobbyName;
  }

  public synchronized void startGame() {
    lock.lock();
    // TODO: to remove, this is just for testing purposes
    playersList.get(0).setColor(Color.RED);
    playersList.get(0).setName("RED");
    playersList.get(1).setColor(Color.MAGENTA);
    playersList.get(1).setName("PINK");

    try {
      for (Player player : this.players.keySet()) {
        this.labyrinth.addPlayer(player);
      }
      this.labyrinth.initGame();
    } finally {
      lock.unlock();
    }
  }

  public int getPlayerCount() {
    if (this.playersList == null) {
      return 0;
    }
    return this.playersList.size();
  }

  public String getLOBBY_ID() {
    return LOBBY_ID;
  }

  public void setGameInProgress(Boolean gameInProgress) {
    this.gameInProgress = gameInProgress;
  }

  public Boolean isGameInProgress() {
    return gameInProgress;
  }
}
