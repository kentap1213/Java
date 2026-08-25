// ============================================================
//  Room.java
//  A "model" class - it just HOLDS the data for one room.
//  No database code lives here, that's RoomDAO's job.
// ============================================================

public class Room {

    private int roomId;
    private String roomNumber;
    private int roomTypeId;
    private String typeName;      // filled in from a JOIN, for display only
    private double basePrice;     // filled in from a JOIN, for display only
    private int floor;
    private String status;
    private boolean active;

    // Empty constructor (used when building a new Room to insert)
    public Room() {
    }

    // Full constructor (used when reading a Room back from the database)
    public Room(int roomId, String roomNumber, int roomTypeId, String typeName,
                double basePrice, int floor, String status, boolean active) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomTypeId = roomTypeId;
        this.typeName = typeName;
        this.basePrice = basePrice;
        this.floor = floor;
        this.status = status;
        this.active = active;
    }

    // ---- Getters and setters (simple access to each field) ----

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(int roomTypeId) { this.roomTypeId = roomTypeId; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // This controls how a Room looks when placed inside a JComboBox
    // dropdown - we want "Room 101 - Single", not "Room@1a2b3c".
    @Override
    public String toString() {
        return "Room " + roomNumber + " - " + typeName;
    }
}
