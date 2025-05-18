package it.unibs.pajc.labyrinth.client.views;

import it.unibs.pajc.labyrinth.client.components.LobbyPnl;
import it.unibs.pajc.labyrinth.client.components.LogoPanel;
import it.unibs.pajc.labyrinth.client.components.SvgIconButton;
import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.client.controllers.labyrinth.LabyrinthClientController;
import it.unibs.pajc.labyrinth.client.controllers.lobby.LobbyClientController;
import it.unibs.pajc.labyrinth.core.Labyrinth;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

public class FindOnlineGamePnl extends JPanel {
  private static final int PANEL_WIDTH = 800;
  private static final int PANEL_HEIGHT = 600;
  private static final int BORDER_GAP = 10;
  private static final int LOBBY_LIST_WIDTH = 250;
  private static final int BUTTON_PANEL_HEIGHT = 60;
  private static final int BUTTON_WIDTH = 200;
  private static final int BUTTON_HEIGHT = 50;
  private static final int LOBBY_LIST_FONT_SIZE = 16;
  private static final int LOBBY_LIST_CELL_ARC = 20;
  private static final int LOBBY_LIST_CELL_INSET = 4;
  private static final int LOBBY_LIST_CELL_BORDER_TOP = 10;
  private static final int LOBBY_LIST_CELL_BORDER_LEFT = 15;
  private static final int LOBBY_LIST_CELL_BORDER_BOTTOM = 10;
  private static final int LOBBY_LIST_CELL_BORDER_RIGHT = 15;
  private static final int LOBBY_LIST_ICON_SIZE = 30;
  private static final int LOBBY_LIST_PANEL_BORDER = 5;
  private static final int LOBBY_NAME_MAX_LEN = 20;
  private LobbyClientController lobbyController;
  private LobbyPnl currentLobbyPnl;
  private JButton createNewLobbyButton;
  private JButton readyButton;
  private JButton addBotButton;
  private ArrayList<Lobby> availableLobbies;
  private JList<String> lobbiesJList;
  private Lobby currentLobby;

