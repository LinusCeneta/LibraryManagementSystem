package model;

public class Member {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String memberId;
    private String status;
    private String membershipType;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getStatus() {
        return status;
    }

    public String getMembershipType() {
        return membershipType;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    @Override
    public String toString() {
        return "Member [name=" + name + ", email=" + email + ", phone=" + phone 
            + ", memberId=" + memberId + ", status=" + status 
            + ", membershipType=" + membershipType + "]";
    }
}