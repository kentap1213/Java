// ============================================================
//  PaymentDAO.java
//  All SQL for payments lives here (Phase 7).
// ============================================================

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    /** STEP 26 (Record payment) */
    public void addPayment(int reservationId, double amount, LocalDate paymentDate,
                            String paymentMethod, String notes) throws SQLException {
        String sql = "INSERT INTO payments (reservation_id, amount, payment_date, payment_method, notes) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            ps.setDouble(2, amount);
            ps.setDate(3, Date.valueOf(paymentDate));
            ps.setString(4, paymentMethod);
            ps.setString(5, notes);
            ps.executeUpdate();
        }
    }

    /** STEP 28 (Payment history): every payment made for one reservation */
    public List<Payment> getPaymentsByReservation(int reservationId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payments WHERE reservation_id = ? ORDER BY payment_date";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * STEP 27 (Calculate balance): total amount already paid
     * for one reservation (we subtract this from the room total
     * in the Payments panel to get the remaining balance).
     */
    public double getTotalPaidForReservation(int reservationId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_paid " +
                     "FROM payments WHERE reservation_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble("total_paid");
            }
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        return new Payment(
                rs.getInt("payment_id"),
                rs.getInt("reservation_id"),
                rs.getDouble("amount"),
                rs.getDate("payment_date").toLocalDate(),
                rs.getString("payment_method"),
                rs.getString("notes")
        );
    }
}
