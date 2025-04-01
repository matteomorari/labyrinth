package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.client.gameView.GamePnl;
import it.unibs.pajc.labyrinth.client.gameView.RoundedIconButton;
import it.unibs.pajc.labyrinth.core.Bot;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.OnlineGameManager;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.PlayerColor;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LocalOnlineGameSelectionPnl extends JPanel {

  public LocalOnlineGameSelectionPnl() {
    setLayout(new BorderLayout());

    // Title label at the top
    JLabel titleLabel = new JLabel("LABIRINTO", SwingConstants.CENTER);
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    add(titleLabel, BorderLayout.NORTH);

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
    RoundedIconButton localButton = new RoundedIconButton("resource\\images\\rotate.svg", "LOCAL");
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
    RoundedIconButton onlineButton = new RoundedIconButton("resource\\images\\rotate.svg", "ONLINE");
    onlineButton.setBorderRadius(20);
    onlineButton.setButtonSize(200, 50);
    onlineButton.setSvgIconSize(150, 150);
    onlineButton.addActionListener(e -> findOnlineGame());
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.insets = new Insets(20, 40, 20, 20); // Add space to the left of the button
    centerPanel.add(onlineButton, gbc);

    add(centerPanel, BorderLayout.CENTER);
  }

  /** Custom paintComponent method to apply a gradient background. */
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();

    int width = getWidth();
    int height = getHeight();

    GradientPaint gradientPaint =
        new GradientPaint(-100, height/2, Color.RED, width/2, height/2, Color.YELLOW);
    g2d.setPaint(gradientPaint);
    g2d.fillRect(0, 0, width, height);

    g2d.dispose();
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
