package com.librarysystem.model;

public class MembershipTier {
    private int membershipTierID;
    private String tierName;
    private int borrowingLimit; // Max number of items that can be borrowed simultaneously
    private int loanDurationDays; // Standard loan period in days
    private int renewalLimit; // Max number of times a loan can be renewed

    public MembershipTier() {
    }

    public MembershipTier(String tierName, int borrowingLimit, int loanDurationDays, int renewalLimit) {
        this.tierName = tierName;
        this.borrowingLimit = borrowingLimit;
        this.loanDurationDays = loanDurationDays;
        this.renewalLimit = renewalLimit;
    }

    // Getters and Setters
    public int getMembershipTierID() {
        return membershipTierID;
    }

    public void setMembershipTierID(int membershipTierID) {
        this.membershipTierID = membershipTierID;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public int getBorrowingLimit() {
        return borrowingLimit;
    }

    public void setBorrowingLimit(int borrowingLimit) {
        this.borrowingLimit = borrowingLimit;
    }

    public int getLoanDurationDays() {
        return loanDurationDays;
    }

    public void setLoanDurationDays(int loanDurationDays) {
        this.loanDurationDays = loanDurationDays;
    }

    public int getRenewalLimit() {
        return renewalLimit;
    }

    public void setRenewalLimit(int renewalLimit) {
        this.renewalLimit = renewalLimit;
    }

    @Override
    public String toString() {
        return "MembershipTier{" +
                "membershipTierID=" + membershipTierID +
                ", tierName='" + tierName + '\'' +
                ", borrowingLimit=" + borrowingLimit +
                ", loanDurationDays=" + loanDurationDays +
                ", renewalLimit=" + renewalLimit +
                '}';
    }
}
