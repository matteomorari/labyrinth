package it.unibs.pajc.labyrinth.client.views;

import it.unibs.pajc.labyrinth.client.components.LobbyPnl;
import it.unibs.pajc.labyrinth.client.controllers.labyrinth.LabyrinthLocalController;
import it.unibs.pajc.labyrinth.client.controllers.lobby.LobbyLocalController;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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

    // Title label
    JLabel titleLabel = new JLabel("LABIRINTO", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
    titleLabel.setForeground(Color.BLUE);
    titleLabel.setPreferredSize(new Dimension(0, 200));
    titleLabel.setBackground(Color.YELLOW); // TODO: to remove
    // titleLabel.setOpaque(true);
    add(titleLabel, BorderLayout.NORTH);

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
    addPlayerButton.addActionListener(
        e -> {
          addNewPlayer();
        });
    buttonPanel.add(addPlayerButton);

    // Add bot button
    addBotButton = new JButton("ADD BOT");
    addBotButton.setPreferredSize(new Dimension(200, 50));
    addBotButton.addActionListener(
        e -> {
          addNewBot();
        });
    buttonPanel.add(addBotButton);

    // Start game button
    buttonPanel.setPreferredSize(new Dimension(0, 60));
    startGameButton = new JButton("START GAME");
    startGameButton.setPreferredSize(new Dimension(200, 50));
    startGameButton.addActionListener(e -> startGame());
    buttonPanel.add(startGameButton);
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
