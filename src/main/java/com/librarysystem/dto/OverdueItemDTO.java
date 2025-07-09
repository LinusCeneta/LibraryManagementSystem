package com.librarysystem.dto;

import com.librarysystem.model.Book;
import com.librarysystem.model.Copy;
import com.librarysystem.model.Loan;
import com.librarysystem.model.User; // Assuming User represents Member

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class OverdueItemDTO {
    private long loanId;
    private String memberName;
    private int memberId;
    private String bookTitle;
    private String copyBarcode;
    private int copyId;
    private Timestamp issueDate;
    private Timestamp dueDate;
    private long daysOverdue;
    private String branchName; // Optional, if available

    public OverdueItemDTO(Loan loan, User member, Copy copy, Book book, String branchName) {
        this.loanId = loan.getLoanID();
        this.memberName = (member != null) ? member.getFirstName() + " " + member.getLastName() : "N/A";
        this.memberId = loan.getMemberID();
        this.bookTitle = (book != null) ? book.getTitle() : "N/A";
        this.copyBarcode = (copy != null) ? copy.getCopyBarcode() : "N/A";
        this.copyId = loan.getCopyID();
        this.issueDate = loan.getIssueDate();
        this.dueDate = loan.getDueDate();
        this.branchName = branchName != null ? branchName : "N/A";

        if (loan.getDueDate() != null) {
            long diffInMillis = System.currentTimeMillis() - loan.getDueDate().getTime();
            this.daysOverdue = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            if (this.daysOverdue < 0) this.daysOverdue = 0; // Not yet overdue if diff is negative
        } else {
            this.daysOverdue = 0; // Should not happen for an overdue item
        }
    }

    public OverdueItemDTO(long loanId, String memberName, int memberId, String bookTitle, String copyBarcode, int copyId, Timestamp issueDate, Timestamp dueDate, String branchName) {
        this.loanId = loanId;
        this.memberName = memberName;
        this.memberId = memberId;
        this.bookTitle = bookTitle;
        this.copyBarcode = copyBarcode;
        this.copyId = copyId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.branchName = branchName != null ? branchName : "N/A";

        if (dueDate != null) {
            long diffInMillis = System.currentTimeMillis() - dueDate.getTime();
            this.daysOverdue = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
             if (this.daysOverdue < 0) this.daysOverdue = 0;
        } else {
            this.daysOverdue = 0;
        }
    }


    // Getters
    public long getLoanId() { return loanId; }
    public String getMemberName() { return memberName; }
    public int getMemberId() { return memberId; }
    public String getBookTitle() { return bookTitle; }
    public String getCopyBarcode() { return copyBarcode; }
    public int getCopyId() { return copyId; }
    public Timestamp getIssueDate() { return issueDate; }
    public Timestamp getDueDate() { return dueDate; }
    public long getDaysOverdue() { return daysOverdue; }
    public String getBranchName() { return branchName; }
}
