package com.librarysystem.dao;

import com.librarysystem.model.AuditLogEntry;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    public AuditLogEntry createLogEntry(AuditLogEntry logEntry) throws SQLException {
        String sql = "INSERT INTO AuditLog (UserID, UsernameAttempted, Timestamp, Action, IPAddress, Details) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (logEntry.getUserID() != null) {
                pstmt.setInt(1, logEntry.getUserID());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, logEntry.getUsernameAttempted());
            pstmt.setTimestamp(3, logEntry.getTimestamp() != null ? logEntry.getTimestamp() : new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, logEntry.getAction());
            pstmt.setString(5, logEntry.getIpAddress());
            pstmt.setString(6, logEntry.getDetails()); // For CLOB, setString should work for Derby if data is not excessively large. For other DBs, setClob might be needed.

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    logEntry.setLogID(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating audit log entry failed, no ID obtained.");
                }
            }
            return logEntry;
        }
    }

    public List<AuditLogEntry> findLogsByUserId(int userId) throws SQLException {
        List<AuditLogEntry> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE UserID = ? ORDER BY Timestamp DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToAuditLogEntry(rs));
                }
            }
        }
        return logs;
    }

    public List<AuditLogEntry> findLogsByAction(String action) throws SQLException {
        List<AuditLogEntry> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE Action = ? ORDER BY Timestamp DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, action);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToAuditLogEntry(rs));
                }
            }
        }
        return logs;
    }

    public List<AuditLogEntry> findLogsByDateRange(Timestamp startTime, Timestamp endTime) throws SQLException {
        List<AuditLogEntry> logs = new ArrayList<>();
        String sql = "SELECT * FROM AuditLog WHERE Timestamp >= ? AND Timestamp <= ? ORDER BY Timestamp DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, startTime);
            pstmt.setTimestamp(2, endTime);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToAuditLogEntry(rs));
                }
            }
        }
        return logs;
    }

    public List<AuditLogEntry> findAllLogs(int limit, int offset) throws SQLException {
        List<AuditLogEntry> logs = new ArrayList<>();
        // Derby specific syntax for LIMIT and OFFSET. Adjust for other databases.
        // For Derby 10.5+ : "SELECT * FROM AuditLog ORDER BY Timestamp DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"
        String sql = "SELECT * FROM AuditLog ORDER BY Timestamp DESC"; // Basic query, add pagination if needed
        if (limit > 0) {
             // Derby specific syntax for LIMIT and OFFSET
             // Ensure your Derby version supports this or use a different pagination method
            sql = "SELECT * FROM AuditLog ORDER BY Timestamp DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        }


        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (limit > 0) {
                pstmt.setInt(1, offset);
                pstmt.setInt(2, limit);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRowToAuditLogEntry(rs));
                }
            }
        }
        return logs;
    }

    public int countAllLogs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM AuditLog";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }


    private AuditLogEntry mapRowToAuditLogEntry(ResultSet rs) throws SQLException {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setLogID(rs.getLong("LogID"));
        entry.setUserID(rs.getObject("UserID", Integer.class)); // Handles NULL UserID
        entry.setUsernameAttempted(rs.getString("UsernameAttempted"));
        entry.setTimestamp(rs.getTimestamp("Timestamp"));
        entry.setAction(rs.getString("Action"));
        entry.setIpAddress(rs.getString("IPAddress"));
        entry.setDetails(rs.getString("Details")); // For CLOB, getString should work for Derby
        return entry;
    }
}
