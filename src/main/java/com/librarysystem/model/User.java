// Placeholder User.java model class
package com.librarysystem.model;

public class User {
    private int userID;
    private String username;
    private String role; // e.g., ROLE_STAFF, ROLE_MEMBER, ROLE_ADMIN

    public User(int userID, String username, String role) {
        this.userID = userID;
        this.username = username;
        this.role = role;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        // This setter might be problematic if Role object is primary way to manage role
        // Prefer setRole(Role role)
        this.role = role; // Keep for now if UserServlet uses it, but ideally refactor
    }

    // Fields from schema for circulation
    private Integer membershipTierID;
    private MembershipTier membershipTier; // Object representation
    private String membershipStatus; // Active, Suspended, Expired
    private java.math.BigDecimal currentFineBalance;


    public Integer getMembershipTierID() {
        return membershipTierID;
    }

    public void setMembershipTierID(Integer membershipTierID) {
        this.membershipTierID = membershipTierID;
    }

    public MembershipTier getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(MembershipTier membershipTier) {
        this.membershipTier = membershipTier;
        if (membershipTier != null) {
            this.membershipTierID = membershipTier.getMembershipTierID();
        }
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public java.math.BigDecimal getCurrentFineBalance() {
        return currentFineBalance;
    }

    public void setCurrentFineBalance(java.math.BigDecimal currentFineBalance) {
        this.currentFineBalance = currentFineBalance;
    }

    // Add other user fields and methods as needed
}
