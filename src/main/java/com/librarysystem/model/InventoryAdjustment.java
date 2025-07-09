package com.librarysystem.model;

import java.sql.Date;

public class InventoryAdjustment {
    private int adjustmentID;
    private int copyID; // Foreign key to Copies table
    private Date adjustmentDate;
    private String reason; // e.g., Lost, Damaged, Found, Initial Stock, Cycle Count Adjustment
    private int adjustedBy; // UserID of staff
    private int quantityChange; // e.g., -1 for lost, +1 for found
    private String notes;

    // Default constructor
    public InventoryAdjustment() {
    }

    // Getters and Setters
    public int getAdjustmentID() {
        return adjustmentID;
    }

    public void setAdjustmentID(int adjustmentID) {
        this.adjustmentID = adjustmentID;
    }

    public int getCopyID() {
        return copyID;
    }

    public void setCopyID(int copyID) {
        this.copyID = copyID;
    }

    public Date getAdjustmentDate() {
        return adjustmentDate;
    }

    public void setAdjustmentDate(Date adjustmentDate) {
        this.adjustmentDate = adjustmentDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getAdjustedBy() {
        return adjustedBy;
    }

    public void setAdjustedBy(int adjustedBy) {
        this.adjustedBy = adjustedBy;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "InventoryAdjustment{" +
                "adjustmentID=" + adjustmentID +
                ", copyID=" + copyID +
                ", reason='" + reason + '\'' +
                ", quantityChange=" + quantityChange +
                ", adjustmentDate=" + adjustmentDate +
                '}';
    }
}
