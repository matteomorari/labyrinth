package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.client.controllers.labyrinth.LabyrinthLocalController;
import it.unibs.pajc.labyrinth.client.views.GamePnl;
import it.unibs.pajc.labyrinth.client.views.HomePnl;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

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
    frame.setBounds(20, 20, 1400, 850);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout(10, 10));

    // for debug purposes
    final boolean LOAD_FROM_FILE = false;
    if (LOAD_FROM_FILE) {
      String deepCopy = "";
      try {
        deepCopy =
            new String(Files.readAllBytes(Paths.get("modelCopy.json")), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
      }

      Labyrinth labyrinthModel = LabyrinthGson.fromJson(deepCopy);
      LabyrinthLocalController controller = new LabyrinthLocalController(labyrinthModel);
      JPanel tempPnl = new JPanel();
      tempPnl.setLayout(new BorderLayout());

      GamePnl gamePanel = new GamePnl(controller);
      labyrinthModel.addChangeListener(e -> gamePanel.update());

      frame.add(gamePanel, BorderLayout.CENTER);
    } else {
      frame.add(new HomePnl(), BorderLayout.CENTER);
    }
  }
}
