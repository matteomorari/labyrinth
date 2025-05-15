package it.unibs.pajc.labyrinth.core.clientServerCommon;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class SocketCommunicationProtocol {
  protected Socket remoteHost;
  protected BufferedReader inputStream;
  protected PrintWriter outputStream;
  protected HashMap<String, Consumer<LabyrinthEvent>> commandMap;
  protected boolean isRunning = false;
  private static CopyOnWriteArrayList<SocketCommunicationProtocol> connectedUsers =
      new CopyOnWriteArrayList<>();
  private volatile boolean initialized = false;

  public SocketCommunicationProtocol(Socket client) {
    commandMap = new HashMap<>();
    connect(client);
    connectedUsers.add(this);
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
      synchronized (this) {
        initialized = true;
        notifyAll();
      }
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

    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      disconnectUser();
      close();
    }
  }

  public void disconnectUser() {
    System.out.printf("Collegamento terminato\n");
    connectedUsers.remove(this);
  }

  public boolean isInitialized() {
    return initialized;
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
    } else {
      System.out.printf("impossibile inviare messaggio, outputStream nullo\n");
    }
  }

  public static String createMessage(String comand, JsonObject parameters) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", comand);
    msg.add("parameters", parameters);
    return msg.toString();
  }

  public static CopyOnWriteArrayList<SocketCommunicationProtocol> getConnectedUsers() {
    return connectedUsers;
  }
}
