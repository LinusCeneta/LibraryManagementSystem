package model;

import java.sql.Timestamp;

public class UserActivity {
    // Fields
    private Integer userId;       // Can be null for anonymous activities
    private String username;
    private String sessionId;
    private String action;
    private Timestamp timestamp;
    private String ipAddress;
    private String userAgent;
    private String status;       // SUCCESS, FAILURE, WARNING
    private String actionDetails;

    // Constructors
    public UserActivity() {}

    public UserActivity(String username, String action, Timestamp timestamp, String status) {
        this.username = username;
        this.action = action;
        this.timestamp = timestamp;
        this.setStatus(status); // Use setter for validation
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getStatus() {
        return status != null ? status : "UNKNOWN";
    }

    public void setStatus(String status) {
        if (status == null || (!status.equals("SUCCESS") && !status.equals("FAILURE") && !status.equals("WARNING"))) {
            throw new IllegalArgumentException("Status must be SUCCESS, FAILURE, or WARNING");
        }
        this.status = status;
    }

    public String getActionDetails() {
        return actionDetails;
    }

    public void setActionDetails(String actionDetails) {
        this.actionDetails = actionDetails;
    }

    // Utility Methods
    public boolean isSuccessful() {
        return "SUCCESS".equals(status);
    }

    public boolean isFailed() {
        return "FAILURE".equals(status);
    }

    public boolean isWarning() {
        return "WARNING".equals(status);
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", action='" + action + '\'' +
                ", timestamp=" + timestamp +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", status='" + status + '\'' +
                ", actionDetails='" + actionDetails + '\'' +
                '}';
    }

    // Builder Pattern (Optional)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final UserActivity activity = new UserActivity();

        public Builder username(String username) {
            activity.setUsername(username);
            return this;
        }

        public Builder action(String action) {
            activity.setAction(action);
            return this;
        }

        public Builder timestamp(Timestamp timestamp) {
            activity.setTimestamp(timestamp);
            return this;
        }

        public Builder status(String status) {
            activity.setStatus(status);
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            activity.setIpAddress(ipAddress);
            return this;
        }

        public UserActivity build() {
            return activity;
        }
    }
}