package com.librarysystem.model;

import java.sql.Timestamp;

public class Hold {
    private long holdID;
    private int bookID;    // FK to Books table
    private int memberID;  // FK to Users (or Members table)
    private Timestamp requestDate;
    private String status; // e.g., PENDING, READY_FOR_PICKUP, FULFILLED, CANCELLED, EXPIRED
    private Timestamp expiryDate; // Date when a 'READY_FOR_PICKUP' hold expires
    private Integer fulfilledByCopyID; // FK to Copies, set when a specific copy is assigned
    private Integer pickupLocationID; // FK to a Locations/Branches table (if multi-branch)
    private Integer queuePosition; // Optional: managed by application logic
    private Timestamp notificationSentDate; // When member was notified it's ready

    // Optional: Store related objects
    private Book book;
    private User member;
    private Copy fulfilledByCopy;
    // private Location pickupLocation;

    public Hold() {
        this.requestDate = new Timestamp(System.currentTimeMillis());
        this.status = "PENDING";
    }

    // Getters and Setters
    public long getHoldID() {
        return holdID;
    }

    public void setHoldID(long holdID) {
        this.holdID = holdID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getFulfilledByCopyID() {
        return fulfilledByCopyID;
    }

    public void setFulfilledByCopyID(Integer fulfilledByCopyID) {
        this.fulfilledByCopyID = fulfilledByCopyID;
    }

    public Integer getPickupLocationID() {
        return pickupLocationID;
    }

    public void setPickupLocationID(Integer pickupLocationID) {
        this.pickupLocationID = pickupLocationID;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public Timestamp getNotificationSentDate() {
        return notificationSentDate;
    }

    public void setNotificationSentDate(Timestamp notificationSentDate) {
        this.notificationSentDate = notificationSentDate;
    }


    // Optional related object getters/setters
    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
        if(book != null) this.bookID = book.getBookID(); // Assuming Book model has getBookID()
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
         if(member != null) this.memberID = member.getUserID();
    }

    public Copy getFulfilledByCopy() {
        return fulfilledByCopy;
    }

    public void setFulfilledByCopy(Copy fulfilledByCopy) {
        this.fulfilledByCopy = fulfilledByCopy;
        if(fulfilledByCopy != null) this.fulfilledByCopyID = fulfilledByCopy.getCopyID();
    }

    // public Location getPickupLocation() { return pickupLocation; }
    // public void setPickupLocation(Location pickupLocation) { this.pickupLocation = pickupLocation; }


    @Override
    public String toString() {
        return "Hold{" +
                "holdID=" + holdID +
                ", bookID=" + bookID +
                ", memberID=" + memberID +
                ", requestDate=" + requestDate +
                ", status='" + status + '\'' +
                '}';
    }
}
