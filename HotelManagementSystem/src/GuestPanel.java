// ============================================================
//  GuestPanel.java
//  PHASE 4: The "Guests" tab.
//  Add Guest, Guest list, Search guest, Guest history.
// ============================================================

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GuestPanel extends JPanel {

    private final GuestDAO guestDAO = new GuestDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    private JTextField firstNameField, lastNameField, phoneField, emailField, idNumberField, addressField;
    private JTextField searchField;

    private JTable guestTable;
    private DefaultTableModel tableModel;

    public GuestPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        refreshTable(); // STEP 16: show the guest list right away
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));

        JPanel fields = new JPanel(new GridLayout(3, 4, 8, 8));
        fields.setBorder(BorderFactory.createTitledBorder("Guest Details"));

        fields.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        fields.add(firstNameField);

        fields.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        fields.add(lastNameField);

        fields.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        fields.add(phoneField);

        fields.add(new JLabel("Email:"));
        emailField = new JTextField();
        fields.add(emailField);

        fields.add(new JLabel("ID Number:"));
        idNumberField = new JTextField();
        fields.add(idNumberField);

        fields.add(new JLabel("Address:"));
        addressField = new JTextField();
        fields.add(addressField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Guest");
        JButton historyButton = new JButton("View History of Selected Guest");
        JButton clearButton = new JButton("Clear Form");

        buttons.add(addButton);
        buttons.add(historyButton);
        buttons.add(clearButton);

        addButton.addActionListener(e -> addGuest());
        historyButton.addActionListener(e -> viewGuestHistory());
        clearButton.addActionListener(e -> clearForm());

        outer.add(fields, BorderLayout.NORTH);
        outer.add(buttons, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildTablePanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search (name or phone):"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        searchButton.addActionListener(e -> searchGuests());
        showAllButton.addActionListener(e -> { searchField.setText(""); refreshTable(); });

        String[] columns = {"Guest ID", "First Name", "Last Name", "Phone", "Email", "ID Number", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        guestTable = new JTable(tableModel);

        outer.add(searchPanel, BorderLayout.NORTH);
        outer.add(new JScrollPane(guestTable), BorderLayout.CENTER);
        return outer;
    }

    /** STEP 15: Add Guest */
    private void addGuest() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String idNumber = idNumberField.getText().trim();
        String address = addressField.getText().trim();

        // Validation: first and last name are the minimum we need
        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "First Name and Last Name are required.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            guestDAO.addGuest(firstName, lastName, phone, email, idNumber, address);
            JOptionPane.showMessageDialog(this, "Guest added successfully.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 17: Search guest */
    private void searchGuests() {
        String keyword = searchField.getText().trim();
        try {
            List<Guest> results = keyword.isEmpty()
                    ? guestDAO.getAllGuests()
                    : guestDAO.searchGuests(keyword);
            fillTable(results);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 18: Guest history - shows all past/current reservations for the selected guest */
    private void viewGuestHistory() {
        int row = guestTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a guest from the table first.",
                    "No Guest Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int guestId = (int) tableModel.getValueAt(row, 0);
        String guestName = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);

        try {
            List<Reservation> history = reservationDAO.getReservationsByGuest(guestId);

            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, guestName + " has no reservation history yet.");
                return;
            }

            // Build a simple readable text report and show it in a dialog
            StringBuilder sb = new StringBuilder();
            sb.append("Reservation history for ").append(guestName).append(":\n\n");
            for (Reservation r : history) {
                sb.append("Room ").append(r.getRoomNumber())
                  .append(" | ").append(r.getCheckInDate())
                  .append(" to ").append(r.getCheckOutDate())
                  .append(" | Status: ").append(r.getStatus())
                  .append("\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "Guest History", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void refreshTable() {
        try {
            fillTable(guestDAO.getAllGuests());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void fillTable(List<Guest> guests) {
        tableModel.setRowCount(0);
        for (Guest g : guests) {
            tableModel.addRow(new Object[]{
                    g.getGuestId(), g.getFirstName(), g.getLastName(),
                    g.getPhone(), g.getEmail(), g.getIdNumber(), g.getAddress()
            });
        }
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        idNumberField.setText("");
        addressField.setText("");
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "A database error occurred:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
