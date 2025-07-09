package com.librarysystem.model;

import java.sql.Date;

public class Copy {
    private int copyID;
    private int bookID; // Foreign Key to Book table
    private String copyBarcode; // Unique barcode for this physical copy
    private Date acquisitionDate;
    private double cost;
    private String condition; // e.g., New, Good, Fair, Damaged, Lost
    private String status;    // e.g., Available, Checked Out, On Hold, Lost, Under Repair, Withdrawn
    private String location;  // e.g., Shelf A1, Branch B

    // Default constructor
    public Copy() {
    }

    // Getters and Setters
    public int getCopyID() {
        return copyID;
    }

    public void setCopyID(int copyID) {
        this.copyID = copyID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getCopyBarcode() {
        return copyBarcode;
    }

    public void setCopyBarcode(String copyBarcode) {
        this.copyBarcode = copyBarcode;
    }

    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(Date acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Copy{" +
                "copyID=" + copyID +
                ", bookID=" + bookID +
                ", copyBarcode='" + copyBarcode + '\'' +
                ", status='" + status + '\'' +
                ", condition='" + condition + '\'' +
                '}';
    }

    // To hold Book details when joining in queries
    private Book book;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

}
