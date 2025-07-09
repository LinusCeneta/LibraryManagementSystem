package dao;

import model.UserActivity;
import utils.DBConnection;

import java.sql.*;
import java.util.*;

public class UserActivityMonitoringDAO {
    private Connection conn;

    public UserActivityMonitoringDAO(Connection conn) {
        this.conn = conn;
    }

    public List<UserActivity> getActiveSessions() throws SQLException {
        List<UserActivity> activities = new ArrayList<>();
        String sql = "SELECT u.username, s.session_id, s.login_time, s.ip_address " +
                     "FROM UserSessions s " +
                     "JOIN Users u ON s.user_id = u.id " +  // Changed to u.id
                     "WHERE s.expired = false";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserActivity activity = new UserActivity();
                activity.setUsername(rs.getString("username"));
                activity.setSessionId(rs.getString("session_id"));
                activity.setIpAddress(rs.getString("ip_address"));
                activity.setAction("Active Session");
                activity.setTimestamp(rs.getTimestamp("login_time"));
                activities.add(activity);
            }
        }
        return activities;
    }

    public List<UserActivity> getRecentActions() throws SQLException {
        List<UserActivity> activities = new ArrayList<>();
        String sql = "SELECT u.username, a.action, a.timestamp, a.ip_address " +
                     "FROM UserActivities a " +
                     "JOIN Users u ON a.user_id = u.id " +  // Changed to u.id
                     "WHERE a.timestamp >= CURRENT_TIMESTAMP - 7 DAY " +
                     "ORDER BY a.timestamp DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserActivity activity = new UserActivity();
                activity.setUsername(rs.getString("username"));
                activity.setAction(rs.getString("action"));
                activity.setIpAddress(rs.getString("ip_address"));
                activity.setTimestamp(rs.getTimestamp("timestamp"));
                activities.add(activity);
            }
        }
        return activities;
    }

    public List<UserActivity> getSuspiciousActivities() throws SQLException {
        List<UserActivity> activities = new ArrayList<>();
        String sql = "SELECT u.username, COUNT(*) AS failed_attempts, MAX(a.timestamp) AS last_attempt " +
                     "FROM UserActivities a " +
                     "JOIN Users u ON a.user_id = u.id " +  // Changed to u.id
                     "WHERE a.action LIKE 'FAILED_LOGIN%' " +
                     "AND a.timestamp >= CURRENT_TIMESTAMP - 24 HOUR " +
                     "GROUP BY u.username " +
                     "HAVING COUNT(*) >= 5";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserActivity activity = new UserActivity();
                activity.setUsername(rs.getString("username"));
                activity.setAction("Suspicious Activity: " + rs.getInt("failed_attempts") + " failed logins");
                activity.setTimestamp(rs.getTimestamp("last_attempt"));
                activities.add(activity);
            }
        }
        return activities;
    }

    public boolean logActivity(UserActivity activity) throws SQLException {
        String sql = "INSERT INTO UserActivities (user_id, username, session_id, action, timestamp, ip_address, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, activity.getUserId());  // Can be null
            stmt.setString(2, activity.getUsername());
            stmt.setString(3, activity.getSessionId());
            stmt.setString(4, activity.getAction());
            stmt.setTimestamp(5, activity.getTimestamp());
            stmt.setString(6, activity.getIpAddress());
            stmt.setString(7, activity.getStatus());
            
            return stmt.executeUpdate() > 0;
        }
    }
}