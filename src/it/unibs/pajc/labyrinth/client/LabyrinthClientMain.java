package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.client.views.StartPnl;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;

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
    frame.setLayout(new BorderLayout(10, 10));

    frame.add(new StartPnl(), BorderLayout.CENTER);
  }
}
