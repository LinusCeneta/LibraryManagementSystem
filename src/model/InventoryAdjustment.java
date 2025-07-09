package model;

import java.sql.Date;

public class InventoryAdjustment {
    private int adjustmentId;
    private int copyId;
    private String adjustmentType; // e.g., "Lost", "Damaged", "Stolen"
    private String reason;
    private Date adjustmentDate;
    private String user; // staff username or ID

    public InventoryAdjustment() {}

    public InventoryAdjustment(int adjustmentId, int copyId, String adjustmentType, String reason, Date adjustmentDate, String user) {
        this.adjustmentId = adjustmentId;
        this.copyId = copyId;
        this.adjustmentType = adjustmentType;
        this.reason = reason;
        this.adjustmentDate = adjustmentDate;
        this.user = user;
    }

    public int getAdjustmentId() { return adjustmentId; }
    public void setAdjustmentId(int adjustmentId) { this.adjustmentId = adjustmentId; }

    public int getCopyId() { return copyId; }
    public void setCopyId(int copyId) { this.copyId = copyId; }

    public String getAdjustmentType() { return adjustmentType; }
    public void setAdjustmentType(String adjustmentType) { this.adjustmentType = adjustmentType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Date getAdjustmentDate() { return adjustmentDate; }
    public void setAdjustmentDate(Date adjustmentDate) { this.adjustmentDate = adjustmentDate; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
}
