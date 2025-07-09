package model;

import java.sql.Date;

public class HoldReservation {
    private int holdId;
    private int memberId;
    private int copyId;
    private Date requestDate;
    private String status; // "Active", "Fulfilled", "Cancelled"
    private Date pickupExpiryDate;

    // Constructors
    public HoldReservation() {}

    public HoldReservation(int holdId, int memberId, int copyId, Date requestDate, String status, Date pickupExpiryDate) {
        this.holdId = holdId;
        this.memberId = memberId;
        this.copyId = copyId;
        this.requestDate = requestDate;
        this.status = status;
        this.pickupExpiryDate = pickupExpiryDate;
    }

    // Getters and Setters
    public int getHoldId() { return holdId; }
    public void setHoldId(int holdId) { this.holdId = holdId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public int getCopyId() { return copyId; }
    public void setCopyId(int copyId) { this.copyId = copyId; }

    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getPickupExpiryDate() { return pickupExpiryDate; }
    public void setPickupExpiryDate(Date pickupExpiryDate) { this.pickupExpiryDate = pickupExpiryDate; }
}
