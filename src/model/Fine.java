package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Fine {
    private int fineId;
    private int memberId;
    private int loanId;
    private BigDecimal amount;
    private Date dateLevied;
    private String status; // "Unpaid", "Paid"

    // Constructors
    public Fine() {}

    public Fine(int fineId, int memberId, int loanId, BigDecimal amount, Date dateLevied, String status) {
        this.fineId = fineId;
        this.memberId = memberId;
        this.loanId = loanId;
        this.amount = amount;
        this.dateLevied = dateLevied;
        this.status = status;
    }

    // Getters and Setters
    public int getFineId() { return fineId; }
    public void setFineId(int fineId) { this.fineId = fineId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Date getDateLevied() { return dateLevied; }
    public void setDateLevied(Date dateLevied) { this.dateLevied = dateLevied; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
