package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import utils.DBConnection;

public class AuditLogDAO {
    public void logAction(int userId, String action, String ipAddress) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO AuditLog (user_id, action, ip_address) VALUES (?, ?, ?)");
            stmt.setInt(1, userId);
            stmt.setString(2, action);
            stmt.setString(3, ipAddress);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
