package it.unibs.pajc.labyrinth.client.gameView;

import javax.swing.*;

import it.unibs.pajc.labyrinth.core.LabyrinthController;

import java.awt.*;

public class GoalPnl extends JPanel {
    
    public GoalPnl(LabyrinthController controller) {
        // Set a BoxLayout with Y_AXIS to allow better control over vertical layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.LIGHT_GRAY);
        
        // Add some space before the text label
        add(Box.createVerticalStrut(15));

        // Create a text label
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.LIGHT_GRAY);  
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);  

        // Create a label with font and two lines of text
        JLabel textLabel = new JLabel("<html><center>PROSSIMO<br>OBBIETTIVO</center></html>", JLabel.CENTER);
        textLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        textLabel.setForeground(Color.DARK_GRAY);
        textPanel.add(textLabel);  
        add(textPanel);  

        // Create the card panel where the image will be placed
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Load the default card image (card with white oval in the center)
                ImageIcon goalCardImage = new ImageIcon("resource/images/goal.png");
                
                // Check if the default card image is loaded correctly
                if (goalCardImage.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    // Draw the default card image in the panel (fill the entire panel with padding)
                    int padding = 30;  // Padding around the default card
                    int width = getWidth() - 2 * padding;
                    int height = getHeight() - padding/2;

                    // Draw the default card image with padding
                    g.drawImage(goalCardImage.getImage(), padding, padding/5, width, height, this);
                } else {
                    System.err.println("Error loading default card image.");
                }

                // If a goal image is available, draw it on top of the default card
                if (!controller.getCurrentPlayer().getGoals().isEmpty()) {
                    // Assuming the goal type is already defined in your controller
                    ImageCntrl goalImageCntrl = ImageCntrl.valueOf("GOAL_" + controller.getCurrentPlayer().getCurrentGoal().getType().name());

                    // Get dimensions of the goal image
                    Image goal = goalImageCntrl.getImage();
                    int goalWidth = goal.getWidth(null);
                    int goalHeight = goal.getHeight(null);

                    // Scale the goal image to fit within the oval on the card (make it smaller)
                    int maxGoalWidth = getWidth() / 2; // Goal will be half the width of the card
                    int maxGoalHeight = getHeight() / 3; // Goal will be half the height of the card

                    // Scale the goal image while keeping the aspect ratio
                    double aspectRatio = (double) goalWidth / goalHeight;
                    if (goalWidth > maxGoalWidth) {
                        goalWidth = maxGoalWidth;
                        goalHeight = (int) (goalWidth / aspectRatio);
                    }
                    if (goalHeight > maxGoalHeight) {
                        goalHeight = maxGoalHeight;
                        goalWidth = (int) (goalHeight * aspectRatio);
                    }

                    // Calculate the center position for the goal image
                    int x = (getWidth() - goalWidth) / 2;
                    int y = (getHeight() - goalHeight) / 2;

                    // Draw the goal image centered on the default card
                    g.drawImage(goal, x, y, goalWidth, goalHeight, this);
                }
            }
        };

        cardPanel.setBackground(Color.LIGHT_GRAY);
        cardPanel.setPreferredSize(new Dimension(100,350));

        add(cardPanel); 

        // Set the preferred size of the GoalPnl
        setPreferredSize(new Dimension(300, 200)); 
    }
}

