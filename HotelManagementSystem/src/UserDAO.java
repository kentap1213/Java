// ============================================================
//  UserDAO.java
//  Handles checking a username/password against the database
//  (Phase 9, Step 33: Login).
//
//  NOTE FOR LEARNING: this compares plain-text passwords, which
//  is fine while you're learning, but a REAL system must hash
//  passwords (e.g. with BCrypt) so they aren't stored as
//  readable text. See the README for a pointer on this.
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Checks the username and password. Returns the matching User
     * if correct, or null if the login is wrong.
     */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null; // no matching account found
    }
}
