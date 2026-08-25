// ============================================================
//  ReportDAO.java
//  PHASE 8: The SQL behind each report. Each method returns a
//  ready-to-display block of text, built from a query.
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportDAO {

    /** STEP 29: Daily revenue - total payments received on one date */
    public String getDailyRevenueReport(LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total, COUNT(*) AS payment_count " +
                     "FROM payments WHERE payment_date = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                double total = rs.getDouble("total");
                int count = rs.getInt("payment_count");
                return String.format("Daily Revenue Report for %s%n%n" +
                                "Number of payments: %d%n" +
                                "Total revenue: ₱%.2f", date, count, total);
            }
        }
    }

    /** STEP 30: Room occupancy - how many rooms are occupied right now, as a percentage */
    public String getRoomOccupancyReport() throws SQLException {
        String sql = "SELECT " +
                     "SUM(CASE WHEN status = 'Occupied' THEN 1 ELSE 0 END) AS occupied, " +
                     "COUNT(*) AS total " +
                     "FROM rooms WHERE is_active = TRUE";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            int occupied = rs.getInt("occupied");
            int total = rs.getInt("total");
            double percent = total == 0 ? 0 : (occupied * 100.0 / total);

            return String.format("Room Occupancy Report%n%n" +
                            "Occupied rooms: %d%n" +
                            "Total active rooms: %d%n" +
                            "Occupancy rate: %.1f%%",
                    occupied, total, percent);
        }
    }

    /** STEP 31: Guest history report - every guest and how many stays they've had */
    public String getGuestHistoryReport() throws SQLException {
        String sql = "SELECT CONCAT(g.first_name, ' ', g.last_name) AS guest_name, " +
                     "COUNT(res.reservation_id) AS total_stays " +
                     "FROM guests g " +
                     "LEFT JOIN reservations res ON g.guest_id = res.guest_id " +
                     "GROUP BY g.guest_id, guest_name " +
                     "ORDER BY total_stays DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder("Guest History Report\n\n");
            while (rs.next()) {
                sb.append(rs.getString("guest_name"))
                  .append(" - ")
                  .append(rs.getInt("total_stays"))
                  .append(" reservation(s)\n");
            }
            return sb.toString();
        }
    }

    /** STEP 32: Reservation report - counts by status (Booked / CheckedIn / CheckedOut / Cancelled) */
    public String getReservationReport() throws SQLException {
        String sql = "SELECT status, COUNT(*) AS total FROM reservations GROUP BY status";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder sb = new StringBuilder("Reservation Report (by status)\n\n");
            while (rs.next()) {
                sb.append(rs.getString("status"))
                  .append(": ")
                  .append(rs.getInt("total"))
                  .append("\n");
            }
            return sb.toString();
        }
    }
}
