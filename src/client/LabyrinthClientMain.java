package client;

import client.gameView.GamePnl;
import java.awt.BorderLayout;
import java.awt.EventQueue;
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

    JPanel gamePanel = new GamePnl();
    frame.add(gamePanel);

    frame.setVisible(true);
  }
}
