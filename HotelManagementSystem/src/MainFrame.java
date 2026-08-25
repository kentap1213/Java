// ============================================================
//  MainFrame.java
//  The main window, shown after a successful login.
//  It holds one tab per module - this is the "hub" of the app.
// ============================================================

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(User loggedInUser) {

        setTitle("Hotel Management System  —  Logged in as: " +
                loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // JTabbedPane = the row of tabs across the top of the window
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Rooms", new RoomPanel());
        tabs.addTab("Guests", new GuestPanel());
        tabs.addTab("Reservations", new ReservationPanel());
        tabs.addTab("Check-In / Check-Out", new CheckInOutPanel());
        tabs.addTab("Payments", new PaymentPanel());

        // STEP 34 (Owner/staff permissions): only an Owner can see
        // the Reports tab. Staff simply won't have it in their window.
        if (loggedInUser.isOwner()) {
            tabs.addTab("Reports", new ReportsPanel());
        }

        add(tabs, BorderLayout.CENTER);
    }
}
