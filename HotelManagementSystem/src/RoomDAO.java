// ============================================================
//  RoomDAO.java
//  "DAO" = Data Access Object. This class is the ONLY place
//  that runs SQL for rooms. RoomPanel calls these methods
//  instead of writing SQL itself - that keeps the GUI code
//  clean and the SQL code in one easy-to-find spot.
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    /**
     * STEP 10 (Add Room): insert a new room into the database.
     */
    public void addRoom(String roomNumber, int roomTypeId, int floor) throws SQLException {
        String sql = "INSERT INTO rooms (room_number, room_type_id, floor) VALUES (?, ?, ?)";

        // try-with-resources: Java automatically closes the connection
        // and statement for us when the block finishes, even if an error happens.
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roomNumber);
            ps.setInt(2, roomTypeId);
            ps.setInt(3, floor);
            ps.executeUpdate();
        }
    }

    /**
     * STEP 11 (Display Rooms): get every ACTIVE room, joined with
     * its room type name and price so the table can show everything.
     */
    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT r.room_id, r.room_number, r.room_type_id, rt.type_name, " +
                     "rt.base_price, r.floor, r.status, r.is_active " +
                     "FROM rooms r " +
                     "JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
                     "WHERE r.is_active = TRUE " +
                     "ORDER BY r.room_number";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        }
        return rooms;
    }

    /**
     * STEP 14 (Search Room): find rooms whose room number CONTAINS
     * the given text (so searching "10" finds "101", "102", "103"...).
     */
    public List<Room> searchRooms(String keyword) throws SQLException {
        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT r.room_id, r.room_number, r.room_type_id, rt.type_name, " +
                     "rt.base_price, r.floor, r.status, r.is_active " +
                     "FROM rooms r " +
                     "JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
                     "WHERE r.is_active = TRUE AND r.room_number LIKE ? " +
                     "ORDER BY r.room_number";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // The % signs mean "any text before or after" - a "contains" search
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
        }
        return rooms;
    }

    /**
     * STEP 12 (Edit Room): update an existing room's details.
     */
    public void updateRoom(int roomId, String roomNumber, int roomTypeId, int floor, String status) throws SQLException {
        String sql = "UPDATE rooms SET room_number = ?, room_type_id = ?, floor = ?, status = ? " +
                     "WHERE room_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, roomNumber);
            ps.setInt(2, roomTypeId);
            ps.setInt(3, floor);
            ps.setString(4, status);
            ps.setInt(5, roomId);
            ps.executeUpdate();
        }
    }

    /**
     * STEP 13 (Deactivate Room): we NEVER delete rooms from the database
     * (that would break old reservation history). Instead we just flip
     * is_active to FALSE, so it stops showing up in the room list.
     */
    public void deactivateRoom(int roomId) throws SQLException {
        String sql = "UPDATE rooms SET is_active = FALSE WHERE room_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.executeUpdate();
        }
    }

    /**
     * Update just a room's status (used during check-in / check-out).
     */
    public void updateRoomStatus(int roomId, String status) throws SQLException {
        String sql = "UPDATE rooms SET status = ? WHERE room_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, roomId);
            ps.executeUpdate();
        }
    }

    /**
     * Get only rooms that are currently "Available" (used when
     * creating a new reservation, so staff can't double-book a room).
     */
    public List<Room> getAvailableRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT r.room_id, r.room_number, r.room_type_id, rt.type_name, " +
                     "rt.base_price, r.floor, r.status, r.is_active " +
                     "FROM rooms r " +
                     "JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
                     "WHERE r.is_active = TRUE AND r.status = 'Available' " +
                     "ORDER BY r.room_number";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        }
        return rooms;
    }

    // Small helper so we don't repeat this ResultSet-reading code
    // in every method above.
    private Room mapRow(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("room_id"),
                rs.getString("room_number"),
                rs.getInt("room_type_id"),
                rs.getString("type_name"),
                rs.getDouble("base_price"),
                rs.getInt("floor"),
                rs.getString("status"),
                rs.getBoolean("is_active")
        );
    }
}
