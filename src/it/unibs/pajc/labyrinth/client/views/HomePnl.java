package it.unibs.pajc.labyrinth.client.views;

import io.github.cdimascio.dotenv.Dotenv;
import it.unibs.pajc.labyrinth.client.components.LogoPanel;
import it.unibs.pajc.labyrinth.client.components.SvgIconButton;
import it.unibs.pajc.labyrinth.client.controllers.ClientSocketProtocol;
import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.client.controllers.lobby.LobbyClientController;
import it.unibs.pajc.labyrinth.client.controllers.lobby.LobbyLocalController;
import it.unibs.pajc.labyrinth.core.lobby.LobbyManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class HomePnl extends JPanel {
  public HomePnl() {
    setLayout(new BorderLayout());

    // logo panel
    BufferedImage logo = ImageCntrl.LOGO.getImage();
    LogoPanel logoPanel = new LogoPanel(logo);
    add(logoPanel, BorderLayout.NORTH);

    // Center panel for buttons
    JPanel centerPanel = new JPanel(new GridBagLayout());
    centerPanel.setOpaque(false);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(20, 20, 20, 20);
    gbc.fill = GridBagConstraints.NONE; // Allow buttons to size naturally
    gbc.weightx = 0.0; // No horizontal stretching
    gbc.weighty = 1.0; // Allow vertical centering
    gbc.anchor = GridBagConstraints.CENTER; // Center the buttons

    // "Local" button
    SvgIconButton localButton = new SvgIconButton("resource\\icons\\computer.svg", null);
    localButton.setBorderRadius(20);
    localButton.setButtonSize(200, 50);
    localButton.setSvgIconSize(150, 150);
    localButton.addActionListener(e -> createLocalGame());
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.ipadx = 50;
    gbc.insets = new Insets(20, 20, 20, 40); // Add space to the right of the button
    centerPanel.add(localButton, gbc);

    // "Online" button
    SvgIconButton onlineButton = new SvgIconButton("resource\\icons\\online.svg", null);
    onlineButton.setBorderRadius(20);
    onlineButton.setButtonSize(200, 50);
    onlineButton.setSvgIconSize(150, 150);
    onlineButton.addActionListener(e -> findOnlineGame());
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.insets = new Insets(20, 40, 20, 20);
    centerPanel.add(onlineButton, gbc);

    add(centerPanel, BorderLayout.CENTER);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();

    int width = getWidth();
    int height = getHeight();

    GradientPaint gradientPaint =
        new GradientPaint(
            (int) (0 - width * 0.5),
            height,
            Color.YELLOW,
            (int) (width * 1.5),
            (int) (0 - width * 0.5),
            Color.RED);
    g2d.setPaint(gradientPaint);
    g2d.fillRect(0, 0, width, height);

    g2d.dispose();
  }

  /** Creates a new local game and displays the game panel */
  private void createLocalGame() {
    LobbyManager onlineGameManager = new LobbyManager();
    LobbyLocalController lobbyLocalController = new LobbyLocalController(onlineGameManager);
    lobbyLocalController.createLobby("Local Game");
    LocalGameLobbyPnl localGameLobbyPnl = new LocalGameLobbyPnl(lobbyLocalController);
    onlineGameManager.addChangeListener(
        e -> {
          localGameLobbyPnl.update();
        });
    replaceParentPnlContent(localGameLobbyPnl);
  }

  /** Opens the online game finder UI */
  private void findOnlineGame() {
    Dotenv dotenv = Dotenv.load();
    String serverIp = dotenv.get("SERVER_IP", "localhost");
    int serverPort = Integer.parseInt(dotenv.get("SERVER_PORT", "2234"));

    ClientSocketProtocol clientSocketController = new ClientSocketProtocol();
    clientSocketController.connect(serverIp, serverPort);

    // Wait until the protocol thread is initialized
    // TODO: is synchronized needed here?
    synchronized (clientSocketController) {
      while (!clientSocketController.isInitialized()) {
        try {
          clientSocketController.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          System.err.println("Interrupted while waiting for protocol thread initialization.");
        }
      }
    }

    // Wait for the player to be set (new_player command received)
    try {
      LobbyManager onlineGameManager = new LobbyManager();
      LobbyClientController lobbyClientController =
          new LobbyClientController(clientSocketController, onlineGameManager);
      lobbyClientController.waitForPlayer();
      FindOnlineGamePnl findOnlineGamePnl = new FindOnlineGamePnl(lobbyClientController);
      onlineGameManager.addChangeListener(
          e -> {
            findOnlineGamePnl.updateData();
          });
      lobbyClientController.fetchLobby();
      replaceParentPnlContent(findOnlineGamePnl);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.err.println("Interrupted while waiting for player data from server.");
    }
  }

  private void replaceParentPnlContent(JPanel pnl) {
    JPanel parent = (JPanel) getParent();
    parent.removeAll();
    parent.setLayout(new BorderLayout());
    parent.add(pnl, BorderLayout.CENTER);
    parent.revalidate();
    parent.repaint();
  }
}
