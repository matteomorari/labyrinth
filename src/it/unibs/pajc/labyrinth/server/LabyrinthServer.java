package it.unibs.pajc.labyrinth.server;

import io.github.cdimascio.dotenv.Dotenv;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;

public class LabyrinthServer {
  public static void main(String[] args) {
    LabyrinthServer trisServer = new LabyrinthServer();
    Dotenv dotenv = Dotenv.load();
    int serverPort = Integer.parseInt(dotenv.get("SERVER_PORT", "2234"));
    try (ServerSocket server = new ServerSocket(serverPort)) {
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
    System.out.printf("[%s] %s%n", LocalDateTime.now(), String.format(format, params));
  }

  public SocketCommunicationProtocol addPlayer(Socket playerSocket) {
    Player player = new Player();
    LabyrinthServerProtocol playerProtocol = new LabyrinthServerProtocol(player, playerSocket);

    new Thread(playerProtocol::run).start();

    // Wait until the protocol thread is initialized
    synchronized (playerProtocol) {
      while (!playerProtocol.isInitialized()) {
        try {
          playerProtocol.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          System.err.println("Interrupted while waiting for protocol thread initialization.");
        }
      }
    }

    playerProtocol.sendNewPlayerMsg();

    return playerProtocol;
  }
}
