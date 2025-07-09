package com.librarysystem.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BookCollection {
    private int collectionID;
    private String collectionName;
    private String description;
    private int createdByStaffID; // FK to Users table
    private Timestamp dateCreated;
    private boolean isPublic;

    // Optional: Store related objects
    private User createdByStaff;
    private List<Book> books; // Books in this collection

    public BookCollection() {
        this.books = new ArrayList<>();
        this.dateCreated = new Timestamp(System.currentTimeMillis());
        this.isPublic = true;
    }

    // Getters and Setters
    public int getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(int collectionID) {
        this.collectionID = collectionID;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreatedByStaffID() {
        return createdByStaffID;
    }

    public void setCreatedByStaffID(int createdByStaffID) {
        this.createdByStaffID = createdByStaffID;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public User getCreatedByStaff() {
        return createdByStaff;
    }

    public void setCreatedByStaff(User createdByStaff) {
        this.createdByStaff = createdByStaff;
        if (createdByStaff != null) this.createdByStaffID = createdByStaff.getUserID();
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public void addBook(Book book) {
        if (this.books == null) {
            this.books = new ArrayList<>();
        }
        this.books.add(book);
    }


    @Override
    public String toString() {
        return "BookCollection{" +
                "collectionID=" + collectionID +
                ", collectionName='" + collectionName + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }
}
