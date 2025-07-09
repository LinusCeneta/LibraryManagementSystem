package dao;

import model.AlertNotification;
import java.sql.*;
import java.util.*;

public class AlertsNotificationsDAO {
    private Connection conn;

    public AlertsNotificationsDAO(Connection conn) {
        this.conn = conn;
    }

    public List<AlertNotification> getAlerts(int branchId) throws SQLException {
        List<AlertNotification> alerts = new ArrayList<>();

        String overdueSql = "SELECT i.item_id, i.title, m.name, i.due_date " +
                            "FROM items i " +
                            "JOIN members m ON i.member_id = m.member_id " +
                            "WHERE i.branch_id = ? AND i.status = 'Checked Out' " +
                            "AND i.due_date BETWEEN CURRENT_DATE - INTERVAL '3' DAY AND CURRENT_DATE";
        try (PreparedStatement ps = conn.prepareStatement(overdueSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AlertNotification alert = new AlertNotification();
                    alert.setType("Overdue Approaching");
                    alert.setItemId(rs.getInt("item_id"));
                    alert.setTitle(rs.getString("title"));
                    alert.setMemberName(rs.getString("name"));
                    alert.setDate(rs.getDate("due_date"));
                    alerts.add(alert);
                }
            }
        }

        String holdsSql = "SELECT h.hold_id, i.title, m.name, h.pickup_expiry_date " +
                          "FROM holds h " +
                          "JOIN items i ON h.item_id = i.item_id " +
                          "JOIN members m ON h.member_id = m.member_id " +
                          "WHERE h.branch_id = ? AND h.status = 'Available' " +
                          "AND h.pickup_expiry_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '2' DAY";
        try (PreparedStatement ps = conn.prepareStatement(holdsSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AlertNotification alert = new AlertNotification();
                    alert.setType("Hold Expiring Soon");
                    alert.setItemId(rs.getInt("hold_id"));
                    alert.setTitle(rs.getString("title"));
                    alert.setMemberName(rs.getString("name"));
                    alert.setDate(rs.getDate("pickup_expiry_date"));
                    alerts.add(alert);
                }
            }
        }

        // 3️⃣ Membership expiration alerts (e.g., due in next 30 days)
        String membershipSql = "SELECT m.member_id, m.name, m.membership_expiry_date " +
                               "FROM members m " +
                               "WHERE m.branch_id = ? AND m.membership_expiry_date BETWEEN CURRENT_DATE AND CURRENT_DATE + INTERVAL '30' DAY";
        try (PreparedStatement ps = conn.prepareStatement(membershipSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AlertNotification alert = new AlertNotification();
                    alert.setType("Membership Expiring Soon");
                    alert.setItemId(rs.getInt("member_id"));
                    alert.setTitle("Membership Expiry");
                    alert.setMemberName(rs.getString("name"));
                    alert.setDate(rs.getDate("membership_expiry_date"));
                    alerts.add(alert);
                }
            }
        }

        return alerts;
    }
}
