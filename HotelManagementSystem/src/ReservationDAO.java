// ============================================================
//  ReservationDAO.java
//  All SQL for reservations lives here (Phase 5 + Phase 6
//  check-in/out, since check-in/out just changes a
//  reservation's status).
// ============================================================

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    /**
     * STEP 20 (Check room availability): returns TRUE if the room is
     * free for the whole requested date range (no overlapping booking).
     */
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        // Two date ranges overlap if: existing.checkIn < new.checkOut
        // AND existing.checkOut > new.checkIn.
        // We only worry about bookings that are still "active"
        // (not Cancelled and not already CheckedOut).
        String sql = "SELECT COUNT(*) AS overlap_count FROM reservations " +
                     "WHERE room_id = ? " +
                     "AND status IN ('Booked','CheckedIn') " +
                     "AND check_in_date < ? AND check_out_date > ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(checkOut));
            ps.setDate(3, Date.valueOf(checkIn));

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                int overlaps = rs.getInt("overlap_count");
                return overlaps == 0; // available only if nothing overlaps
            }
        }
    }

    /**
     * STEP 21 (Save reservation): create a new booking.
     */
    public void addReservation(int guestId, int roomId, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        String sql = "INSERT INTO reservations (guest_id, room_id, check_in_date, check_out_date, status) " +
                     "VALUES (?, ?, ?, ?, 'Booked')";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, guestId);
            ps.setInt(2, roomId);
            ps.setDate(3, Date.valueOf(checkIn));
            ps.setDate(4, Date.valueOf(checkOut));
            ps.executeUpdate();
        }
    }

    /**
     * STEP 22 (View reservations): all bookings, newest first,
     * with guest name and room number already joined in.
     */
    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> list = new ArrayList<>();

        String sql = "SELECT res.reservation_id, res.guest_id, " +
                     "CONCAT(g.first_name, ' ', g.last_name) AS guest_name, " +
                     "res.room_id, r.room_number, res.check_in_date, res.check_out_date, res.status " +
                     "FROM reservations res " +
                     "JOIN guests g ON res.guest_id = g.guest_id " +
                     "JOIN rooms r ON res.room_id = r.room_id " +
                     "ORDER BY res.reservation_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Reservations that currently have status 'Booked' - these are
     * the ones eligible to check in (used by the Check-in tab).
     */
    public List<Reservation> getBookedReservations() throws SQLException {
        return getReservationsByStatus("Booked");
    }

    /**
     * Reservations that currently have status 'CheckedIn' - these are
     * the ones eligible to check out (used by the Check-out tab).
     */
    public List<Reservation> getCheckedInReservations() throws SQLException {
        return getReservationsByStatus("CheckedIn");
    }

    private List<Reservation> getReservationsByStatus(String status) throws SQLException {
        List<Reservation> list = new ArrayList<>();

        String sql = "SELECT res.reservation_id, res.guest_id, " +
                     "CONCAT(g.first_name, ' ', g.last_name) AS guest_name, " +
                     "res.room_id, r.room_number, res.check_in_date, res.check_out_date, res.status " +
                     "FROM reservations res " +
                     "JOIN guests g ON res.guest_id = g.guest_id " +
                     "JOIN rooms r ON res.room_id = r.room_id " +
                     "WHERE res.status = ? " +
                     "ORDER BY res.check_in_date";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * All reservations belonging to one guest (Phase 4, Step 18:
     * "Guest history").
     */
    public List<Reservation> getReservationsByGuest(int guestId) throws SQLException {
        List<Reservation> list = new ArrayList<>();

        String sql = "SELECT res.reservation_id, res.guest_id, " +
                     "CONCAT(g.first_name, ' ', g.last_name) AS guest_name, " +
                     "res.room_id, r.room_number, res.check_in_date, res.check_out_date, res.status " +
                     "FROM reservations res " +
                     "JOIN guests g ON res.guest_id = g.guest_id " +
                     "JOIN rooms r ON res.room_id = r.room_id " +
                     "WHERE res.guest_id = ? " +
                     "ORDER BY res.check_in_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * STEP 23 (Check-in): flips a reservation's status to CheckedIn.
     * STEP 24 (Check-out): flips a reservation's status to CheckedOut.
     * (Same method, different status text passed in.)
     */
    public void updateReservationStatus(int reservationId, String status) throws SQLException {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, reservationId);
            ps.executeUpdate();
        }
    }

    /** Look up one reservation by its ID (used by the Payments tab). */
    public Reservation getReservationById(int reservationId) throws SQLException {
        String sql = "SELECT res.reservation_id, res.guest_id, " +
                     "CONCAT(g.first_name, ' ', g.last_name) AS guest_name, " +
                     "res.room_id, r.room_number, res.check_in_date, res.check_out_date, res.status " +
                     "FROM reservations res " +
                     "JOIN guests g ON res.guest_id = g.guest_id " +
                     "JOIN rooms r ON res.room_id = r.room_id " +
                     "WHERE res.reservation_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * The base nightly rate for a reservation's room (used to
     * calculate the total bill in the Payments tab).
     */
    public double getRoomRateForReservation(int reservationId) throws SQLException {
        String sql = "SELECT rt.base_price FROM reservations res " +
                     "JOIN rooms r ON res.room_id = r.room_id " +
                     "JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
                     "WHERE res.reservation_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("base_price");
                }
            }
        }
        return 0.0;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("reservation_id"),
                rs.getInt("guest_id"),
                rs.getString("guest_name"),
                rs.getInt("room_id"),
                rs.getString("room_number"),
                rs.getDate("check_in_date").toLocalDate(),
                rs.getDate("check_out_date").toLocalDate(),
                rs.getString("status")
        );
    }
}
