// ============================================================
//  Reservation.java
//  Model class - holds the data for one reservation (booking).
// ============================================================

import java.time.LocalDate;

public class Reservation {

    private int reservationId;
    private int guestId;
    private String guestName;      // filled in from a JOIN, for display
    private int roomId;
    private String roomNumber;     // filled in from a JOIN, for display
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String status;

    public Reservation() {
    }

    public Reservation(int reservationId, int guestId, String guestName, int roomId,
                        String roomNumber, LocalDate checkInDate, LocalDate checkOutDate,
                        String status) {
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.guestName = guestName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Number of nights this reservation covers - used to calculate price
    public long getNumberOfNights() {
        return java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
}
