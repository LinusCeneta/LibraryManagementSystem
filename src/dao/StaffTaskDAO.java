package dao;

import model.StaffTaskReport;
import java.sql.*;
import java.util.*;

public class StaffTaskDAO {
    private Connection conn;

    public StaffTaskDAO(Connection conn) {
        this.conn = conn;
    }

    public StaffTaskReport getStaffTasks(int branchId) throws SQLException {
        StaffTaskReport report = new StaffTaskReport();

        // 1️⃣ Pending acquisition requests
        String acquisitionSql = "SELECT COUNT(*) AS count " +
                                "FROM acquisition_requests " +
                                "WHERE branch_id = ? AND status = 'Pending'";
        try (PreparedStatement ps = conn.prepareStatement(acquisitionSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setPendingAcquisitions(rs.getInt("count"));
                }
            }
        }

        // 2️⃣ Books to catalog
        String catalogSql = "SELECT COUNT(*) AS count " +
                            "FROM items " +
                            "WHERE branch_id = ? AND status = 'To Catalog'";
        try (PreparedStatement ps = conn.prepareStatement(catalogSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setBooksToCatalog(rs.getInt("count"));
                }
            }
        }

        // 3️⃣ Items awaiting check-in (e.g., returned via book drop)
        String checkinSql = "SELECT COUNT(*) AS count " +
                            "FROM items " +
                            "WHERE branch_id = ? AND status = 'Awaiting Check-In'";
        try (PreparedStatement ps = conn.prepareStatement(checkinSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setItemsAwaitingCheckIn(rs.getInt("count"));
                }
            }
        }

        // 4️⃣ Holds to process (items available for pickup)
        String holdsSql = "SELECT COUNT(*) AS count " +
                          "FROM holds " +
                          "WHERE branch_id = ? AND status = 'Available'";
        try (PreparedStatement ps = conn.prepareStatement(holdsSql)) {
            ps.setInt(1, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    report.setHoldsToProcess(rs.getInt("count"));
                }
            }
        }

        return report;
    }
}
