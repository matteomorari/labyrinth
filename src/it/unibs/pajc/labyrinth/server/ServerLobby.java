package it.unibs.pajc.labyrinth.server;

import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ServerLobby extends Lobby {
  private final transient ReentrantLock lock = new ReentrantLock();
  private final transient ConcurrentHashMap<Player, LabyrinthServerProtocol> playersSocket;

  public ServerLobby(String lobbyName, Labyrinth.EnvironmentType environmentType) {
    super(lobbyName, environmentType);
    this.playersSocket = new ConcurrentHashMap<>();
  }

  public synchronized void addPlayer(Player player, LabyrinthServerProtocol playerSocket) {
    super.addPlayer(player);
    this.playersSocket.put(player, playerSocket);
  }

  @Override
  public synchronized void removePlayer(Player player) {
    super.removePlayer(player);
    this.playersSocket.remove(player);
  }

  public ConcurrentHashMap<Player, LabyrinthServerProtocol> getPlayersSockets() {
    return this.playersSocket;
  }

  public SocketCommunicationProtocol getPlayerSocket(Player player) {
    return playersSocket.get(player);
  }

  @Override
  public synchronized void startGame() {
    if (isGameInProgress()) {
      throw new IllegalStateException("Game is already in progress.");
    }
    lock.lock();
    try {
      for (Player player : getPlayers()) {
        getModel().addPlayer(player);
      }
      getModel().initGame();
      setIsGameInProgress(true);

    } finally {
      lock.unlock();
    }
  }
}
