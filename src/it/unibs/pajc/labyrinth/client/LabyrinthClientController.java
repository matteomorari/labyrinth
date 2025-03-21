package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.core.Card;
import it.unibs.pajc.labyrinth.core.Goal;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.LabyrinthController;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import it.unibs.pajc.labyrinth.core.utility.Position;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

public class LabyrinthClientController extends SocketCommunicationProtocol
    implements LabyrinthController {

  Labyrinth model;
  Player player;

  static {
    commandMap = new HashMap<>();

    commandMap.put(
        "new_player",
        e -> {
          try {
            LabyrinthClientController cntrl = (LabyrinthClientController) e.getSender();
            
          } catch (Exception exc) {

            exc.printStackTrace();
          }
        });
  }

  public LabyrinthClientController() {
    super(null);
    this.model = new Labyrinth();
  }

  public boolean connect(String serverAddress, int serverPort) {
    try {
      remoteHost = new Socket(serverAddress, serverPort);
      new Thread(this::run).start();
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public void initGame() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'initGame'");
  }

  @Override
  public int getBoardSize() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getBoardSize'");
  }

  @Override
  public ArrayList<ArrayList<Card>> getBoard() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getBoard'");
  }

  @Override
  public Player getCurrentPlayer() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCurrentPlayer'");
  }

  @Override
  public ArrayDeque<Player> getPlayers() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getPlayers'");
  }

  @Override
  public void movePlayer(int row, int col) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'movePlayer'");
  }

  @Override
  public void insertCard(Position position) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertCard'");
  }

  @Override
  public ArrayList<Position> getLastPlayerMovedPath() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getLastPlayerMovedPath'");
  }

  @Override
  public Position lastInsertedCardPosition() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'lastInsertedCardPosition'");
  }

  @Override
  public Card getAvailableCard() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAvailableCard'");
  }

  @Override
  public void swapPlayers(Player player) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'swapPlayers'");
  }

  @Override
  public void changeGoal(Goal goal) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'changeGoal'");
  }

  @Override
  public void changeSecondGoal() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'changeSecondGoal'");
  }

  @Override
  public void nextPlayer() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'nextPlayer'");
  }

  @Override
  public boolean getHasCurrentPlayerInserted() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getHasCurrentPlayerInserted'");
  }

  @Override
  public boolean getHasCurrentPlayerDoubleTurn() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getHasCurrentPlayerDoubleTurn'");
  }

  @Override
  public void setHasCurrentPlayerDoubleTurn(boolean hasDoubleTurn) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setHasCurrentPlayerDoubleTurn'");
  }

  @Override
  public void setHasCurrentPlayerInserted(boolean hasInserted) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setHasCurrentPlayerInserted'");
  }

  @Override
  public void skipTurn() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'skipTurn'");
  }
}
