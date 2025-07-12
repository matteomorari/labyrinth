package it.unibs.pajc.labyrinth.core.clientServerCommon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SocketEvent<T> {

  private static final String COMMAND_KEY = "command";
  private static final String PARAMETERS_KEY = "parameters";

  protected T sender;
  protected JsonObject message;
  protected String command;
  protected JsonObject parameters;

  public SocketEvent(T sender, String message) {
    this.sender = sender;
    this.message = JsonParser.parseString(message).getAsJsonObject();
    this.command = this.message.get(COMMAND_KEY).getAsString();
    if (this.message.has(PARAMETERS_KEY) && !this.message.get(PARAMETERS_KEY).isJsonNull()) {
      this.parameters = this.message.getAsJsonObject(PARAMETERS_KEY);
    }
  }

  public static String createMessage(String command, JsonObject parameters) {
    JsonObject msg = new JsonObject();
    msg.addProperty(COMMAND_KEY, command);
    if (parameters != null) {
      msg.add(PARAMETERS_KEY, parameters);
    }
    return msg.toString();
  }

  public T getSender() {
    return sender;
  }

  public String getCommand() {
    return command;
  }

  public JsonObject getMessage() {
    return message;
  }

  public JsonObject getParameters() {
    return parameters;
  }

  public String toString() {
    return message.toString();
  }
}
