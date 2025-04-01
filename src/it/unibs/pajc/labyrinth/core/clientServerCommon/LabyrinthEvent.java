package it.unibs.pajc.labyrinth.core.clientServerCommon;

public class LabyrinthEvent extends SocketEvent<SocketCommunicationProtocol> {

  public LabyrinthEvent(SocketCommunicationProtocol sender, String message) {
    super(sender, message);
  }
}
