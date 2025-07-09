package model;

public class MemberReport {
    private int memberId;
    private String name;
    private int checkoutCount;

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCheckoutCount() { return checkoutCount; }
    public void setCheckoutCount(int checkoutCount) { this.checkoutCount = checkoutCount; }
}
