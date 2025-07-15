package it.unibs.pajc.labyrinth.client.components;

import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.client.controllers.lobby.LobbyController;
import it.unibs.pajc.labyrinth.core.Avatar;
import it.unibs.pajc.labyrinth.core.Player;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JPanel;

public class LobbyPnl extends JPanel {
  private static final int AVATAR_COUNT = 4;
  private static final int AVATAR_SIZE = 200;
  private static final int AVATAR_INSET = 40;
  private static final int GRID_INSET = 20;
  private static final int ARC_RADIUS = 40;
  private static final Color BACKGROUND_COLOR = new Color(217, 217, 217);
  private static final String AVATAR_SELECTION_DIALOG_TITLE = "SELECT PLAYER COLOR";
  private static final int DEFAULT_PANEL_WIDTH = 800;

  private LobbyController lobbyController;
  private ArrayList<AvatarPnl> avatarPnlList;
  // use to, in case of online game, allow only this player to change the avatar.
  // If null it means it's a local game and so all players can change their avatar.
  private Player localPlayer;

  public LobbyPnl(LobbyController lobbyController, Player localPlayer) {
    if (lobbyController == null) {
      throw new IllegalArgumentException("LobbyController cannot be null");
    }
    this.lobbyController = lobbyController;
    this.localPlayer = localPlayer;
    setPreferredSize(new Dimension(0, 100));
    setLayout(new GridBagLayout());
    setOpaque(false);
    addComponentListener(
        new java.awt.event.ComponentAdapter() {
          @Override
          public void componentResized(java.awt.event.ComponentEvent e) {
            update();
          }
        });
    avatarPnlList = new ArrayList<>();
  }

  public LobbyPnl(LobbyController lobbyController) {
    this(lobbyController, null);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(BACKGROUND_COLOR);
      g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_RADIUS, ARC_RADIUS);
    } finally {
      g2.dispose();
    }
  }

  public void update() {
    removeAll();
    avatarPnlList.clear();

    if (lobbyController.getSelectedLobby() == null) {
      revalidate();
      repaint();
      return;
    }

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(GRID_INSET, GRID_INSET, GRID_INSET, GRID_INSET);

    int panelWidth = getWidth() > 0 ? getWidth() : DEFAULT_PANEL_WIDTH;
    int avatarWidth = AVATAR_SIZE + AVATAR_INSET;
    int columns = panelWidth >= avatarWidth * AVATAR_COUNT ? AVATAR_COUNT : 2;

    ArrayList<Player> players = lobbyController.getPlayers();
    if (players != null) {
      createAndAddAvatarPanels(players, columns, gbc);
      addMouseListenersToAvatarPanels();
    }

    revalidate();
    repaint();
  }

  private void createAndAddAvatarPanels(
      ArrayList<Player> players, int columns, GridBagConstraints gbc) {
    for (int i = 0; i < AVATAR_COUNT; i++) {
      gbc.gridx = i % columns;
      gbc.gridy = i / columns;

      boolean canBeRemoved = i < players.size() && canInteractWithPlayer(players.get(i));
      AvatarPnl avatarPnl = new AvatarPnl(lobbyController, canBeRemoved);
      avatarPnl.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));

      // if there are less players than the max number of players, paint an empty avatar
      if (i < players.size()) {
        avatarPnl.setPlayer(players.get(i));
      }

      add(avatarPnl, gbc);
      avatarPnlList.add(avatarPnl);
    }
  }

  private void addMouseListenersToAvatarPanels() {
    for (AvatarPnl avatarPnl : getAvatarPanels()) {
      Player player = avatarPnl.getPlayer();

      // Combine the two skip conditions into one
      if (player == null || !canInteractWithPlayer(player)) {
        continue;
      }

      avatarPnl.addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              Player currentPlayer = avatarPnl.getPlayer();
              if (currentPlayer == null) {
                return;
              }

              // Only proceed if there are available colors to choose from
              HashSet<Avatar> availableColors = lobbyController.getAvailableColors();
              if (availableColors == null || availableColors.isEmpty()) {
                return;
              }

              showAvatarSelectionDialog(avatarPnl, currentPlayer);
            }
          });
    }
  }

  private boolean canInteractWithPlayer(Player player) {
    // If localPlayer is null, it means it's a local game and all players can interact.
    // In online mode, only allow interaction with the local player or bots.
    return localPlayer == null || player.isBot() || player.equals(localPlayer);
  }

  private void showAvatarSelectionDialog(AvatarPnl avatarPnl, Player player) {
    // Get available colors from the lobby
    HashSet<Avatar> availableColors = lobbyController.getAvailableColors();

    // Build selection items for the dialog
    List<SelectionDialog.SelectionItem> items = new ArrayList<>();
    for (Avatar color : availableColors) {
      BufferedImage img = ImageCntrl.valueOf(color.name() + "_PLAYER_SPRITE").getStandingImage();
      items.add(
          new SelectionDialog.SelectionItem(
              img,
              () -> {
                lobbyController.setPlayerColor(player, color);
                repaint();
              }));
    }

    SelectionDialog.displaySelectionDialog(avatarPnl, AVATAR_SELECTION_DIALOG_TITLE, items);
  }

  public void setLobbyController(LobbyController lobbyController) {
    if (lobbyController == null) {
      throw new IllegalArgumentException("LobbyController cannot be null");
    }
    this.lobbyController = lobbyController;
  }

  public LobbyController getLobbyController() {
    return lobbyController;
  }

  public ArrayList<AvatarPnl> getAvatarPanels() {
    return avatarPnlList;
  }
}
