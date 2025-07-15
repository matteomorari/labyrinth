package it.unibs.pajc.labyrinth.client.controllers;

import it.unibs.pajc.labyrinth.core.clientServerCommon.SocketCommunicationProtocol;
import java.net.Socket;

public class ClientSocketProtocol extends SocketCommunicationProtocol {

  public ClientSocketProtocol() {
    super(null);
  }

  public boolean connect(String serverAddress, int serverPort) {
    try {
      remoteHost = new Socket(serverAddress, serverPort);
      new Thread(this::run).start();
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }
}
