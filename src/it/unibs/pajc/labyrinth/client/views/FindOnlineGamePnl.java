package it.unibs.pajc.labyrinth.client.views;

import it.unibs.pajc.labyrinth.client.components.LobbyPnl;
import it.unibs.pajc.labyrinth.client.components.RoundedIconButton;
import it.unibs.pajc.labyrinth.client.controllers.LabyrinthClientController;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class FindOnlineGamePnl extends JPanel {
  private LabyrinthClientController clientController;
  private LobbyPnl currentLobbyPnl;
  private JButton createNewLobbyButton;
  private JButton readyButton;
  private JButton addBotButton;
  private ArrayList<Lobby> availableLobbies;
  private JList<String> lobbiesJList;
  private Lobby currentLobby;

  public FindOnlineGamePnl(LabyrinthClientController clientController) {
    this.clientController = clientController;
    this.availableLobbies = new ArrayList<>();
    this.currentLobby = null;
    setPreferredSize(new Dimension(800, 600));
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Title label
    JLabel titleLabel = new JLabel("LABIRINTO", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
    titleLabel.setForeground(Color.BLUE);
    titleLabel.setPreferredSize(new Dimension(0, 200));
    titleLabel.setBackground(Color.YELLOW); // TODO: to remove
    titleLabel.setOpaque(true);
    add(titleLabel, BorderLayout.NORTH);

    // Game list panel
    lobbiesJList = new JList<>();
    lobbiesJList.setFont(new Font("Arial", Font.PLAIN, 16));
    lobbiesJList.setCellRenderer(
        (list, value, index, isSelected, cellHasFocus) -> {
          Color bgColor = Color.LIGHT_GRAY;
          JPanel panel =
              new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(
                      RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setColor(bgColor);
                  int arc = 20;
                  int inset = 4;
                  g2.fillRoundRect(
                      inset, inset, getWidth() - 2 * inset, getHeight() - 2 * inset, arc, arc);
                  g2.dispose();
                }
              };
          panel.setLayout(new BorderLayout());
          panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

          // set the Lobby name and player count
          String displayValue = value;
          int maxLen = 20;
          boolean truncated = false;
          if (displayValue.length() > maxLen) {
            displayValue = displayValue.substring(0, maxLen - 3) + "...";
            truncated = true;
          }
          JLabel label = new JLabel(displayValue);
          label.setFont(list.getFont());
          if (truncated) {
            panel.setToolTipText(value);
          }
          panel.add(label, BorderLayout.WEST);

          RoundedIconButton icon = new RoundedIconButton("resource\\images\\rotate.svg");
          icon.setBorderRadius(-1);
          icon.setButtonSize(30, 30);
          icon.setSvgIconSize(30, 30);
          icon.setBgColor(bgColor);
          panel.add(icon, BorderLayout.EAST);
          return panel;
        });
    JScrollPane lobbyListPnl = new JScrollPane(lobbiesJList);
    lobbyListPnl.setBorder(null);
    lobbyListPnl.setPreferredSize(new Dimension(250, 0));
    lobbyListPnl.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    add(lobbyListPnl, BorderLayout.WEST);

    // lobby panel
    currentLobbyPnl = new LobbyPnl(clientController.getLocalPlayer());
    add(currentLobbyPnl, BorderLayout.CENTER);

    // Action buttons panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setPreferredSize(new Dimension(0, 60));
    add(buttonPanel, BorderLayout.SOUTH);

    readyButton = new JButton("READY TO PLAY");
    readyButton.setPreferredSize(new Dimension(200, 50));
    addBotButton = new JButton("ADD BOT");
    addBotButton.setPreferredSize(new Dimension(200, 50));
    createNewLobbyButton = new JButton("CREATE NEW LOBBY");
    createNewLobbyButton.setPreferredSize(new Dimension(200, 50));

    buttonPanel.add(readyButton);
    buttonPanel.add(addBotButton);
    buttonPanel.add(createNewLobbyButton);

    // Add action listeners
    createNewLobbyButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String lobbyName = JOptionPane.showInputDialog("Enter lobby name:");
            if (lobbyName != null) {
              if (!lobbyName.trim().isEmpty()) {
                // check if the lobby name is already taken
                for (Lobby lobby : availableLobbies) {
                  if (lobby.getLobbyName().equalsIgnoreCase(lobbyName)) {
                    JOptionPane.showMessageDialog(
                        FindOnlineGamePnl.this,
                        "Lobby name already taken. Please choose another name.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                  }
                }
                clientController.createLobby(lobbyName);
              } else {
                JOptionPane.showMessageDialog(
                    FindOnlineGamePnl.this,
                    "Lobby name cannot be empty.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
              }
            }
          }
        });
    readyButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (currentLobby != null) {
              clientController.togglePlayerReadyToPlay();
            }
          }
        });

    addBotButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (currentLobby != null) {
              clientController.addBotToLobby();
            }
          }
        });

    lobbiesJList.addListSelectionListener(
        e -> {
          if (!e.getValueIsAdjusting()) { // Ensure the event is not fired multiple times
            int selectedIndex = lobbiesJList.getSelectedIndex();
            if (selectedIndex != -1) {
              currentLobby = availableLobbies.get(selectedIndex);
              clientController.joinLobby(currentLobby.getLobbyId());
            }
          }
        });
  }

  public void updateData() {
    currentLobby = clientController.getOnlineGameManager().getSelectedLobby();

    // if on an update the game is in progress, this means that the game is started and the
    // so we have to switch to the game panel
    if (currentLobby != null && currentLobby.isGameInProgress()) {
      Labyrinth labyrinthModel = clientController.getLabyrinthModel();

      GamePnl gamePanel = new GamePnl(clientController);
      labyrinthModel.addChangeListener(e -> gamePanel.update());

      // Replace the current panel's content with the game panel
      JPanel parent = (JPanel) getParent();
      parent.removeAll();
      parent.setLayout(new BorderLayout());
      parent.add(gamePanel, BorderLayout.CENTER);
      parent.revalidate();
      parent.repaint();
    }

    this.availableLobbies = clientController.getOnlineGameManager().getAvailableLobbies();
    updateLobbiesJList();
    currentLobbyPnl.update(currentLobby);
    revalidate();
    repaint();
  }

  private void updateLobbiesJList() {
    if (currentLobby == null) {}

    DefaultListModel<String> listModel = new DefaultListModel<>();
    for (Lobby lobby : availableLobbies) {
      if (lobby.isGameInProgress()) {
        continue;
      }

      String lobbyName = lobby.getLobbyName();
      if (currentLobby != null && lobby.getLobbyId().equals(currentLobby.getLobbyId())) {
        listModel.addElement(lobbyName.toUpperCase() + "  (" + lobby.getPlayerCount() + ")");
      } else {
        listModel.addElement(lobbyName + "  (" + lobby.getPlayerCount() + ")");
      }
    }
    lobbiesJList.setModel(listModel);
  }
}
