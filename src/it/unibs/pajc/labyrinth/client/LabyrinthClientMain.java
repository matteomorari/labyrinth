package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.client.gameView.GamePnl;
import it.unibs.pajc.labyrinth.core.Bot;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

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

    Labyrinth labyrinthModel = new Labyrinth();

    Player player1 = new Player();
    player1.setColor(Color.RED);
    labyrinthModel.addPlayer(player1);

    Player player2 = new Player();
    player2.setColor(Color.YELLOW);
    labyrinthModel.addPlayer(player2);

    LabyrinthLocalController controller = new LabyrinthLocalController(labyrinthModel);
    JPanel gamePanel = new GamePnl(controller);
    labyrinthModel.addChangeListener(e -> gamePanel.repaint());
    frame.add(gamePanel, BorderLayout.CENTER);
    frame.setVisible(true);

    controller.initGame();

    // TODO: to remove
    Bot bot1 = new Bot(labyrinthModel, player1);
    JButton button = new JButton("move bot");
    frame.add(button, BorderLayout.SOUTH);
    button.addActionListener(e -> bot1.calcMove());
  }
}
