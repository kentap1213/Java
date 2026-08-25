// ============================================================
//  GuestDAO.java
//  All SQL for guests lives here (Phase 4).
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    /** STEP 15 (Add Guest) */
    public void addGuest(String firstName, String lastName, String phone,
                          String email, String idNumber, String address) throws SQLException {
        String sql = "INSERT INTO guests (first_name, last_name, phone, email, id_number, address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, idNumber);
            ps.setString(6, address);
            ps.executeUpdate();
        }
    }

    /** STEP 16 (Guest list) */
    public List<Guest> getAllGuests() throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY last_name, first_name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                guests.add(mapRow(rs));
            }
        }
        return guests;
    }

    /** STEP 17 (Search guest) - by first name, last name, or phone */
    public List<Guest> searchGuests(String keyword) throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests " +
                     "WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? " +
                     "ORDER BY last_name, first_name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setString(3, likeKeyword);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    guests.add(mapRow(rs));
                }
            }
        }
        return guests;
    }

    /** Get a single guest by their ID (used when building reservation history) */
    public Guest getGuestById(int guestId) throws SQLException {
        String sql = "SELECT * FROM guests WHERE guest_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null; // no guest found with that ID
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        return new Guest(
                rs.getInt("guest_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("id_number"),
                rs.getString("address")
        );
    }
}
