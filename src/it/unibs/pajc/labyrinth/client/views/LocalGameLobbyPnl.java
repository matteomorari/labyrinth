package it.unibs.pajc.labyrinth.client.views;

import it.unibs.pajc.labyrinth.client.components.LobbyPnl;
import it.unibs.pajc.labyrinth.client.controllers.LabyrinthLocalController;
import it.unibs.pajc.labyrinth.core.Bot;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LocalGameLobbyPnl extends JPanel {
  private Lobby lobby;
  private LobbyPnl lobbyPnl;
  private JButton startGameButton;
  private JButton addPlayerButton;
  private JButton addBottButton; // TODO: to implement

  public LocalGameLobbyPnl() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    lobby = new Lobby("Local Game");

    // Title label
    JLabel titleLabel = new JLabel("LABIRINTO", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
    titleLabel.setForeground(Color.BLUE);
    titleLabel.setPreferredSize(new Dimension(0, 200));
    titleLabel.setBackground(Color.YELLOW); // TODO: to remove
    titleLabel.setOpaque(true);
    add(titleLabel, BorderLayout.NORTH);

    // lobby panel
    lobbyPnl = new LobbyPnl();
    add(lobbyPnl, BorderLayout.CENTER);
    lobbyPnl.setLobby(lobby);

    // add 2 players (the minimum to start a game)
    addNewPlayer();
    addNewPlayer();

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

    // Start game button
    buttonPanel.setPreferredSize(new Dimension(0, 60));
    startGameButton = new JButton("START GAME");
    startGameButton.setPreferredSize(new Dimension(200, 50));
    startGameButton.addActionListener(e -> startGame());
    buttonPanel.add(startGameButton);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  private void addNewPlayer() {
    Player newPlayer = new Player();
    newPlayer.setIsReadyToPlay(true);
    lobby.addPlayer(newPlayer);
    lobbyPnl.update(lobby);
  }

  private void startGame() {
    Labyrinth labyrinthModel;
    final boolean LOAD_FROM_FILE = false;
    if (LOAD_FROM_FILE) {
      String deepCopy = "";
      try {
        deepCopy =
            new String(Files.readAllBytes(Paths.get("modelCOpy.json")), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
      }

      labyrinthModel = LabyrinthGson.fromJson(deepCopy);
    } else {
      lobby.startGame();
      labyrinthModel = lobby.getModel();
    }
    LabyrinthLocalController controller = new LabyrinthLocalController(labyrinthModel);
    // if (!LOAD_FROM_FILE) {
    //   labyrinthModel.initGame();
    // }

    // TODO: to remove
    JPanel tempPnl = new JPanel();
    tempPnl.setLayout(new BorderLayout());

    GamePnl gamePanel = new GamePnl(controller);
    labyrinthModel.addChangeListener(e -> gamePanel.repaint());
    tempPnl.add(gamePanel, BorderLayout.CENTER);
    tempPnl.setVisible(true);

    // TODO: to remove
    Bot bot1 = new Bot(labyrinthModel, labyrinthModel.getCurrentPlayer());
    JButton button = new JButton("move bot");
    tempPnl.add(button, BorderLayout.SOUTH);
    button.addActionListener(e -> bot1.calcMove());

    // Replace the current panel's content with the game panel
    JPanel parent = (JPanel) getParent();
    parent.removeAll();
    parent.setLayout(new BorderLayout());
    parent.add(tempPnl, BorderLayout.CENTER);
    parent.revalidate();
    parent.repaint();
  }
}
