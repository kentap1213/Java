// ============================================================
//  DatabaseConnection.java
//  ONE job only: open a connection to MySQL.
//  Every DAO class (RoomDAO, GuestDAO, etc.) calls
//  DatabaseConnection.getConnection() when it needs to run SQL.
// ============================================================

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // ---- SETTINGS: change these to match YOUR computer -----
    private static final String URL =
            "jdbc:mysql://localhost:3306/hotel_management";

    private static final String USERNAME = "root";

    // ‼ CHANGE THIS to your real MySQL password ‼
    private static final String PASSWORD = "your_password_here";
    // -----------------------------------------------------------

    /**
     * Opens and returns a new connection to the MySQL database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
