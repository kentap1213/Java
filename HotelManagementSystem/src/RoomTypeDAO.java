// ============================================================
//  RoomTypeDAO.java
//  Small DAO just for reading room_types (used to fill
//  dropdown menus in the Rooms tab).
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    public List<RoomType> getAllRoomTypes() throws SQLException {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT * FROM room_types ORDER BY room_type_id";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new RoomType(
                        rs.getInt("room_type_id"),
                        rs.getString("type_name"),
                        rs.getDouble("base_price")
                ));
            }
        }
        return list;
    }
}
