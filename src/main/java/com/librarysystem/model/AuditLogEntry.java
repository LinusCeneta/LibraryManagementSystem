package com.librarysystem.model;

import java.sql.Timestamp;

public class AuditLogEntry {
    private long logID;
    private Integer userID; // Can be null for system actions or pre-login attempts
    private String usernameAttempted; // For logging username in login attempts, separate from userID
    private Timestamp timestamp;
    private String action; // e.g., LOGIN_SUCCESS, LOGIN_FAILURE, USER_REGISTERED
    private String ipAddress;
    private String details; // CLOB for more extensive information, e.g., JSON

    public AuditLogEntry() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public AuditLogEntry(Integer userID, String action, String ipAddress) {
        this();
        this.userID = userID;
        this.action = action;
        this.ipAddress = ipAddress;
    }

    public AuditLogEntry(Integer userID, String usernameAttempted, String action, String ipAddress, String details) {
        this(userID, action, ipAddress);
        this.usernameAttempted = usernameAttempted;
        this.details = details;
    }


    // Getters and Setters
    public long getLogID() {
        return logID;
    }

    public void setLogID(long logID) {
        this.logID = logID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsernameAttempted() {
        return usernameAttempted;
    }

    public void setUsernameAttempted(String usernameAttempted) {
        this.usernameAttempted = usernameAttempted;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "AuditLogEntry{" +
                "logID=" + logID +
                ", userID=" + userID +
                (usernameAttempted != null ? ", usernameAttempted='" + usernameAttempted + '\'' : "") +
                ", timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", details='" + (details != null && details.length() > 50 ? details.substring(0, 50) + "..." : details) + '\'' +
                '}';
    }
}
