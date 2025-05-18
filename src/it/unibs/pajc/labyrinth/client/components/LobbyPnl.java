package it.unibs.pajc.labyrinth.client.components;

import it.unibs.pajc.labyrinth.client.controllers.ImageCntrl;
import it.unibs.pajc.labyrinth.client.controllers.LabyrinthController;
import it.unibs.pajc.labyrinth.core.Player;
import it.unibs.pajc.labyrinth.core.PlayerColor;
import it.unibs.pajc.labyrinth.core.lobby.Lobby;
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

  private Lobby lobby;
  private ArrayList<AvatarPnl> avatarPnlList;
  // use to, in case of online game, allow only this player to change the avatar.
  // If null it means it's a local game and so all players can change their avatar.
  private Player localPlayer;

  public LobbyPnl(Player localPlayer) {
    this.localPlayer = localPlayer;
    setPreferredSize(new Dimension(0, 100));
    setLayout(new GridBagLayout());
    addComponentListener(
        new java.awt.event.ComponentAdapter() {
          @Override
          public void componentResized(java.awt.event.ComponentEvent e) {
            update(lobby);
          }
        });
    avatarPnlList = new ArrayList<>();
  }

  public LobbyPnl() {
    this(null);
  }

  @Override
  protected void paintComponent(Graphics g) {
    // Draw rounded background
    Graphics2D g2 = (Graphics2D) g.create();
    super.paintComponent(g);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(Color.ORANGE);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_RADIUS, ARC_RADIUS);
    g2.dispose();
  }

  public void update(Lobby gameLobby) {
    if (gameLobby == null) {
      return;
    }
    this.lobby = gameLobby;
    removeAll();
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(GRID_INSET, GRID_INSET, GRID_INSET, GRID_INSET);

    int panelWidth = getWidth() > 0 ? getWidth() : 800;
    int avatarWidth = AVATAR_SIZE + AVATAR_INSET;
    int columns = panelWidth >= avatarWidth * AVATAR_COUNT ? AVATAR_COUNT : 2;

    ArrayList<Player> players = lobby.getPlayers();
    avatarPnlList.clear();
    if (players != null && !players.isEmpty()) {
      for (int i = 0; i < AVATAR_COUNT; i++) {
        gbc.gridx = i % columns;
        gbc.gridy = i / columns;
        AvatarPnl avatarPnl = new AvatarPnl(true);
        avatarPnl.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        // if there are less players than the max number of players, paint an empty avatar
        if (i < players.size()) {
          avatarPnl.setPlayer(players.get(i));
        }
        add(avatarPnl, gbc);
        avatarPnlList.add(avatarPnl);
      }
    }

    // Add mouse listener to each AvatarPnl in the lobby panel
    for (AvatarPnl avatarPnl : getAvatarPanels()) {

      if ((this.localPlayer != null && avatarPnl.getPlayer() != null)) {
        if (!avatarPnl.getPlayer().equals(this.localPlayer)) {
          // the local player can change only his avatar and bots
          continue;
        }
      }

      avatarPnl.addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              Player player = avatarPnl.getPlayer();
              if (player == null) return;

              // Get available colors from the lobby
              HashSet<PlayerColor> availableColors = lobby.getAvailableColors();

              // Build selection items for the dialog
              List<SelectionDialog.SelectionItem> items = new ArrayList<>();
              for (PlayerColor color : availableColors) {
                BufferedImage img =
                    ImageCntrl.valueOf(color.name() + "_PLAYER_SPRITE").getStandingImage();
                // Create a Runnable variable for the color selection action
                Runnable onAvatarChange =
                    () -> {
                      lobby.setPlayerColor(player, color);
                      repaint();
                    };

                items.add(
                    new SelectionDialog.SelectionItem(
                        img,
                        () -> {
                          onAvatarChange.run();
                        }));
              }

              SelectionDialog.show(avatarPnl, "Select Player Color", items);
            }
          });
    }
    revalidate();
    repaint();
  }

  public void setLobby(Lobby gameLobby) {
    this.lobby = gameLobby;
    update(gameLobby);
  }

  public Lobby getLobby() {
    return lobby;
  }

  public ArrayList<AvatarPnl> getAvatarPanels() {
    return avatarPnlList;
  }
}
