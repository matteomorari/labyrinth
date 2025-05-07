package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.client.controllers.LabyrinthLocalController;
import it.unibs.pajc.labyrinth.client.views.GamePnl;
import it.unibs.pajc.labyrinth.client.views.StartPnl;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.utility.LabyrinthGson;

import java.awt.BorderLayout;
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
    EventQueue.invokeLater(() -> {
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
    frame.setLayout(new BorderLayout(10, 10));

    final boolean LOAD_FROM_FILE = false;

    if (LOAD_FROM_FILE) {
      String deepCopy = "";
      try {
        deepCopy = new String(Files.readAllBytes(Paths.get("modelCOpy.json")), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
      }

      Labyrinth labyrinthModel = LabyrinthGson.fromJson(deepCopy);
      LabyrinthLocalController controller = new LabyrinthLocalController(labyrinthModel);
      JPanel tempPnl = new JPanel();
      tempPnl.setLayout(new BorderLayout());

      GamePnl gamePanel = new GamePnl(controller);
      labyrinthModel.addChangeListener(e -> gamePanel.repaint());
      tempPnl.add(gamePanel, BorderLayout.CENTER);
      tempPnl.setVisible(true);
      JButton button = new JButton("move bot");
      tempPnl.add(button, BorderLayout.SOUTH);
      button.addActionListener(e -> labyrinthModel.startBotPlayerTurn());
      frame.add(tempPnl, BorderLayout.CENTER);
    } else {
      frame.add(new StartPnl(), BorderLayout.CENTER);
    }

  }
}
