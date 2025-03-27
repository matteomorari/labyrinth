package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.client.gameView.GamePnl;
import it.unibs.pajc.labyrinth.core.Bot;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.OnlineGameManager;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.PlayerColor;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class LocalOnlineGameSelectionPnl extends JPanel {

  private JButton localGameBtn;
  private JButton onlineGameBtn;

  public LocalOnlineGameSelectionPnl() {
    initializeComponents();
  }

  private void initializeComponents() {
    // Set up the panel layout
    setLayout(new GridBagLayout());
    setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(15, 15, 15, 15);

    // Create buttons with appropriate styling
    localGameBtn = new JButton("Create Local Game");
    onlineGameBtn = new JButton("Find Online Game");

    // Style the buttons
    Font buttonFont = new Font("Arial", Font.BOLD, 16);
    Dimension buttonSize = new Dimension(250, 60);

    localGameBtn.setFont(buttonFont);
    onlineGameBtn.setFont(buttonFont);

    localGameBtn.setPreferredSize(buttonSize);
    onlineGameBtn.setPreferredSize(buttonSize);

    // Add action listeners
    localGameBtn.addActionListener(e -> createLocalGame());
    onlineGameBtn.addActionListener(e -> findOnlineGame());

    // Add buttons to panel
    add(localGameBtn, gbc);
    add(onlineGameBtn, gbc);
  }

  /** Creates a new local game and displays the game panel */
  private void createLocalGame() {
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
      labyrinthModel = new Labyrinth();
      Player player1 = new Player(PlayerColor.RED);
      labyrinthModel.addPlayer(player1);

      Player player2 = new Player(PlayerColor.BLACK);
      labyrinthModel.addPlayer(player2);

      Player player3 = new Player(PlayerColor.PINK);
      labyrinthModel.addPlayer(player3);

      Player player4 = new Player(PlayerColor.GREEN);
      labyrinthModel.addPlayer(player4);
    }
    LabyrinthLocalController controller = new LabyrinthLocalController(labyrinthModel);
    if (!LOAD_FROM_FILE) {
      labyrinthModel.initGame();
    }

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

  /** Opens the online game finder UI */
  private void findOnlineGame() {
    OnlineGameManager onlineGameManager = new OnlineGameManager();

    LabyrinthClientController clientCntrl = new LabyrinthClientController(onlineGameManager);
    clientCntrl.connect("localhost", 1234);

    // Wait until the protocol thread is initialized
    synchronized (clientCntrl) {
      while (!clientCntrl.isInitialized()) {
        try {
          clientCntrl.wait();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          System.err.println("Interrupted while waiting for protocol thread initialization.");
        }
      }
    }

    FindOnlineGamePnl findOnlineGamePnl = new FindOnlineGamePnl(clientCntrl);
    onlineGameManager.addChangeListener(e -> findOnlineGamePnl.updateData());
    JPanel parent = (JPanel) getParent();
    parent.removeAll();
    parent.setLayout(new BorderLayout());
    parent.add(findOnlineGamePnl, BorderLayout.CENTER);
    parent.revalidate();
    parent.repaint();
  }
}
