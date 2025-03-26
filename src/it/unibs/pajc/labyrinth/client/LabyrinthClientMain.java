package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.client.gameView.GamePnl;
import it.unibs.pajc.labyrinth.core.Bot;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.PlayerColor;
import it.unibs.pajc.labyrinth.core.utility.MyGson;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LabyrinthClientMain {

  private JFrame frame;

  /** Launch the application. */
  public static void main(String[] args) {
    EventQueue.invokeLater(
        () -> {
          try {
            LabyrinthClientMain window = new LabyrinthClientMain();
            window.frame.setVisible(true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }

  /** Create the application. */
  public LabyrinthClientMain() {
    initialize();
  }

  /** Initialize the contents of the frame. */
  private void initialize() {
    frame = new JFrame("Labyrinth");
    frame.setBounds(20, 20, 1400, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.setResizable(false);
    frame.setLayout(new BorderLayout(10, 10));

    final boolean LOAD_FROM_FILE = false;
    Labyrinth labyrinthModel;

    if (LOAD_FROM_FILE) {
      String deepCopy = "";
      try {
        deepCopy =
            new String(Files.readAllBytes(Paths.get("modelCOpy.json")), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
      }

      MyGson myGson = new MyGson();
      labyrinthModel = myGson.fromJson(deepCopy);
    } else {
      labyrinthModel = new Labyrinth();
      Player player1 = new Player(PlayerColor.WHITE);
      labyrinthModel.addPlayer(player1);

      Player player2 = new Player(PlayerColor.SKYBLUE);
      labyrinthModel.addPlayer(player2);

      Player player3 = new Player(PlayerColor.RED);
      labyrinthModel.addPlayer(player3);

      Player player4 = new Player(PlayerColor.BROWN);
      labyrinthModel.addPlayer(player4);
    }

    LabyrinthLocalController controller = new LabyrinthLocalController(labyrinthModel);
    if (!LOAD_FROM_FILE) {
      controller.initGame();
    }

    JPanel gamePanel = new GamePnl(controller);
    labyrinthModel.addChangeListener(e -> gamePanel.repaint());
    frame.add(gamePanel, BorderLayout.CENTER);
    frame.setVisible(true);

    // TODO: to remove
    Bot bot1 = new Bot(labyrinthModel, labyrinthModel.getCurrentPlayer());
    JButton button = new JButton("move bot");
    frame.add(button, BorderLayout.SOUTH);
    button.addActionListener(e -> bot1.calcMove());
  }
}
