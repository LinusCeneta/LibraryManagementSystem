package model;

import java.sql.Date;

public class GoodsReceiptNote {
    private int grnId;
    private int poId;
    private Date receiptDate;
    private String invoiceNumber;
    private Date invoiceDate;
    private String notes;

    public GoodsReceiptNote() {}

    public GoodsReceiptNote(int grnId, int poId, Date receiptDate, String invoiceNumber, Date invoiceDate, String notes) {
        this.grnId = grnId;
        this.poId = poId;
        this.receiptDate = receiptDate;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.notes = notes;
    }

    public int getGrnId() { return grnId; }
    public void setGrnId(int grnId) { this.grnId = grnId; }

    public int getPoId() { return poId; }
    public void setPoId(int poId) { this.poId = poId; }

    public Date getReceiptDate() { return receiptDate; }
    public void setReceiptDate(Date receiptDate) { this.receiptDate = receiptDate; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
