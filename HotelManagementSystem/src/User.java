// ============================================================
//  User.java
//  Model class - holds the data for one login account.
// ============================================================

public class User {

    private int userId;
    private String username;
    private String role; // "Owner" or "Staff"

    public User(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }

    public boolean isOwner() {
        return "Owner".equals(role);
    }
}
