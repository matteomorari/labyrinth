package it.unibs.pajc.labyrinth.client.views;

import it.unibs.pajc.labyrinth.client.components.LobbyPnl;
import it.unibs.pajc.labyrinth.client.components.LogoPanel;
import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.client.controllers.labyrinth.LabyrinthLocalController;
import it.unibs.pajc.labyrinth.client.controllers.lobby.LobbyLocalController;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class LocalGameLobbyPnl extends JPanel {
  private LobbyLocalController lobbyController;
  private LobbyPnl lobbyPnl;
  private JButton startGameButton;
  private JButton addPlayerButton;
  private JButton addBotButton;

  public LocalGameLobbyPnl(LobbyLocalController lobbyController) {
    this.lobbyController = lobbyController;
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Logo panel
    BufferedImage logo = ImageCntrl.LOGO.getImage();
    LogoPanel logoPanel = new LogoPanel(logo);
    add(logoPanel, BorderLayout.NORTH);

    // lobby panel
    lobbyPnl = new LobbyPnl(lobbyController);
    add(lobbyPnl, BorderLayout.CENTER);

    // add 2 players (the minimum to start a game)
    addNewPlayer();
    // addNewPlayer();
    addNewBot();
    // addNewBot();

    // Action buttons panel
    JPanel buttonPanel = new JPanel();

    // Add player button
    addPlayerButton = new JButton("ADD PLAYER");
    addPlayerButton.setPreferredSize(new Dimension(200, 50));
    addPlayerButton.addActionListener((ActionEvent e) -> addNewPlayer());
    buttonPanel.add(addPlayerButton);

    // Add bot button
    addBotButton = new JButton("ADD BOT");
    addBotButton.setPreferredSize(new Dimension(200, 50));
    addBotButton.addActionListener((ActionEvent e) -> addNewBot());
    buttonPanel.add(addBotButton);

    // Start game button
    buttonPanel.setPreferredSize(new Dimension(0, 60));
    startGameButton = new JButton("START GAME");
    startGameButton.setPreferredSize(new Dimension(200, 50));
    startGameButton.addActionListener(e -> startGame());
    buttonPanel.add(startGameButton);
    buttonPanel.setOpaque(false);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  public void update() {
    Lobby selectedLobby = lobbyController.getSelectedLobby();
    if (selectedLobby.isGameInProgress()) {
      Labyrinth labyrinthModel = selectedLobby.getModel();
      LabyrinthLocalController labyrinthController = new LabyrinthLocalController(labyrinthModel);

      GamePnl gamePanel = new GamePnl(labyrinthController);
      labyrinthModel.addChangeListener(e -> gamePanel.update());

      // Replace the current panel's content with the game panel
      JPanel parent = (JPanel) getParent();
      parent.removeAll();
      parent.setLayout(new BorderLayout());
      parent.add(gamePanel, BorderLayout.CENTER);
      parent.revalidate();
      parent.repaint();

      // if the first player is a bot
      if (labyrinthModel.getCurrentPlayer().isBot()) {
        labyrinthModel.startBotPlayerTurn();
      }
    } else {
      lobbyPnl.update();
    }
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

  private void addNewPlayer() {
    Player newPlayer = new Player();
    newPlayer.setIsReadyToPlay(true);
    lobbyController.addPlayer(newPlayer);
  }

  private void addNewBot() {
    Player newPlayer = new Player();
    newPlayer.setIsBot(true);
    newPlayer.setIsReadyToPlay(true);
    lobbyController.addPlayer(newPlayer);
  }

  private void startGame() {
    lobbyController.startGame();
  }
}
