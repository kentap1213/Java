// ============================================================
//  RoomType.java
//  Model class for a row in the room_types table
//  (Single, Double, Deluxe, Suite...).
// ============================================================

public class RoomType {

    private int roomTypeId;
    private String typeName;
    private double basePrice;

    public RoomType(int roomTypeId, String typeName, double basePrice) {
        this.roomTypeId = roomTypeId;
        this.typeName = typeName;
        this.basePrice = basePrice;
    }

    public int getRoomTypeId() { return roomTypeId; }
    public String getTypeName() { return typeName; }
    public double getBasePrice() { return basePrice; }

    // This controls how the object looks when placed inside a
    // JComboBox - we want it to show the name, not "RoomType@1a2b3c"
    @Override
    public String toString() {
        return typeName + " (₱" + basePrice + "/night)";
    }
}
