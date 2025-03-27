package it.unibs.pajc.labyrinth.core.clientServerCommon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SocketEvent<T> {

  protected T sender;
  protected JsonObject message;
  protected String command;
  protected JsonObject parameters;

  public SocketEvent(T sender, String message) {
    this.sender = sender;
    this.message = JsonParser.parseString(message).getAsJsonObject();
    this.command = this.message.get("command").getAsString();
    if (this.message.has("parameters") && !this.message.get("parameters").isJsonNull()) {
      this.parameters = this.message.getAsJsonObject("parameters");
    }
  }

  public static String createMessage(String comand, JsonObject parameters) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", comand);
    if (parameters != null) {
      msg.add("parameters", parameters);
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
