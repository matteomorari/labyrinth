package it.unibs.pajc.labyrinth.core.clientServerCommon;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;

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

  // private static ArrayList<String> parseParameters(String message) {
  //   ArrayList<String> parameters = new ArrayList<String>();

  //   if (message.startsWith("@")) {

  //     String[] tokens = message.split(":");
  //     parameters = new ArrayList<String>();
  //     parameters.addAll(Arrays.asList(tokens));
  //   }

  //   return parameters;
  // }

  public static String createMessage(String comand, JsonObject parameters) {
    JsonObject msg = new JsonObject();
    msg.addProperty("command", comand);
    if (parameters != null){
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

  // public int getParametersCount() {
  //   return parameters.size();
  // }

  // public String getParameter(int indx) {
  //   return parameters.size() > indx ? parameters.get(indx) : "";
  // }

  public String toString() {
    return message.toString();
  }
}
