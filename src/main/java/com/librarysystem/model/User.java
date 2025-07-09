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
        this.role = role;
    }

    // Add other user fields and methods as needed
}
