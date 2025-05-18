package it.unibs.pajc.labyrinth.core.lobby;

import it.unibs.pajc.labyrinth.core.BaseModel;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import java.util.ArrayList;

public class LobbyManager extends BaseModel {
  private Lobby selectedLobby;
  private ArrayList<Lobby> availableLobbies;

  public LobbyManager() {
    this.selectedLobby = null;
    this.availableLobbies = new ArrayList<>();
  }

  public void createLobby(String lobbyName) {
    Lobby newLobby = new Lobby(lobbyName, Labyrinth.EnvironmentType.CLIENT);
    getAvailableLobbies().add(newLobby);
  }

  public void setAvailableLobbies(ArrayList<Lobby> availableLobbies) {
    this.availableLobbies = availableLobbies;
    fireChangeListener();
  }

  public ArrayList<Lobby> getAvailableLobbies() {
    return availableLobbies;
  }

  public void setSelectedLobby(Lobby currentLobby) {
    this.selectedLobby = currentLobby;
    fireChangeListener();
  }

  public Lobby getSelectedLobby() {
    return selectedLobby;
  }

  public void setGameInProgress() {
    getSelectedLobby().setIsGameInProgress(true);
    fireChangeListener();
  }

  public void addPlayerToLobby(Lobby lobby, Player player) {
    if (lobby != null) {
      lobby.addPlayer(player);
      fireChangeListener();
    }
  }

  public void startGame() {
    if (getSelectedLobby() != null) {
      getSelectedLobby().startGame();
      fireChangeListener();
    }
  }

  public void removePlayerFromSelectedLobby(Player player) {
    if (getSelectedLobby() != null) {
      getSelectedLobby().removePlayer(player);
      fireChangeListener();
    }
  }
}
