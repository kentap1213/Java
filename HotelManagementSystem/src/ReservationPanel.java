// ============================================================
//  ReservationPanel.java
//  PHASE 5: The "Reservations" tab.
//  Create reservation, check room availability, save it,
//  and view all existing reservations.
// ============================================================

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReservationPanel extends JPanel {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final GuestDAO guestDAO = new GuestDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    private JComboBox<Guest> guestCombo;
    private JComboBox<Room> roomCombo;
    private JTextField checkInField;   // format: yyyy-MM-dd
    private JTextField checkOutField;  // format: yyyy-MM-dd

    private JTable reservationTable;
    private DefaultTableModel tableModel;

    public ReservationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        loadGuestsAndRooms();
        refreshTable(); // STEP 22: View reservations
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));

        JPanel fields = new JPanel(new GridLayout(2, 4, 8, 8));
        fields.setBorder(BorderFactory.createTitledBorder(
                "New Reservation  (dates use format: yyyy-MM-dd, e.g. 2026-09-01)"));

        fields.add(new JLabel("Guest:"));
        guestCombo = new JComboBox<>();
        fields.add(guestCombo);

        fields.add(new JLabel("Room (Available only):"));
        roomCombo = new JComboBox<>();
        fields.add(roomCombo);

        fields.add(new JLabel("Check-in Date:"));
        checkInField = new JTextField();
        fields.add(checkInField);

        fields.add(new JLabel("Check-out Date:"));
        checkOutField = new JTextField();
        fields.add(checkOutField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton checkButton = new JButton("Check Availability");
        JButton saveButton = new JButton("Save Reservation");
        JButton refreshButton = new JButton("Refresh Lists");

        buttons.add(checkButton);
        buttons.add(saveButton);
        buttons.add(refreshButton);

        checkButton.addActionListener(e -> checkAvailability());
        saveButton.addActionListener(e -> saveReservation());
        refreshButton.addActionListener(e -> loadGuestsAndRooms());

        outer.add(fields, BorderLayout.NORTH);
        outer.add(buttons, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildTablePanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));
        outer.setBorder(BorderFactory.createTitledBorder("All Reservations"));

        String[] columns = {"Res. ID", "Guest", "Room", "Check-in", "Check-out", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(tableModel);

        outer.add(new JScrollPane(reservationTable), BorderLayout.CENTER);
        return outer;
    }

    // Fill the Guest and Room dropdowns with fresh data from the database
    private void loadGuestsAndRooms() {
        try {
            guestCombo.removeAllItems();
            for (Guest g : guestDAO.getAllGuests()) {
                guestCombo.addItem(g);
            }

            roomCombo.removeAllItems();
            for (Room r : roomDAO.getAvailableRooms()) {
                roomCombo.addItem(r);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 20: Check room availability (for the chosen dates) */
    private void checkAvailability() {
        Room room = (Room) roomCombo.getSelectedItem();
        LocalDate checkIn = parseDate(checkInField.getText().trim());
        LocalDate checkOut = parseDate(checkOutField.getText().trim());

        if (room == null) {
            showWarning("Please select a room.");
            return;
        }
        if (checkIn == null || checkOut == null) {
            showWarning("Please enter valid dates in yyyy-MM-dd format.");
            return;
        }
        if (!checkOut.isAfter(checkIn)) {
            showWarning("Check-out date must be after the check-in date.");
            return;
        }

        try {
            boolean available = reservationDAO.isRoomAvailable(room.getRoomId(), checkIn, checkOut);
            if (available) {
                JOptionPane.showMessageDialog(this,
                        "Room " + room.getRoomNumber() + " IS available for those dates.",
                        "Available", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Room " + room.getRoomNumber() + " is ALREADY booked for part of those dates.",
                        "Not Available", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 21: Save reservation */
    private void saveReservation() {
        Guest guest = (Guest) guestCombo.getSelectedItem();
        Room room = (Room) roomCombo.getSelectedItem();
        LocalDate checkIn = parseDate(checkInField.getText().trim());
        LocalDate checkOut = parseDate(checkOutField.getText().trim());

        if (guest == null || room == null) {
            showWarning("Please select both a guest and a room.");
            return;
        }
        if (checkIn == null || checkOut == null) {
            showWarning("Please enter valid dates in yyyy-MM-dd format.");
            return;
        }
        if (!checkOut.isAfter(checkIn)) {
            showWarning("Check-out date must be after the check-in date.");
            return;
        }

        try {
            // Always re-check availability right before saving, in case
            // someone else booked the room in the meantime.
            if (!reservationDAO.isRoomAvailable(room.getRoomId(), checkIn, checkOut)) {
                showWarning("Sorry, this room is no longer available for those dates.");
                return;
            }

            reservationDAO.addReservation(guest.getGuestId(), room.getRoomId(), checkIn, checkOut);
            JOptionPane.showMessageDialog(this, "Reservation saved successfully.");

            checkInField.setText("");
            checkOutField.setText("");
            refreshTable();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 22: View reservations */
    private void refreshTable() {
        try {
            List<Reservation> list = reservationDAO.getAllReservations();
            tableModel.setRowCount(0);
            for (Reservation r : list) {
                tableModel.addRow(new Object[]{
                        r.getReservationId(), r.getGuestName(), r.getRoomNumber(),
                        r.getCheckInDate(), r.getCheckOutDate(), r.getStatus()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // Turns text like "2026-09-01" into a LocalDate, or returns null if invalid
    private LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Missing/Invalid Information", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "A database error occurred:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
