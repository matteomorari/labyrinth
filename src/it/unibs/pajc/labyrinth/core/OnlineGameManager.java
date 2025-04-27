package it.unibs.pajc.labyrinth.core;

import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import it.unibs.pajc.labyrinth.core.lobby.OnlineLobby;

import java.util.ArrayList;

public class OnlineGameManager extends BaseModel {
  private Lobby selectedLobby;
  private ArrayList<Lobby> availableLobbies;

  public OnlineGameManager() {
    this.selectedLobby = null;
    this.availableLobbies = new ArrayList<>();
  }

  public void createLobby(String lobbyName) {
    Lobby newLobby = new OnlineLobby(lobbyName);
    this.availableLobbies.add(newLobby);
  }

  public void setAvailableLobbies(ArrayList<Lobby> availableLobbies) {
    this.availableLobbies = availableLobbies;
    System.out.println("Available lobbies updated.");
    this.fireChangeListener();
  }

  public ArrayList<Lobby> getAvailableLobbies() {
    return availableLobbies;
  }

  public void setSelectedLobby(Lobby currentLobby) {
    this.selectedLobby = currentLobby;
    this.fireChangeListener();
  }

  public Lobby getSelectedLobby() {
    return selectedLobby;
  }

  public void setGameInProgress() {
    this.selectedLobby.setGameInProgress(true);
    this.fireChangeListener();
  }
}
