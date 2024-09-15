import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class BombTimer extends JFrame {
    private Point initialClick;
    private JLabel timerLabel;
    private int timeRemaining;
    private JLabel bombLabel;
    private ImageIcon bombIcon;

    public BombTimer(int minutes) {
        // Convert minutes to seconds
        timeRemaining = minutes * 60;

        // Load bomb image
        bombIcon = new ImageIcon("./src/assets/bomb.png");

        // Resize the bomb image (width=400, height=400 for example)
        Image bombImage = bombIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        bombIcon = new ImageIcon(bombImage);  // Update bombIcon with the scaled image

        // Set JFrame properties
        setUndecorated(true);  // Remove title bar
        setSize(bombIcon.getIconWidth(), bombIcon.getIconHeight());
        setBackground(new Color(0, 0, 0, 0));  // Transparent background
        setLocationRelativeTo(null);  // Center the window

        // Make the window always stay on top
        setAlwaysOnTop(true);

        // JLabel to hold the bomb image
        bombLabel = new JLabel(bombIcon);
        setContentPane(bombLabel);  // Set the bomb image as content pane

        // Add the timer display label
        timerLabel = new JLabel(formatTime(timeRemaining), SwingConstants.CENTER);
        timerLabel.setForeground(Color.RED);  // Red text color
        timerLabel.setBounds(120, 180, 150, 30);  // Adjust size and position based on new image size

        // Load font
        try {
            Font digitalFont = Font.createFont(Font.TRUETYPE_FONT, new File("./src/assets/digital-7(mono).ttf")).deriveFont(32f); // Load and set size
            timerLabel.setFont(digitalFont);
        } catch (FontFormatException | IOException e) {
            timerLabel.setFont(new Font("Arial", Font.BOLD, 32));  // Fallback to default if loading fails
        }

        bombLabel.add(timerLabel);  // Add timer label to the bomb image

        // Add drag functionality
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Get the location of the window
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // Calculate the new location
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        // Timer logic
        Timer countdownTimer = new Timer(1000, e -> {
            if (timeRemaining > 0) {
                timeRemaining--;
                timerLabel.setText(formatTime(timeRemaining));  // Update time in minutes:seconds
                if (timeRemaining == 5) {
                    playSound("./src/assets/countdown.wav");
                }
            } else {
                ((Timer) e.getSource()).stop();
                showExplosion();  // Show explosion GIF when timer reaches 0
                playSound("./src/assets/explosion2.wav");
                // Add a delay before closing the window
                new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            dispose();  // Close the window
                        }
                    }, 870);
            }
        });
        countdownTimer.start();  // Start the countdown timer
    }

    // Show explosion GIF
    private void showExplosion() {
        // Load explosion GIF
        ImageIcon explosionIcon = new ImageIcon("./src/assets/explosion.gif");

        // Resize the explosion GIF (e.g., 500x500 pixels)
        Image explosionImage = explosionIcon.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT);
        explosionIcon = new ImageIcon(explosionImage);  // Update explosionIcon with the scaled image

        bombLabel.setIcon(explosionIcon);  // Replace bomb image with explosion GIF
        bombLabel.revalidate();  // Revalidate to ensure the label is updated
        bombLabel.repaint();     // Repaint to ensure the new icon is drawn
        timerLabel.setVisible(false);  // Hide the timer label
    }

    // Method to play the explosion sound
    private void playSound(String soundFilePath) {
        try {
            // Load the sound file
            File soundFile = new File(soundFilePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);

            // Get a clip resource
            Clip clip = AudioSystem.getClip();

            // Open the audio clip and load the sound data
            clip.open(audioStream);

            // Play the sound
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();  // Handle exceptions (file not found, unsupported format, etc.)
        }
    }

    // Method to format time as mm:ss
    private String formatTime(int seconds) {
        int minutes = (seconds / 60);
        seconds %= 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
