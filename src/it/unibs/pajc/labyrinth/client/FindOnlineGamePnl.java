package it.unibs.pajc.labyrinth.client;

import it.unibs.pajc.labyrinth.client.gameView.GamePnl;
import it.unibs.pajc.labyrinth.core.Bot;
import it.unibs.pajc.labyrinth.core.GameLobby;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class FindOnlineGamePnl extends JPanel {
  private LabyrinthClientController clientController;
  private ArrayList<GameLobby> gameLobbies;
  private GameLobby selectedLobby;
  private JButton newGameButton;
  private JButton readyButton;
  private JList<String> gameList;
  private JPanel avatarPanel;

  public FindOnlineGamePnl(LabyrinthClientController clientController) {
    this.clientController = clientController;
    this.gameLobbies = new ArrayList<>();
    this.selectedLobby = null;
    setPreferredSize(new Dimension(800, 600));
    setLayout(new BorderLayout());

    clientController.fetchLobbyOptions();

    // Title label
    JLabel titleLabel = new JLabel("LABIRINTO", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
    titleLabel.setForeground(Color.BLUE);
    add(titleLabel, BorderLayout.NORTH);

    // Main content panel
    JPanel contentPanel = new JPanel(new BorderLayout());
    add(contentPanel, BorderLayout.CENTER);

    // Game list panel
    JPanel gameListPanel = new JPanel(new BorderLayout());
    gameListPanel.setBackground(Color.LIGHT_GRAY);
    gameListPanel.setPreferredSize(new Dimension(200, 0));
    contentPanel.add(gameListPanel, BorderLayout.WEST);

    JLabel gameListLabel = new JLabel("Available Games", SwingConstants.CENTER);
    gameListLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    gameListPanel.add(gameListLabel, BorderLayout.NORTH);

    gameList = new JList<>();
    gameList.setFont(new Font("Arial", Font.PLAIN, 16));
    gameListPanel.add(new JScrollPane(gameList), BorderLayout.CENTER);

    // Avatar panel
    avatarPanel = new JPanel();
    avatarPanel.setBackground(Color.ORANGE);
    avatarPanel.setPreferredSize(new Dimension(0, 100));
    contentPanel.add(avatarPanel, BorderLayout.SOUTH);

    avatarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
    updateAvatarPanel(); // Use the method to initialize avatar panel

    // Action buttons panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setPreferredSize(new Dimension(0, 60));
    add(buttonPanel, BorderLayout.SOUTH);

    newGameButton = new JButton("+ NEW GAME");
    readyButton = new JButton("READY TO PLAY");

    buttonPanel.add(newGameButton);
    buttonPanel.add(readyButton);

    // Add action listeners
    newGameButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            System.out.println("New Game button clicked!");
            // Add logic to handle creating a new game
          }
        });

    readyButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (selectedLobby != null) {
              clientController.togglePlayerReadyToPlay();
            }
          }
        });

    gameList.addListSelectionListener(
        e -> {
          if (!e.getValueIsAdjusting()) { // Ensure the event is not fired multiple times
            int selectedIndex = gameList.getSelectedIndex();
            if (selectedIndex != -1) {
              GameLobby selectedLobby = gameLobbies.get(selectedIndex);
              System.out.println("Selected Game: " + selectedLobby);
              clientController.joinLobby(selectedLobby.getLOBBY_ID());
              // Update the selectedLobby and refresh the avatar panel
              this.selectedLobby = selectedLobby;
              updateAvatarPanel();
            }
          }
        });
  }

  public void updateData() {
    this.selectedLobby = clientController.getOnlineGameManager().getSelectedLobby();
    if (this.selectedLobby == null || !this.selectedLobby.isGameInProgress()) {
      this.gameLobbies = clientController.getOnlineGameManager().getAvailableLobbies();
      updateGameList();
      updateAvatarPanel();
      revalidate();
      repaint();
    } else {
      Labyrinth labyrinthModel = this.selectedLobby.getModel();
      // TODO: to remove
      JPanel tempPnl = new JPanel();
      tempPnl.setLayout(new BorderLayout());

      GamePnl gamePanel = new GamePnl(clientController);
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
  }

  private void updateGameList() {
    DefaultListModel<String> listModel = new DefaultListModel<>();
    for (int i = 0; i < gameLobbies.size(); i++) {
      GameLobby lobby = gameLobbies.get(i);
      if (selectedLobby != null && lobby.getLOBBY_ID().equals(selectedLobby.getLOBBY_ID())) {
        listModel.addElement("GAME " + (i + 1) + "  (" + lobby.getPlayerCount() + ")");

      } else {
        listModel.addElement("game " + (i + 1) + "  (" + lobby.getPlayerCount() + ")");
      }
    }
    gameList.setModel(listModel);
  }

  private void updateAvatarPanel() {
    avatarPanel.removeAll();

    if (selectedLobby != null) {
      for (int i = 0; i < selectedLobby.getPlayerCount(); i++) {
        JPanel avatar = new JPanel();
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setBackground(Color.GRAY);

        // Set border color based on player's readiness
        if (selectedLobby.getPlayers().get(i).isReadyToPlay()) {
          avatar.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
        } else {
          avatar.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }

        JLabel avatarLabel =
            new JLabel(selectedLobby.getPlayers().get(i).getId(), SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        avatar.add(avatarLabel);
        avatarPanel.add(avatar);
      }
    }

    avatarPanel.revalidate();
    avatarPanel.repaint();
  }
}
