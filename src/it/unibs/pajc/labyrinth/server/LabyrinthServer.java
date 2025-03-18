package it.unibs.pajc.labyrinth.server;

import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class LabyrinthServer {
  private ArrayList<GameLobby> games = new ArrayList<>();
  private ArrayList<SocketCommunicationProtocol> playersInGameSearch = new ArrayList<>();

  public static void main(String[] args) {
    LabyrinthServer trisServer = new LabyrinthServer();
    try (ServerSocket server = new ServerSocket(1234)) {
      log("server started");

      while (true) {
        SocketCommunicationProtocol p = trisServer.addPlayer(server.accept());
        log("client connected [%s]", p);
      }

    } catch (Exception exc) {
      exc.printStackTrace();
    }

    log("server closed!");
  }

  private static void log(String format, Object... params) {
    System.out.printf("[%s] %s\n", LocalDateTime.now(), String.format(format, params));
  }

  public SocketCommunicationProtocol addPlayer(Socket playerSocket) {
    // TrisGame game = findFreeGame();
    Player player = new Player(null, null);

    LabyrinthServerProtocol playerProtocol = new LabyrinthServerProtocol(player, playerSocket);

    new Thread(() -> playerProtocol.run()).start();
    // game.addPlayerConnection(playerProtocol);

    return playerProtocol;
  }
}
