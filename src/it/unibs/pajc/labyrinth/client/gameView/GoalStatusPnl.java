package it.unibs.pajc.labyrinth.client.gameView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayDeque;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibs.pajc.labyrinth.core.LabyrinthController;
import it.unibs.pajc.labyrinth.core.Player;

public class GoalStatusPnl extends JPanel {
    private LabyrinthController controller;
    private ArrayDeque<Player> players;
    
    public GoalStatusPnl(LabyrinthController controller) {
        this.controller = controller;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.LIGHT_GRAY);
        refreshPanel();  
    }


    public void refreshPanel(){
        removeAll();
        // Set a BoxLayout with Y_AXIS to allow better control over vertical layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.LIGHT_GRAY);
    
        // Add minimal space before the text label 
        add(Box.createVerticalStrut(5)); 
    
        // text label
        JPanel textPanel = new JPanel();
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.setBackground(Color.LIGHT_GRAY);  
    
        // Create the label with the title
        JLabel textLabel = new JLabel("<html><center>CARTE<br>RIMANENTI</center></html>", JLabel.CENTER);
        textLabel.setFont(new Font("Times New Roman", Font.BOLD, 20)); 
        textLabel.setForeground(Color.BLACK);
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    
        textPanel.add(textLabel);  
        add(textPanel);  
    
        // Loop through each player and show their remaining goals
        for(Player player : controller.getPlayers()){
            // Create a text label for each player
            JPanel playerPanel = new JPanel();
            playerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);  
            playerPanel.setBackground(Color.LIGHT_GRAY);
    
            // Set a maximum size to prevent the player panel from growing too large
            playerPanel.setMaximumSize(new Dimension(250, 20)); 
            playerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    
            // Create a label showing the player's name and goals left
            JLabel playerLabel = new JLabel("<html><center>- " + player.getName() + " " + player.getGoals().size() + " goals left</center></html>", JLabel.CENTER);
            playerLabel.setFont(new Font("Times New Roman", Font.PLAIN, 16)); 
            playerLabel.setForeground(Color.BLACK);
            playerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            playerLabel.setPreferredSize(new Dimension(250, 20));
    
            playerPanel.add(playerLabel);  
            add(playerPanel); 
        }
    
        // Set the preferred size of the GoalStatusPnl to make it smaller
        setPreferredSize(new Dimension(300, 100)); 
    }
}