  public FindOnlineGamePnl(LobbyClientController lobbyController) {
    this.lobbyController = lobbyController;
    this.availableLobbies = new ArrayList<>();
    this.currentLobby = null;
    setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
    setLayout(new BorderLayout(BORDER_GAP, BORDER_GAP));
    setBorder(BorderFactory.createEmptyBorder(BORDER_GAP, BORDER_GAP, BORDER_GAP, BORDER_GAP));

    // Logo panel
    BufferedImage logo = ImageCntrl.LOGO.getImage();
    LogoPanel logoPanel = new LogoPanel(logo);
    add(logoPanel, BorderLayout.NORTH);

    // Game list panel
    lobbiesJList = new JList<>();
    lobbiesJList.setFont(new Font("Arial", Font.PLAIN, LOBBY_LIST_FONT_SIZE));
    lobbiesJList.setOpaque(false);
    lobbiesJList.setCellRenderer(
        (list, value, index, isSelected, cellHasFocus) -> {
          Color bgColor = Color.WHITE;
          JPanel panel =
              new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                  super.paintComponent(g);
                  Graphics2D g2 = (Graphics2D) g.create();
                  g2.setRenderingHint(
                      RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                  g2.setColor(bgColor);
                  int arc = LOBBY_LIST_CELL_ARC;
                  int inset = LOBBY_LIST_CELL_INSET;
                  g2.fillRoundRect(
                      inset, inset, getWidth() - 2 * inset, getHeight() - 2 * inset, arc, arc);
                  g2.dispose();
                }
              };
          panel.setOpaque(false);
          panel.setLayout(new BorderLayout());
          panel.setBorder(
              BorderFactory.createEmptyBorder(
                  LOBBY_LIST_CELL_BORDER_TOP, LOBBY_LIST_CELL_BORDER_LEFT,
                  LOBBY_LIST_CELL_BORDER_BOTTOM, LOBBY_LIST_CELL_BORDER_RIGHT));

          // set the Lobby name and player count
          String displayValue = value;
          int maxLen = LOBBY_NAME_MAX_LEN;
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

          SvgIconButton icon = new SvgIconButton("resource\\icons\\rotate.svg");
          icon.setBorderRadius(-1);
          icon.setButtonSize(LOBBY_LIST_ICON_SIZE, LOBBY_LIST_ICON_SIZE);
          icon.setSvgIconSize(LOBBY_LIST_ICON_SIZE, LOBBY_LIST_ICON_SIZE);
          icon.setBgColor(bgColor);
          panel.add(icon, BorderLayout.EAST);
          return panel;
        });
    JScrollPane lobbyListScrollPanel = new JScrollPane(lobbiesJList);
    lobbyListScrollPanel.setBorder(null);
    lobbyListScrollPanel.getViewport().setBackground(Color.LIGHT_GRAY);
    lobbyListScrollPanel.setPreferredSize(new Dimension(LOBBY_LIST_WIDTH, 0));
    lobbyListScrollPanel.setHorizontalScrollBarPolicy(
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    JPanel lobbyListPanel =
        new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY);
            int arc = LOBBY_LIST_CELL_ARC + LOBBY_LIST_PANEL_BORDER;
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
          }
        };
    lobbyListPanel.setLayout(new BorderLayout());
    lobbyListPanel.setBorder(
        BorderFactory.createEmptyBorder(
            LOBBY_LIST_PANEL_BORDER, LOBBY_LIST_PANEL_BORDER,
            LOBBY_LIST_PANEL_BORDER, LOBBY_LIST_PANEL_BORDER));
    lobbyListPanel.add(lobbyListScrollPanel, BorderLayout.CENTER);
    lobbyListPanel.setOpaque(false);
    add(lobbyListPanel, BorderLayout.WEST);

    // lobby panel
    currentLobbyPnl = new LobbyPnl(lobbyController, lobbyController.getLocalPlayer());
    add(currentLobbyPnl, BorderLayout.CENTER);

    // Action buttons panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setPreferredSize(new Dimension(0, BUTTON_PANEL_HEIGHT));
    buttonPanel.setOpaque(false);
    add(buttonPanel, BorderLayout.SOUTH);

    readyButton = new JButton("READY TO PLAY");
    readyButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    addBotButton = new JButton("ADD BOT");
    addBotButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
    createNewLobbyButton = new JButton("CREATE NEW LOBBY");
    createNewLobbyButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

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
                lobbyController.createLobby(lobbyName);
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
              lobbyController.togglePlayerReadyToPlay();
            }
          }
        });

    addBotButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (currentLobby != null) {
              lobbyController.addBotToLobby();
            }
          }
        });

    lobbiesJList.addListSelectionListener(
        e -> {
          if (!e.getValueIsAdjusting()) { // Ensure the event is not fired multiple times
            int selectedIndex = lobbiesJList.getSelectedIndex();
            if (selectedIndex != -1) {
              currentLobby = availableLobbies.get(selectedIndex);
              lobbyController.joinLobby(currentLobby.getLobbyId());
            }
          }
        });
  }

  public void updateData() {
    currentLobby = lobbyController.getSelectedLobby();

    // if on an update the game is in progress, this means that the game is started and the
    // so we have to switch to the game panel
    if (currentLobby != null && currentLobby.isGameInProgress()) {
      Labyrinth labyrinthModel = lobbyController.getLabyrinth();
      Player localPlayer = lobbyController.getLocalPlayer();

      LabyrinthClientController labyrinthClientController =
          new LabyrinthClientController(
              lobbyController.getConnectionProtocol(), labyrinthModel, localPlayer);
      GamePnl gamePanel = new GamePnl(labyrinthClientController);
      labyrinthModel.addChangeListener(e -> gamePanel.update());

      // Replace the current panel's content with the game panel
      JPanel parent = (JPanel) getParent();
      parent.remove(this);
      parent.setLayout(new BorderLayout());
      parent.add(gamePanel, BorderLayout.CENTER);
      parent.revalidate();
      parent.repaint();
    }

    this.availableLobbies = lobbyController.getAvailableLobbies();
    updateLobbiesJList();
    currentLobbyPnl.update();
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
