package client.gameView;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class Main {
  public static void main(String[] args) {
    JFrame frame = new JFrame("Card Click Example");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 400);

    ImageCntrl imageCntrl = ImageCntrl.CARD_I; // Assuming ImageCntrl is properly defined
    CardImage card = new CardImage(imageCntrl);
    // card.paintComponent(frame.getGraphics(), 0, 0, 100, 100);

    frame.setLayout(new BorderLayout());
    frame.add(card, BorderLayout.CENTER);

    frame.setVisible(true);
  }
}