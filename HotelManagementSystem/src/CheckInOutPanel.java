// ============================================================
//  CheckInOutPanel.java
//  PHASE 6: The "Check-In / Check-Out" tab.
//  Check-in, Check-out, and updating the room's status to match.
// ============================================================

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CheckInOutPanel extends JPanel {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    private DefaultTableModel checkInModel;
    private DefaultTableModel checkOutModel;
    private JTable checkInTable;
    private JTable checkOutTable;

    public CheckInOutPanel() {
        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildCheckInSide());
        add(buildCheckOutSide());

        refreshBothTables();
    }

    // ---- LEFT SIDE: guests who are booked and about to arrive ----
    private JPanel buildCheckInSide() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Ready to Check In (status = Booked)"));

        String[] columns = {"Res. ID", "Guest", "Room", "Check-in", "Check-out"};
        checkInModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        checkInTable = new JTable(checkInModel);

        JButton checkInButton = new JButton("Check In Selected Guest");
        checkInButton.addActionListener(e -> checkInSelectedGuest());

        panel.add(new JScrollPane(checkInTable), BorderLayout.CENTER);
        panel.add(checkInButton, BorderLayout.SOUTH);
        return panel;
    }

    // ---- RIGHT SIDE: guests currently staying, ready to leave ----
    private JPanel buildCheckOutSide() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Ready to Check Out (status = CheckedIn)"));

        String[] columns = {"Res. ID", "Guest", "Room", "Check-in", "Check-out"};
        checkOutModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        checkOutTable = new JTable(checkOutModel);

        JButton checkOutButton = new JButton("Check Out Selected Guest");
        checkOutButton.addActionListener(e -> checkOutSelectedGuest());

        panel.add(new JScrollPane(checkOutTable), BorderLayout.CENTER);
        panel.add(checkOutButton, BorderLayout.SOUTH);
        return panel;
    }

    /** STEP 23 (Check-in) + STEP 25 (Update room status) */
    private void checkInSelectedGuest() {
        int row = checkInTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select a reservation to check in.");
            return;
        }

        int reservationId = (int) checkInModel.getValueAt(row, 0);

        try {
            Reservation res = reservationDAO.getReservationById(reservationId);

            // 1. Flip the reservation status to CheckedIn
            reservationDAO.updateReservationStatus(reservationId, "CheckedIn");

            // 2. Flip the room's status to Occupied
            roomDAO.updateRoomStatus(res.getRoomId(), "Occupied");

            JOptionPane.showMessageDialog(this,
                    res.getGuestName() + " checked in to Room " + res.getRoomNumber() + ".");

            refreshBothTables();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 24 (Check-out) + STEP 25 (Update room status) */
    private void checkOutSelectedGuest() {
        int row = checkOutTable.getSelectedRow();
        if (row == -1) {
            showWarning("Please select a reservation to check out.");
            return;
        }

        int reservationId = (int) checkOutModel.getValueAt(row, 0);

        try {
            Reservation res = reservationDAO.getReservationById(reservationId);

            // 1. Flip the reservation status to CheckedOut
            reservationDAO.updateReservationStatus(reservationId, "CheckedOut");

            // 2. Flip the room's status back to Available
            roomDAO.updateRoomStatus(res.getRoomId(), "Available");

            JOptionPane.showMessageDialog(this,
                    res.getGuestName() + " checked out of Room " + res.getRoomNumber() + ".");

            refreshBothTables();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void refreshBothTables() {
        try {
            List<Reservation> booked = reservationDAO.getBookedReservations();
            checkInModel.setRowCount(0);
            for (Reservation r : booked) {
                checkInModel.addRow(new Object[]{
                        r.getReservationId(), r.getGuestName(), r.getRoomNumber(),
                        r.getCheckInDate(), r.getCheckOutDate()
                });
            }

            List<Reservation> checkedIn = reservationDAO.getCheckedInReservations();
            checkOutModel.setRowCount(0);
            for (Reservation r : checkedIn) {
                checkOutModel.addRow(new Object[]{
                        r.getReservationId(), r.getGuestName(), r.getRoomNumber(),
                        r.getCheckInDate(), r.getCheckOutDate()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "No Selection", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "A database error occurred:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
