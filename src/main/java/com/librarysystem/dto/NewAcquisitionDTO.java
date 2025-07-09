package com.librarysystem.dto;

import java.math.BigDecimal;
import java.sql.Date; // Using sql.Date for acquisitionDate as it's often just a date

public class NewAcquisitionDTO {
    private int copyId;
    private String bookTitle;
    private String isbn;
    private String copyBarcode;
    private Date acquisitionDate;
    private BigDecimal cost;
    private String categoryName; // Optional, if reporting by category
    private String supplierName; // Optional, from PurchaseOrder if available

    public NewAcquisitionDTO(int copyId, String bookTitle, String isbn, String copyBarcode, Date acquisitionDate, BigDecimal cost, String categoryName, String supplierName) {
        this.copyId = copyId;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.copyBarcode = copyBarcode;
        this.acquisitionDate = acquisitionDate;
        this.cost = cost;
        this.categoryName = categoryName;
        this.supplierName = supplierName;
    }

    public NewAcquisitionDTO(int copyId, String bookTitle, String isbn, String copyBarcode, Date acquisitionDate, BigDecimal cost) {
        this(copyId, bookTitle, isbn, copyBarcode, acquisitionDate, cost, null, null);
    }


    // Getters
    public int getCopyId() { return copyId; }
    public String getBookTitle() { return bookTitle; }
    public String getIsbn() { return isbn; }
    public String getCopyBarcode() { return copyBarcode; }
    public Date getAcquisitionDate() { return acquisitionDate; }
    public BigDecimal getCost() { return cost; }
    public String getCategoryName() { return categoryName; }
    public String getSupplierName() { return supplierName; }

    // Setters (optional)
    public void setCopyId(int copyId) { this.copyId = copyId; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setCopyBarcode(String copyBarcode) { this.copyBarcode = copyBarcode; }
    public void setAcquisitionDate(Date acquisitionDate) { this.acquisitionDate = acquisitionDate; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}
