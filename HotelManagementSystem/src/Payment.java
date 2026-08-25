// ============================================================
//  Payment.java
//  Model class - holds the data for one payment.
// ============================================================

import java.time.LocalDate;

public class Payment {

    private int paymentId;
    private int reservationId;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String notes;

    public Payment() {
    }

    public Payment(int paymentId, int reservationId, double amount,
                    LocalDate paymentDate, String paymentMethod, String notes) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
