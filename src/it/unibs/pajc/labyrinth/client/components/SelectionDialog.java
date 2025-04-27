package it.unibs.pajc.labyrinth.client.components;

import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;

/** A reusable dialog for displaying image-based selection options to the user. */
public class SelectionDialog {
  private static final int POPUP_IMAGE_SIZE = 100;
  private static final int BUTTON_PADDING = 10;
  private static final int LINE_THICKNESS = 5;

  /** Class representing a selectable item in the dialog */
  public static class SelectionItem {
    private final BufferedImage image;
    private final Runnable onSelectAction;

    /**
     * Creates a new selection item
     *
     * @param image The image to display for this selection
     * @param onSelectAction The action to run when this item is selected
     */
    public SelectionItem(BufferedImage image, Runnable onSelectAction) {
      this.image = image;
      this.onSelectAction = onSelectAction;
    }

    public BufferedImage getImage() {
      return image;
    }

    public Runnable getOnSelectAction() {
      return onSelectAction;
    }
  }

  /**
   * Shows a selection dialog with the given title and selection items
   *
   * @param parent The parent component for positioning the dialog
   * @param title The title of the dialog
   * @param items The list of selectable items to display
   */
  // TODO: create two rows if there are too many items
  public static void show(Component parent, String title, List<SelectionItem> items) {
    // Create the content panel
    JPanel panel = new JPanel(new GridLayout(1, items.size()));
    // Calculate the width based on number of items
    int panelWidth = POPUP_IMAGE_SIZE * items.size() + (BUTTON_PADDING * 2 * items.size());
    panel.setPreferredSize(new Dimension(panelWidth, 200));
    panel.setBackground(Color.LIGHT_GRAY);

    // Create the dialog
    JDialog dialog = new JDialog((Frame) null, null, true);
    dialog.getContentPane().add(createCustomPanel(panel, title));
    dialog.pack();
    dialog.setLocationRelativeTo(parent);

    // Add the buttons to the panel
    for (SelectionItem item : items) {
      BufferedImage scaledImage =
          ImageCntrl.scaleBufferedImage(item.getImage(), POPUP_IMAGE_SIZE, POPUP_IMAGE_SIZE);
      JButton button = new JButton(new ImageIcon(scaledImage));
      button.setFocusPainted(false);
      button.setBackground(Color.LIGHT_GRAY);
      button.setBorder(new LineBorder(Color.WHITE, BUTTON_PADDING));

      button.addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
              button.setBorder(new LineBorder(Color.GREEN, LINE_THICKNESS));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
              button.setBorder(new LineBorder(Color.WHITE, BUTTON_PADDING));
            }
          });

      button.addActionListener(
          e -> {
            item.getOnSelectAction().run();
            dialog.dispose();
          });

      panel.add(button);
    }

    dialog.setVisible(true);
  }

  /**
   * Creates a custom panel with a title and content
   *
   * @param panel The content panel
   * @param title The title to display
   * @return The complete panel with title and content
   */
  private static JPanel createCustomPanel(JPanel panel, String title) {
    JLabel messageLabel = new JLabel(title, JLabel.CENTER);
    messageLabel.setFont(new Font("Times New Roman", Font.BOLD, 15));
    JPanel customPanel = new JPanel(new BorderLayout());
    customPanel.add(messageLabel, BorderLayout.NORTH);
    customPanel.add(panel, BorderLayout.CENTER);
    return customPanel;
  }
}
