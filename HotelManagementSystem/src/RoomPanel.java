// ============================================================
//  RoomPanel.java
//  PHASE 3: The "Rooms" tab.
//  Lets staff Add, Display, Edit, Deactivate, and Search rooms.
// ============================================================

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RoomPanel extends JPanel {

    private final RoomDAO roomDAO = new RoomDAO();
    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    // ---- Form fields (top of the panel) ----
    private JTextField roomNumberField;
    private JComboBox<RoomType> roomTypeCombo;
    private JTextField floorField;
    private JComboBox<String> statusCombo;

    // ---- Search field ----
    private JTextField searchField;

    // ---- Table (bottom of the panel) ----
    private JTable roomTable;
    private DefaultTableModel tableModel;

    // Remembers which room is selected in the table, so Edit/Deactivate
    // know which row to act on. -1 means "nothing selected".
    private int selectedRoomId = -1;

    public RoomPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        loadRoomTypesIntoCombo();
        refreshTable(); // STEP 11: show all rooms as soon as the tab opens
    }

    // ------------------------------------------------------------
    // BUILDING THE FORM (top section: add/edit fields + buttons)
    // ------------------------------------------------------------
    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));

        // --- Row 1: the input fields ---
        JPanel fields = new JPanel(new GridLayout(2, 4, 8, 8));
        fields.setBorder(BorderFactory.createTitledBorder("Room Details"));

        fields.add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        fields.add(roomNumberField);

        fields.add(new JLabel("Room Type:"));
        roomTypeCombo = new JComboBox<>();
        fields.add(roomTypeCombo);

        fields.add(new JLabel("Floor:"));
        floorField = new JTextField();
        fields.add(floorField);

        fields.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance"});
        fields.add(statusCombo);

        // --- Row 2: the action buttons ---
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Room");
        JButton updateButton = new JButton("Update Selected Room");
        JButton deactivateButton = new JButton("Deactivate Selected Room");
        JButton clearButton = new JButton("Clear Form");

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deactivateButton);
        buttons.add(clearButton);

        addButton.addActionListener(e -> addRoom());
        updateButton.addActionListener(e -> updateRoom());
        deactivateButton.addActionListener(e -> deactivateRoom());
        clearButton.addActionListener(e -> clearForm());

        outer.add(fields, BorderLayout.NORTH);
        outer.add(buttons, BorderLayout.SOUTH);
        return outer;
    }

    // ------------------------------------------------------------
    // BUILDING THE TABLE (bottom section: search bar + room list)
    // ------------------------------------------------------------
    private JPanel buildTablePanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));

        // --- Search bar ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search room number:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        JButton showAllButton = new JButton("Show All");
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        searchButton.addActionListener(e -> searchRooms());
        showAllButton.addActionListener(e -> { searchField.setText(""); refreshTable(); });

        // --- Table ---
        String[] columns = {"Room ID", "Room Number", "Type", "Price/Night", "Floor", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            // Make the whole table read-only - editing happens
            // through the form fields above, not by typing in cells.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        roomTable.getSelectionModel().addListSelectionListener(e -> loadSelectedRoomIntoForm());

        outer.add(searchPanel, BorderLayout.NORTH);
        outer.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        return outer;
    }

    // ------------------------------------------------------------
    // ACTIONS
    // ------------------------------------------------------------

    /** STEP 10: Add Room */
    private void addRoom() {
        String roomNumber = roomNumberField.getText().trim();
        RoomType selectedType = (RoomType) roomTypeCombo.getSelectedItem();
        String floorText = floorField.getText().trim();

        // ---- Validation (Phase 9, Step 35) ----
        if (roomNumber.isEmpty() || floorText.isEmpty() || selectedType == null) {
            showWarning("Please fill in Room Number, Room Type, and Floor.");
            return;
        }

        int floor;
        try {
            floor = Integer.parseInt(floorText);
        } catch (NumberFormatException ex) {
            showWarning("Floor must be a whole number, e.g. 1, 2, 3.");
            return;
        }

        try {
            roomDAO.addRoom(roomNumber, selectedType.getRoomTypeId(), floor);
            showInfo("Room " + roomNumber + " added successfully.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 12: Edit Room */
    private void updateRoom() {
        if (selectedRoomId == -1) {
            showWarning("Please select a room from the table first.");
            return;
        }

        String roomNumber = roomNumberField.getText().trim();
        RoomType selectedType = (RoomType) roomTypeCombo.getSelectedItem();
        String floorText = floorField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        if (roomNumber.isEmpty() || floorText.isEmpty() || selectedType == null) {
            showWarning("Please fill in Room Number, Room Type, and Floor.");
            return;
        }

        int floor;
        try {
            floor = Integer.parseInt(floorText);
        } catch (NumberFormatException ex) {
            showWarning("Floor must be a whole number.");
            return;
        }

        try {
            roomDAO.updateRoom(selectedRoomId, roomNumber, selectedType.getRoomTypeId(), floor, status);
            showInfo("Room updated successfully.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 13: Deactivate Room */
    private void deactivateRoom() {
        if (selectedRoomId == -1) {
            showWarning("Please select a room from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deactivate this room? It will be hidden from the room list\n" +
                "but its history is kept safe.",
                "Confirm Deactivate", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            roomDAO.deactivateRoom(selectedRoomId);
            showInfo("Room deactivated.");
            clearForm();
            refreshTable();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 14: Search Room */
    private void searchRooms() {
        String keyword = searchField.getText().trim();
        try {
            List<Room> results = keyword.isEmpty()
                    ? roomDAO.getAllRooms()
                    : roomDAO.searchRooms(keyword);
            fillTable(results);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 11: Display Rooms (also used to refresh after any change) */
    private void refreshTable() {
        try {
            fillTable(roomDAO.getAllRooms());
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void fillTable(List<Room> rooms) {
        tableModel.setRowCount(0); // clear existing rows
        for (Room room : rooms) {
            tableModel.addRow(new Object[]{
                    room.getRoomId(),
                    room.getRoomNumber(),
                    room.getTypeName(),
                    room.getBasePrice(),
                    room.getFloor(),
                    room.getStatus()
            });
        }
    }

    // When the user clicks a row in the table, copy that room's
    // data into the form fields above, ready for editing.
    private void loadSelectedRoomIntoForm() {
        int row = roomTable.getSelectedRow();
        if (row == -1) return;

        selectedRoomId = (int) tableModel.getValueAt(row, 0);
        roomNumberField.setText(String.valueOf(tableModel.getValueAt(row, 1)));

        String typeName = String.valueOf(tableModel.getValueAt(row, 2));
        for (int i = 0; i < roomTypeCombo.getItemCount(); i++) {
            if (roomTypeCombo.getItemAt(i).getTypeName().equals(typeName)) {
                roomTypeCombo.setSelectedIndex(i);
                break;
            }
        }

        floorField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        statusCombo.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        selectedRoomId = -1;
        roomNumberField.setText("");
        floorField.setText("");
        statusCombo.setSelectedIndex(0);
        roomTable.clearSelection();
    }

    private void loadRoomTypesIntoCombo() {
        try {
            List<RoomType> types = roomTypeDAO.getAllRoomTypes();
            for (RoomType type : types) {
                roomTypeCombo.addItem(type);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    // ---- Small helper methods so we don't repeat dialog code everywhere ----
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Missing Information", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "A database error occurred:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
