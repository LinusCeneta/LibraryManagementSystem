package com.librarysystem.dto;

public class TopBorrowerDTO {
    private int memberId;
    private String memberName;
    private long checkoutCount;

    public TopBorrowerDTO(int memberId, String memberName, long checkoutCount) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.checkoutCount = checkoutCount;
    }

    // Getters
    public int getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public long getCheckoutCount() {
        return checkoutCount;
    }

    // Setters if needed
}
