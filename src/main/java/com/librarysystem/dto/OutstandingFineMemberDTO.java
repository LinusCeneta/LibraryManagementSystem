package com.librarysystem.dto;

import java.math.BigDecimal;

public class OutstandingFineMemberDTO {
    private int memberId;
    private String memberName;
    private BigDecimal totalOutstandingFineAmount;
    private long numberOfUnpaidFines;

    public OutstandingFineMemberDTO(int memberId, String memberName, BigDecimal totalOutstandingFineAmount, long numberOfUnpaidFines) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.totalOutstandingFineAmount = totalOutstandingFineAmount != null ? totalOutstandingFineAmount : BigDecimal.ZERO;
        this.numberOfUnpaidFines = numberOfUnpaidFines;
    }

    // Getters
    public int getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public BigDecimal getTotalOutstandingFineAmount() {
        return totalOutstandingFineAmount;
    }

    public long getNumberOfUnpaidFines() {
        return numberOfUnpaidFines;
    }

    // Setters (optional)
}
