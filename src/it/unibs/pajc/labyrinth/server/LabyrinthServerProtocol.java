package it.unibs.pajc.labyrinth.server;

import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import java.net.Socket;
import java.util.HashMap;

public class LabyrinthServerProtocol extends SocketCommunicationProtocol {
  private GameLobby game;
  private Player player;

  static {
    commandMap = new HashMap<>();

    commandMap.put(
        "@GET_STATUS",
        e -> {
          try {
            LabyrinthServerProtocol s = (LabyrinthServerProtocol) e.getSender();

            // implement the code here

          } catch (Exception exc) {

          }
        });
  }

  public LabyrinthServerProtocol(Player player, Socket client) {
    super(client);
    this.player = player;
  }
}
