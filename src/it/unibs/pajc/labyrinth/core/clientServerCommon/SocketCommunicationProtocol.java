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

    try (BufferedReader in =
            new BufferedReader(new InputStreamReader(remoteHost.getInputStream()));
        PrintWriter out = new PrintWriter(remoteHost.getOutputStream(), true)) {
      this.inputStream = in;
      this.outputStream = out;

      System.out.print("Player collegato");

      isRunning = true;
      synchronized (this) {
        initialized = true;
        notifyAll();
      }
      String request;
      while (isRunning && (request = inputStream.readLine()) != null) {
        System.out.printf("Processing request: %s%n", request);

        LabyrinthEvent e = new LabyrinthEvent(this, request);

        Consumer<LabyrinthEvent> commandExe =
            e.getCommand() != null && commandMap.containsKey(e.getCommand())
                ? commandMap.get(e.getCommand())
                : null;

        if (commandExe != null) {
          commandExe.accept(e);
        } else {
          System.out.printf("comando non riconosciuto: %s%n", e.getCommand());
        }
      }

    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      disconnectUser();
      close();
    }
  }

  public void disconnectUser() {
    System.out.print("Collegamento terminato");
    connectedUsers.remove(this);
  }

  public boolean isInitialized() {
    return initialized;
  }

  private synchronized void close() {
    try {
      isRunning = false;
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
      System.out.printf("invio messaggio: %s%n", msg);
      outputStream.println(msg);
      outputStream.flush();
    } else {
      System.out.print("impossibile inviare messaggio, outputStream nullo");
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

  public HashMap<String, Consumer<LabyrinthEvent>> getCommandMap() {
    return commandMap;
  }

  public void addCommand(String command, Consumer<LabyrinthEvent> commandExe) {
    commandMap.put(command, commandExe);
  }
}
