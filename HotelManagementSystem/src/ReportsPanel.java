// ============================================================
//  ReportsPanel.java
//  PHASE 8: The "Reports" tab (only visible to the Owner role).
//  Four buttons, each showing a different report in the text area.
// ============================================================

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportsPanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();
    private JTextArea outputArea;
    private JTextField dateField; // used only by the Daily Revenue report

    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildButtonPanel(), BorderLayout.NORTH);
        add(buildOutputArea(), BorderLayout.CENTER);
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JLabel("Date for Daily Revenue (yyyy-MM-dd):"));
        dateField = new JTextField(LocalDate.now().toString(), 10);
        panel.add(dateField);

        JButton revenueButton = new JButton("Daily Revenue");
        JButton occupancyButton = new JButton("Room Occupancy");
        JButton guestHistoryButton = new JButton("Guest History");
        JButton reservationButton = new JButton("Reservation Report");

        panel.add(revenueButton);
        panel.add(occupancyButton);
        panel.add(guestHistoryButton);
        panel.add(reservationButton);

        revenueButton.addActionListener(e -> showDailyRevenue());
        occupancyButton.addActionListener(e -> runReport(reportDAO::getRoomOccupancyReport));
        guestHistoryButton.addActionListener(e -> runReport(reportDAO::getGuestHistoryReport));
        reservationButton.addActionListener(e -> runReport(reportDAO::getReservationReport));

        return panel;
    }

    private JScrollPane buildOutputArea() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        outputArea.setText("Click a report button above to see results here.");
        return new JScrollPane(outputArea);
    }

    /** STEP 29: Daily revenue (needs the date field, so it's separate from the others) */
    private void showDailyRevenue() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            outputArea.setText(reportDAO.getDailyRevenueReport(date));
        } catch (Exception ex) {
            outputArea.setText("Could not run report. Make sure the date is in yyyy-MM-dd format.\n\n" + ex.getMessage());
        }
    }

    // A small "functional interface" trick: this lets us pass any of the
    // no-argument report methods (occupancy, guest history, reservation)
    // into ONE shared method, instead of writing three nearly-identical
    // try/catch blocks.
    private interface ReportSupplier {
        String getReport() throws SQLException;
    }

    private void runReport(ReportSupplier supplier) {
        try {
            outputArea.setText(supplier.getReport());
        } catch (SQLException ex) {
            outputArea.setText("A database error occurred:\n" + ex.getMessage());
        }
    }
}
