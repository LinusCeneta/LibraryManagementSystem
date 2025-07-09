package dao;

import java.sql.*;
import java.util.*;
import model.BranchOverviewReport;

public class BranchDashboardDAO {
    private Connection conn;

    public BranchDashboardDAO(Connection conn) {
        this.conn = conn;
    }

    public BranchOverviewReport getBranchOverview(int branchId, int lowInventoryThreshold) throws SQLException {
        BranchOverviewReport report = new BranchOverviewReport();

        // Total items, available, checked out, on hold
        String statusSql = "SELECT " +
                           "(SELECT COUNT(*) FROM items WHERE branch_id = ?) AS total_items, " +
                           "(SELECT COUNT(*) FROM items WHERE branch_id = ? AND status = 'Available') AS available_items, " +
                           "(SELECT COUNT(*) FROM items WHERE branch_id = ? AND status = 'Checked Out') AS checked_out_items, " +
                           "(SELECT COUNT(*) FROM items WHERE branch_id = ? AND status = 'On Hold') AS on_hold_items";
        try (PreparedStatement ps = conn.prepareStatement(statusSql)) {
            for (int i = 1; i <= 4; i++) {
                ps.setInt(i, branchId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setTotalItems(rs.getInt("total_items"));
                    report.setAvailableItems(rs.getInt("available_items"));
                    report.setCheckedOutItems(rs.getInt("checked_out_items"));
                    report.setOnHoldItems(rs.getInt("on_hold_items"));
                }
            }
        }

        // Daily checkouts
        String checkoutSql = "SELECT COUNT(*) AS daily_checkouts " +
                             "FROM loans " +
                             "WHERE branch_id = ? AND checkout_date = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(checkoutSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setDailyCheckouts(rs.getInt("daily_checkouts"));
                }
            }
        }

        // Daily returns
        String returnSql = "SELECT COUNT(*) AS daily_returns " +
                           "FROM returns " +
                           "WHERE branch_id = ? AND return_date = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(returnSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setDailyReturns(rs.getInt("daily_returns"));
                }
            }
        }

        // Overdue count
        String overdueSql = "SELECT COUNT(*) AS overdue_count " +
                            "FROM loans " +
                            "WHERE branch_id = ? AND due_date < CURDATE() AND return_date IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(overdueSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setOverdueCount(rs.getInt("overdue_count"));
                }
            }
        }

        // Low inventory alerts
        String alertSql = "SELECT title, COUNT(*) AS copies " +
                          "FROM items " +
                          "WHERE branch_id = ? " +
                          "GROUP BY title " +
                          "HAVING copies < ? " +
                          "ORDER BY copies ASC";
        try (PreparedStatement ps = conn.prepareStatement(alertSql)) {
            ps.setInt(1, branchId);
            ps.setInt(2, lowInventoryThreshold);
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Integer> lowInventoryAlerts = new HashMap<>();
                while (rs.next()) {
                    lowInventoryAlerts.put(rs.getString("title"), rs.getInt("copies"));
                }
                report.setLowInventoryAlerts(lowInventoryAlerts);
            }
        }

        return report;
    }
}
