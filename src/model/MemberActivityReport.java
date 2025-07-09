package model;

public class MemberActivityReport {
    private String memberId;
    private String memberName;
    private double totalFines;
    private double averageFine;

    public String getMemberId() {
        return memberId;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public double getTotalFines() {
        return totalFines;
    }
    public void setTotalFines(double totalFines) {
        this.totalFines = totalFines;
    }

    public double getAverageFine() {
        return averageFine;
    }
    public void setAverageFine(double averageFine) {
        this.averageFine = averageFine;
    }
}
