package com.librarysystem.model;

import java.math.BigDecimal;

public class PurchaseOrderLine {
    private int poLineID;
    private int poID; // Foreign key to PurchaseOrder
    private Integer bookID; // Foreign key to Book (can be null if new book not yet in catalog)
    private String requestedBookTitle; // Title if BookID is null
    private int quantity;
    private BigDecimal unitPrice;
    // private BigDecimal lineTotal; // Calculated: quantity * unitPrice

    // Default constructor
    public PurchaseOrderLine() {
    }

    // Getters and Setters
    public int getPoLineID() {
        return poLineID;
    }

    public void setPoLineID(int poLineID) {
        this.poLineID = poLineID;
    }

    public int getPoID() {
        return poID;
    }

    public void setPoID(int poID) {
        this.poID = poID;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }

    public String getRequestedBookTitle() {
        return requestedBookTitle;
    }

    public void setRequestedBookTitle(String requestedBookTitle) {
        this.requestedBookTitle = requestedBookTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineTotal() {
        if (unitPrice == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    // No setter for lineTotal as it's derived

    @Override
    public String toString() {
        return "PurchaseOrderLine{" +
                "poLineID=" + poLineID +
                ", poID=" + poID +
                ", bookID=" + bookID +
                ", requestedBookTitle='" + requestedBookTitle + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", lineTotal=" + getLineTotal() +
                '}';
    }
}
