package model;

import java.sql.Date;

public class Loan {
    private int loanId;
    private int memberId;
    private int copyId;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
    private int staffId;
    private String status; // e.g., "Checked Out", "Returned", "Overdue"

    // Constructors
    public Loan() {}

    public Loan(int loanId, int memberId, int copyId, Date issueDate, Date dueDate, Date returnDate, int staffId, String status) {
        this.loanId = loanId;
        this.memberId = memberId;
        this.copyId = copyId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.staffId = staffId;
        this.status = status;
    }

    // Getters and Setters
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public int getCopyId() { return copyId; }
    public void setCopyId(int copyId) { this.copyId = copyId; }

    public Date getIssueDate() { return issueDate; }
    public void setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
