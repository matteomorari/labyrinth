package it.unibs.pajc.labyrinth.server;

import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import java.util.ArrayList;
import java.util.HashMap;

public class GameLobby {
  private HashMap<Player, LabyrinthServerProtocol> players;
  private Labyrinth labyrinth;

  public GameLobby(Labyrinth labyrinth) {
    this.players = new HashMap<>();
    this.labyrinth = labyrinth;
  }

  public void addPlayer(Player player, LabyrinthServerProtocol playerSocket) {
    this.players.put(player, playerSocket);
  }

  public void removePlayer(Player player) {
    this.players.remove(player);
  }

  public ArrayList<Player> getPlayers() {
    return new ArrayList<>(this.players.keySet());
  }

  public void setModel(Labyrinth labyrinth) {
    this.labyrinth = labyrinth;
  }

  public Labyrinth getModel() {
    return this.labyrinth;
  }

  public void startGame() {
    for (Player player : this.players.keySet()) {
      this.labyrinth.addPlayer(player);
    }
    this.labyrinth.initGame();
  }
}
