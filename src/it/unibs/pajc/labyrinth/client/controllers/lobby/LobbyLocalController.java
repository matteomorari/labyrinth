package it.unibs.pajc.labyrinth.client.controllers.lobby;

import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.Avatar;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import it.unibs.pajc.labyrinth.core.lobby.LobbyManager;
import java.util.ArrayList;
import java.util.HashSet;

public class LobbyLocalController implements LobbyController {

  private LobbyManager lobbyManager;

  public LobbyLocalController(LobbyManager lobbyManager) {
    this.lobbyManager = lobbyManager;
  }

  @Override
  public void createLobby(String lobbyName) {
    Lobby lobby = new Lobby(lobbyName, Labyrinth.EnvironmentType.LOCAL);
    lobbyManager.setSelectedLobby(lobby);
  }

  @Override
  public Lobby getSelectedLobby() {
    return lobbyManager.getSelectedLobby();
  }

  @Override
  public void setSelectedLobby(Lobby lobby) {
    lobbyManager.setSelectedLobby(lobby);
  }

  @Override
  public HashSet<Avatar> getAvailableColors() {
    return lobbyManager.getSelectedLobby().getAvailableColors();
  }

  @Override
  public void addPlayer(Player player) {
    lobbyManager.addPlayerToLobby(lobbyManager.getSelectedLobby(), player);
  }

  @Override
  public ArrayList<Player> getPlayers() {
    return lobbyManager.getSelectedLobby().getPlayers();
  }

  @Override
  public void removePlayerFromSelectedLobby(Player player) {
    lobbyManager.removePlayerFromSelectedLobby(player);
  }

  @Override
  public void setPlayerColor(Player player, Avatar color) {
    player.setColor(color);
  }

  @Override
  public void startGame() {
    lobbyManager.startGame();
  }

  @Override
  public Labyrinth getLabyrinth() {
    return lobbyManager.getSelectedLobby().getModel();
  }
}
