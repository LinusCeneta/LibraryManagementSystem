package model;

public class MembershipType {
    private String typeName;
    private int borrowingLimit;
    private int loanDurationDays;

    // Getters and Setters
    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
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
}
