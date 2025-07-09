package com.librarysystem.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptNote {
    private int grnID;
    private int poID; // Foreign key to PurchaseOrder
    private int supplierID; // Can be derived from PO or stored for ad-hoc receipts
    private String invoiceNumber;
    private Date invoiceDate;
    private Date receivedDate;
    private int receivedBy; // UserID of staff
    private String notes; // For discrepancies, damages, etc.
    private List<GoodsReceiptNoteItem> items;

    // Constructor
    public GoodsReceiptNote() {
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public int getGrnID() {
        return grnID;
    }

    public void setGrnID(int grnID) {
        this.grnID = grnID;
    }

    public int getPoID() {
        return poID;
    }

    public void setPoID(int poID) {
        this.poID = poID;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public int getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(int receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<GoodsReceiptNoteItem> getItems() {
        return items;
    }

    public void setItems(List<GoodsReceiptNoteItem> items) {
        this.items = items;
    }

    public void addItem(GoodsReceiptNoteItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        item.setGrnID(this.grnID); // Ensure item knows its parent GRN ID
    }


    @Override
    public String toString() {
        return "GoodsReceiptNote{" +
                "grnID=" + grnID +
                ", poID=" + poID +
                ", supplierID=" + supplierID +
                ", receivedDate=" + receivedDate +
                ", itemCount=" + (items != null ? items.size() : 0) +
                '}';
    }
}
