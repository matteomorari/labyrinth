package it.unibs.pajc.labyrinth.core.clientServerCommon;

import java.util.ArrayList;
import java.util.Arrays;

public class SocketEvent<T> {

  protected T sender;
  protected String message;
  protected String command;
  protected ArrayList<String> parameters = new ArrayList<>();

  public SocketEvent(T sender, String message) {
    this.sender = sender;
    this.message = message;
    this.parameters = parseParameters(message);
    if (parameters != null && parameters.size() > 0) {
      this.command = parameters.get(0);
      this.parameters.remove(0);
    }
  }

  private static ArrayList<String> parseParameters(String message) {
    ArrayList<String> parameters = new ArrayList<String>();

    if (message.startsWith("@")) {

      String[] tokens = message.split(":");
      parameters = new ArrayList<String>();
      parameters.addAll(Arrays.asList(tokens));
    }

    return parameters;
  }

  public static String createMessage(String command, String... params) {
    return String.format("@%s", command, String.join(":", params));
  }

  public T getSender() {
    return sender;
  }

  public String getCommand() {
    return command;
  }

  public String getMessage() {
    return message;
  }

  public ArrayList<String> getParameters() {
    return parameters;
  }

  public int getParametersCount() {
    return parameters.size();
  }

  public String getParameter(int indx) {
    return parameters.size() > indx ? parameters.get(indx) : "";
  }

  public String toString() {
    return String.format("[%s]: %s", command, String.join(",", parameters));
  }
}
