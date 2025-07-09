package model;

import java.util.Date;

public class CirculationReport {
    private Date loanDate;
    private int checkoutCount;

    // Getters and setters
    public Date getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    public int getCheckoutCount() {
        return checkoutCount;
    }

    public void setCheckoutCount(int checkoutCount) {
        this.checkoutCount = checkoutCount;
    }
}
