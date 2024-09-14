import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Ask for the number of minutes using a dialog box
        String input = JOptionPane.showInputDialog(null, "Enter number of minutes:", "Timer Setup", JOptionPane.QUESTION_MESSAGE);

        try {
            int minutes = Integer.parseInt(input);
            if (minutes < 0) {
                throw new NumberFormatException();
            }

            // Start the BombTimer with the inputted number of minutes
            SwingUtilities.invokeLater(() -> {
                new BombTimer(minutes).setVisible(true);
            });
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Please enter a valid positive integer for minutes.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            System.exit(1);  // Exit if input is invalid
        }
    }
}