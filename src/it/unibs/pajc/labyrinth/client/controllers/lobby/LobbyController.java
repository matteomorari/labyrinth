package it.unibs.pajc.labyrinth.client.controllers.lobby;

import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.PlayerColor;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import java.util.ArrayList;
import java.util.HashSet;

public interface LobbyController {
  void createLobby(String lobbyName);

  Lobby getSelectedLobby();

  void setSelectedLobby(Lobby lobby);

  HashSet<PlayerColor> getAvailableColors();

  void addPlayer(Player player);

  ArrayList<Player> getPlayers();

  void setPlayerColor(Player player, PlayerColor color);

  void startGame();

  Labyrinth getLabyrinth();
}
