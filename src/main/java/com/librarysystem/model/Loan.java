package com.librarysystem.model;

import java.sql.Timestamp;

public class Loan {
    private long loanID;
    private int memberID; // FK to Users (or Members table)
    private int copyID;   // FK to Copies
    private int staffID;  // FK to Users (staff who processed)
    private Timestamp issueDate;
    private Timestamp dueDate;
    private Timestamp returnDate; // Null if item is still on loan
    private String loanStatus;    // e.g., ACTIVE, RETURNED, OVERDUE, LOST_ITEM
    private int renewalsDone;

    // Optional: Store related objects for convenience, though DAOs usually populate IDs
    private User member;
    private Copy copy;
    private User staff;

    public Loan() {
        this.issueDate = new Timestamp(System.currentTimeMillis());
        this.loanStatus = "ACTIVE"; // Default status for new loans
        this.renewalsDone = 0;
    }

    // Getters and Setters
    public long getLoanID() {
        return loanID;
    }

    public void setLoanID(long loanID) {
        this.loanID = loanID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public int getCopyID() {
        return copyID;
    }

    public void setCopyID(int copyID) {
        this.copyID = copyID;
    }

    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public Timestamp getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Timestamp issueDate) {
        this.issueDate = issueDate;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }

    public int getRenewalsDone() {
        return renewalsDone;
    }

    public void setRenewalsDone(int renewalsDone) {
        this.renewalsDone = renewalsDone;
    }


    // Optional related object getters/setters
    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
        if (member != null) this.memberID = member.getUserID();
    }

    public Copy getCopy() {
        return copy;
    }

    public void setCopy(Copy copy) {
        this.copy = copy;
         if (copy != null) this.copyID = copy.getCopyID();
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
        if (staff != null) this.staffID = staff.getUserID();
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanID=" + loanID +
                ", memberID=" + memberID +
                ", copyID=" + copyID +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", loanStatus='" + loanStatus + '\'' +
                ", renewalsDone=" + renewalsDone +
                '}';
    }
}
