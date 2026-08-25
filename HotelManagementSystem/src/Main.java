// ============================================================
//  Main.java
//  This is the file you RUN to start the whole application.
//  All it does is show the Login window.
// ============================================================

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater makes sure the GUI is built
        // on Java's special "GUI thread" - this is the standard,
        // safe way to start any Swing application.
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
