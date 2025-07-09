package com.librarysystem.model;

import java.sql.Timestamp;

public class PasswordResetToken {
    private int tokenID;
    private int userID;
    private String token;
    private Timestamp expiryDate;
    private boolean isUsed;

    public PasswordResetToken() {
    }

    public PasswordResetToken(int userID, String token, Timestamp expiryDate) {
        this.userID = userID;
        this.token = token;
        this.expiryDate = expiryDate;
        this.isUsed = false;
    }

    // Getters and Setters
    public int getTokenID() {
        return tokenID;
    }

    public void setTokenID(int tokenID) {
        this.tokenID = tokenID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public boolean isExpired() {
        return expiryDate.before(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "tokenID=" + tokenID +
                ", userID=" + userID +
                ", token='" + token.substring(0, Math.min(token.length(), 10)) + "...'" + // Avoid logging full token
                ", expiryDate=" + expiryDate +
                ", isUsed=" + isUsed +
                '}';
    }
}
