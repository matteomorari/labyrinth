package it.unibs.pajc.labyrinth.core;

import java.util.ArrayList;

public class OnlineGameManager extends BaseModel {
  private GameLobby selectedLobby;
  private ArrayList<GameLobby> availableLobbies;

  public OnlineGameManager() {
    this.selectedLobby = null;
    this.availableLobbies = new ArrayList<>();
  }

  public void createLobby(String lobbyName) {
    Labyrinth labyrinth = new Labyrinth();
    GameLobby newLobby = new GameLobby(labyrinth, lobbyName);
    this.availableLobbies.add(newLobby);
  }

  public void setAvailableLobbies(ArrayList<GameLobby> availableLobbies) {
    this.availableLobbies = availableLobbies;
    this.fireChangeListener();
  }

  public ArrayList<GameLobby> getAvailableLobbies() {
    return availableLobbies;
  }

  public void setSelectedLobby(GameLobby currentLobby) {
    this.selectedLobby = currentLobby;
    this.fireChangeListener();
  }

  public GameLobby getSelectedLobby() {
    return selectedLobby;
  }
}
