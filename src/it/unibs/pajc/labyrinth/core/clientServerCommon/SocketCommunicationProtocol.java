package it.unibs.pajc.labyrinth.core.clientServerCommon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;

public class SocketCommunicationProtocol {
  protected Socket remoteHost;
  protected BufferedReader inputStream;
  protected PrintWriter outputStream;
  protected static HashMap<String, Consumer<LabyrinthEvent>> commandMap;
  protected boolean isRunning = false;

  public SocketCommunicationProtocol(Socket client) {
    connect(client);
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
                : commandMap.get("@debug@");

        if (commandExe != null) commandExe.accept(e);
        else System.out.println("comando non riconosciuto");
      }

      System.out.printf("Collegamento terminato\n");
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

  public String createMessage(String command) {
    return String.format("@%s", command);
  }

  public String createMessage(String command, String... params) {
    return String.format("@%s:%s", command, String.join(":", params));
  }
}
