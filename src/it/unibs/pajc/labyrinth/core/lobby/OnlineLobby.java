package it.unibs.pajc.labyrinth.core.lobby;

import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.server.LabyrinthServerProtocol;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class OnlineLobby extends Lobby {
  private final transient ReentrantLock lock = new ReentrantLock();
  private transient ConcurrentHashMap<Player, LabyrinthServerProtocol> playersSocket;

  public OnlineLobby(String lobbyName) {
    super(lobbyName);
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

  @Override
  public synchronized void startGame() {
    lock.lock();
    try {
      for (Player player : this.playersSocket.keySet()) {
        getLabyrinth().addPlayer(player);
      }
      getLabyrinth().initGame();
      setGameInProgress(true);
    } finally {
      lock.unlock();
    }
  }
}
