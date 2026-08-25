// ============================================================
//  PaymentPanel.java
//  PHASE 7: The "Payments" tab.
//  Record payment, calculate balance, payment history.
// ============================================================

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PaymentPanel extends JPanel {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    private JTextField reservationIdField;
    private JTextField amountField;
    private JComboBox<String> methodCombo;
    private JTextField notesField;

    private JLabel balanceLabel;

    private JTable paymentTable;
    private DefaultTableModel tableModel;

    public PaymentPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));

        JPanel fields = new JPanel(new GridLayout(2, 4, 8, 8));
        fields.setBorder(BorderFactory.createTitledBorder("Record Payment"));

        fields.add(new JLabel("Reservation ID:"));
        reservationIdField = new JTextField();
        fields.add(reservationIdField);

        fields.add(new JLabel("Amount:"));
        amountField = new JTextField();
        fields.add(amountField);

        fields.add(new JLabel("Method:"));
        methodCombo = new JComboBox<>(new String[]{"Cash", "Card", "GCash", "Bank Transfer"});
        fields.add(methodCombo);

        fields.add(new JLabel("Notes:"));
        notesField = new JTextField();
        fields.add(notesField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton lookupButton = new JButton("Look Up Reservation / Balance");
        JButton saveButton = new JButton("Record Payment");
        buttons.add(lookupButton);
        buttons.add(saveButton);

        balanceLabel = new JLabel("Balance: (look up a reservation to see it)");
        balanceLabel.setFont(balanceLabel.getFont().deriveFont(Font.BOLD));

        JPanel south = new JPanel(new BorderLayout());
        south.add(buttons, BorderLayout.NORTH);
        south.add(balanceLabel, BorderLayout.SOUTH);

        lookupButton.addActionListener(e -> lookUpReservation());
        saveButton.addActionListener(e -> recordPayment());

        outer.add(fields, BorderLayout.NORTH);
        outer.add(south, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildTablePanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));
        outer.setBorder(BorderFactory.createTitledBorder("Payment History for Looked-Up Reservation"));

        String[] columns = {"Payment ID", "Amount", "Date", "Method", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        paymentTable = new JTable(tableModel);

        outer.add(new JScrollPane(paymentTable), BorderLayout.CENTER);
        return outer;
    }

    /** STEP 27 (Calculate balance) + STEP 28 (Payment history) */
    private void lookUpReservation() {
        Integer reservationId = parseReservationId();
        if (reservationId == null) return;

        try {
            Reservation res = reservationDAO.getReservationById(reservationId);
            if (res == null) {
                showWarning("No reservation found with that ID.");
                balanceLabel.setText("Balance: (reservation not found)");
                tableModel.setRowCount(0);
                return;
            }

            // Total bill = nightly rate x number of nights
            double rate = reservationDAO.getRoomRateForReservation(reservationId);
            long nights = res.getNumberOfNights();
            double totalBill = rate * nights;

            double totalPaid = paymentDAO.getTotalPaidForReservation(reservationId);
            double balance = totalBill - totalPaid;

            balanceLabel.setText(String.format(
                    "Guest: %s | Room: %s | %d night(s) x ₱%.2f = ₱%.2f total | Paid: ₱%.2f | Balance: ₱%.2f",
                    res.getGuestName(), res.getRoomNumber(), nights, rate, totalBill, totalPaid, balance));

            // STEP 28: show the payment history table for this reservation
            List<Payment> payments = paymentDAO.getPaymentsByReservation(reservationId);
            tableModel.setRowCount(0);
            for (Payment p : payments) {
                tableModel.addRow(new Object[]{
                        p.getPaymentId(), p.getAmount(), p.getPaymentDate(),
                        p.getPaymentMethod(), p.getNotes()
                });
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** STEP 26 (Record payment) */
    private void recordPayment() {
        Integer reservationId = parseReservationId();
        if (reservationId == null) return;

        String amountText = amountField.getText().trim();
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {
            showWarning("Amount must be a number, e.g. 1500 or 1500.50");
            return;
        }
        if (amount <= 0) {
            showWarning("Amount must be greater than zero.");
            return;
        }

        String method = (String) methodCombo.getSelectedItem();
        String notes = notesField.getText().trim();

        try {
            // Make sure the reservation actually exists before recording a payment for it
            Reservation res = reservationDAO.getReservationById(reservationId);
            if (res == null) {
                showWarning("No reservation found with that ID.");
                return;
            }

            paymentDAO.addPayment(reservationId, amount, LocalDate.now(), method, notes);
            JOptionPane.showMessageDialog(this, "Payment recorded.");

            amountField.setText("");
            notesField.setText("");

            lookUpReservation(); // refresh the balance and history shown

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private Integer parseReservationId() {
        String text = reservationIdField.getText().trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            showWarning("Please enter a valid Reservation ID number.");
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
