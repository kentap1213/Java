// ============================================================
//  Guest.java
//  Model class - holds the data for one guest.
// ============================================================

public class Guest {

    private int guestId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String idNumber;
    private String address;

    public Guest() {
    }

    public Guest(int guestId, String firstName, String lastName, String phone,
                 String email, String idNumber, String address) {
        this.guestId = guestId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.idNumber = idNumber;
        this.address = address;
    }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Handy for showing "Firstname Lastname" together in the GUI
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // This controls how a Guest looks when placed inside a JComboBox
    // dropdown - we want "Juan Dela Cruz", not "Guest@1a2b3c".
    @Override
    public String toString() {
        return firstName + " " + lastName + " (ID " + guestId + ")";
    }
}
