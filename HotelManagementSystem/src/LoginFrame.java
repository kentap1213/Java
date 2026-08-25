// ============================================================
//  LoginFrame.java
//  PHASE 9, STEP 33 & 34: Login window + role-based access.
//  The very first thing the user sees when the app opens.
// ============================================================

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        // ---- Basic window setup ----
        setTitle("Hotel Management System - Login");
        setSize(350, 220);
        setLocationRelativeTo(null);              // center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // A panel with a simple grid: label next to field, one row each
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        panel.add(new JLabel()); // empty cell for spacing
        panel.add(loginButton);

        add(panel);

        // When the button is clicked, run our login-checking code
        loginButton.addActionListener(e -> attemptLogin());

        // Also allow pressing Enter in the password field to log in
        passwordField.addActionListener(e -> attemptLogin());
    }

    /**
     * Reads what the user typed, checks it against the database,
     * and either opens the main window or shows an error.
     */
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // STEP 35 (Validation): don't even hit the database if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.login(username, password);

            if (user == null) {
                // STEP 36 (Error handling): wrong credentials
                JOptionPane.showMessageDialog(this,
                        "Incorrect username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                // Success! Close the login window and open the main app,
                // passing along WHO logged in so we know their role.
                this.dispose();
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
            }

        } catch (SQLException ex) {
            // STEP 36 (Error handling): couldn't even reach the database
            JOptionPane.showMessageDialog(this,
                    "Could not connect to the database.\n" +
                    "Check that MySQL is running and your password in\n" +
                    "DatabaseConnection.java is correct.\n\nDetails: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
