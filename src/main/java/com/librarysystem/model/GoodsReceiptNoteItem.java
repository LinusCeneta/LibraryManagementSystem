package com.librarysystem.model;

public class GoodsReceiptNoteItem {
    private int grnItemID;
    private int grnID; // Foreign key to GoodsReceiptNote
    private Integer poLineID; // Optional: Link to the specific line item in the PO
    private Integer bookID; // From POLine or directly if ad-hoc receipt. Should be resolved before creating Copy.
    private int receivedQuantity;
    private int acceptedQuantity; // Quantity accepted after inspection
    private String condition; // e.g., New, Good, Fair, Damaged
    private String notes; // e.g., "1 copy damaged"

    // Default constructor
    public GoodsReceiptNoteItem() {
    }

    // Getters and Setters
    public int getGrnItemID() {
        return grnItemID;
    }

    public void setGrnItemID(int grnItemID) {
        this.grnItemID = grnItemID;
    }

    public int getGrnID() {
        return grnID;
    }

    public void setGrnID(int grnID) {
        this.grnID = grnID;
    }

    public Integer getPoLineID() {
        return poLineID;
    }

    public void setPoLineID(Integer poLineID) {
        this.poLineID = poLineID;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }

    public int getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(int receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public int getAcceptedQuantity() {
        return acceptedQuantity;
    }

    public void setAcceptedQuantity(int acceptedQuantity) {
        this.acceptedQuantity = acceptedQuantity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "GoodsReceiptNoteItem{" +
                "grnItemID=" + grnItemID +
                ", grnID=" + grnID +
                ", bookID=" + bookID +
                ", receivedQuantity=" + receivedQuantity +
                ", acceptedQuantity=" + acceptedQuantity +
                '}';
    }
}
