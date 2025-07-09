package com.librarysystem.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Fine {
    private long fineID;
    private Long loanID; // Nullable
    private int memberID;
    private BigDecimal fineAmount;
    private BigDecimal amountPaid;
    private Timestamp dateLevied;
    private Timestamp datePaid; // Null if not fully paid
    private String fineStatus; // e.g., UNPAID, PAID, PARTIALLY_PAID, WAIVED
    private String reason;     // e.g., OVERDUE, DAMAGED_ITEM, LOST_ITEM
    private String notes;      // Additional details by staff
    private Integer processedByStaffID; // Staff who processed payment or waiver

    // Optional: Store related objects
    private Loan loan;
    private User member;
    private User staffProcessedBy;

    public Fine() {
        this.dateLevied = new Timestamp(System.currentTimeMillis());
        this.fineStatus = "UNPAID";
        this.amountPaid = BigDecimal.ZERO;
    }

    // Getters and Setters
    public long getFineID() {
        return fineID;
    }

    public void setFineID(long fineID) {
        this.fineID = fineID;
    }

    public Long getLoanID() {
        return loanID;
    }

    public void setLoanID(Long loanID) {
        this.loanID = loanID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Timestamp getDateLevied() {
        return dateLevied;
    }

    public void setDateLevied(Timestamp dateLevied) {
        this.dateLevied = dateLevied;
    }

    public Timestamp getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(Timestamp datePaid) {
        this.datePaid = datePaid;
    }

    public String getFineStatus() {
        return fineStatus;
    }

    public void setFineStatus(String fineStatus) {
        this.fineStatus = fineStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getProcessedByStaffID() {
        return processedByStaffID;
    }

    public void setProcessedByStaffID(Integer processedByStaffID) {
        this.processedByStaffID = processedByStaffID;
    }


    // Calculated property for outstanding amount
    public BigDecimal getOutstandingAmount() {
        if (fineAmount == null) return BigDecimal.ZERO;
        if (amountPaid == null) return fineAmount;
        return fineAmount.subtract(amountPaid);
    }

    // Optional related object getters/setters
    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
        if(loan != null) this.loanID = loan.getLoanID();
    }

    public User getMember() {
        return member;
    }

    public void setMember(User member) {
        this.member = member;
        if(member != null) this.memberID = member.getUserID();
    }

    public User getStaffProcessedBy() {
        return staffProcessedBy;
    }

    public void setStaffProcessedBy(User staffProcessedBy) {
        this.staffProcessedBy = staffProcessedBy;
        if(staffProcessedBy != null) this.processedByStaffID = staffProcessedBy.getUserID();
    }

    @Override
    public String toString() {
        return "Fine{" +
                "fineID=" + fineID +
                ", memberID=" + memberID +
                ", fineAmount=" + fineAmount +
                ", amountPaid=" + amountPaid +
                ", status='" + fineStatus + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
