package it.unibs.pajc.labyrinth.core.clientServerCommon;

import com.google.gson.JsonObject;
import it.unibs.pajc.labyrinth.server.LabyrinthServerProtocol;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class SocketCommunicationProtocol {
  protected Socket remoteHost;
  protected BufferedReader inputStream;
  protected PrintWriter outputStream;
  protected HashMap<String, Consumer<LabyrinthEvent>> commandMap;
  protected boolean isRunning = false;
  private static ArrayList<SocketCommunicationProtocol> connectedPlayers = new ArrayList<>();

  public SocketCommunicationProtocol(Socket client) {
    commandMap = new HashMap<>();
    connect(client);
    connectedPlayers.add(this);
  }

  public void connect(Socket client) {
    this.remoteHost = client;
  }

  public void run() {

    try {
      inputStream = new BufferedReader(new InputStreamReader(remoteHost.getInputStream()));

      outputStream = new PrintWriter(remoteHost.getOutputStream(), true);

      System.out.printf("Player collegato\n");

      isRunning = true;
      String request;
      while (isRunning && (request = inputStream.readLine()) != null) {
        System.out.printf("Processing request: %s\n", request);

        LabyrinthEvent e = new LabyrinthEvent(this, request);

        Consumer<LabyrinthEvent> commandExe =
            e.getCommand() != null && commandMap.containsKey(e.getCommand())
                ? commandMap.get(e.getCommand())
                : commandMap.get("@debug@"); // TODO: ??

        if (commandExe != null) commandExe.accept(e);
        else System.out.println("comando non riconosciuto");
      }

      System.out.printf("Collegamento terminato\n");
      connectedPlayers.remove(this);
      close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private synchronized void close() {
    try {
      isRunning = false;
      outputStream.close();
      inputStream.close();
      remoteHost.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      outputStream = null;
      inputStream = null;
      remoteHost = null;
    }
  }

  public void stopAndExit() {
    isRunning = false;
  }

  public synchronized void sendMsg(SocketCommunicationProtocol sender, String msg) {
    if (outputStream != null) {
      System.out.printf("invio messaggio: %s\n", msg);
      outputStream.println(msg);
      outputStream.flush();
    }
  }

  public static String createMessage(String comand, JsonObject parameters) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", comand);
    msg.add("parameters", parameters);
    return msg.toString();
  }

  public static ArrayList<SocketCommunicationProtocol> getConnectedPlayers() {
    return connectedPlayers;
  }
}
