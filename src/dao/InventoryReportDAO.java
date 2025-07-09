package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.InventoryReport;

public class InventoryReportDAO {
    private Connection connection;

    public InventoryReportDAO(Connection connection) {
        this.connection = connection;
    }

    public List<InventoryReport> getNewAcquisitions(Date startDate, Date endDate, String category) throws SQLException {
        List<InventoryReport> reports = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT DATE(acquisition_date) AS date_added, COUNT(*) AS item_count, SUM(cost) AS total_cost " +
            "FROM items " +
            "WHERE acquisition_date BETWEEN ? AND ? "
        );
        if (category != null && !category.isEmpty()) {
            sql.append("AND category = ? ");
        }
        sql.append("GROUP BY DATE(acquisition_date) ORDER BY date_added DESC");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            if (category != null && !category.isEmpty()) {
                stmt.setString(3, category);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InventoryReport report = new InventoryReport();
                    report.setDateAdded(rs.getDate("date_added"));
                    report.setItemCount(rs.getInt("item_count"));
                    report.setTotalCost(rs.getDouble("total_cost"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * Get total inventory valuation grouped by category, branch, and condition
     */
    public List<InventoryReport> getInventoryValuation(String branch, String condition) throws SQLException {
        List<InventoryReport> reports = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT category, branch, condition, COUNT(*) AS item_count, SUM(cost) AS total_cost " +
            "FROM items WHERE 1=1 "
        );
        if (branch != null && !branch.isEmpty()) {
            sql.append("AND branch = ? ");
        }
        if (condition != null && !condition.isEmpty()) {
            sql.append("AND condition = ? ");
        }
        sql.append("GROUP BY category, branch, condition ORDER BY category, branch, condition");

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (branch != null && !branch.isEmpty()) {
                stmt.setString(paramIndex++, branch);
            }
            if (condition != null && !condition.isEmpty()) {
                stmt.setString(paramIndex++, condition);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InventoryReport report = new InventoryReport();
                    // Here we can encode category, branch, condition into dateAdded or other fields if needed
                    // Alternatively extend InventoryReport model to hold these attributes (better)
                    // For now, using dateAdded as null and use itemCount and totalCost only
                    report.setDateAdded(null); // no specific date for valuation summary
                    report.setItemCount(rs.getInt("item_count"));
                    report.setTotalCost(rs.getDouble("total_cost"));
                    // You may want to add category, branch, condition fields to InventoryReport class for full info
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * Get percentage of items older than specified years
     */
    public double getCollectionAgeAnalysis(int olderThanYears) throws SQLException {
        // Calculate date X years ago from today
        LocalDate cutoffDate = LocalDate.now().minusYears(olderThanYears);
        Date cutoff = Date.valueOf(cutoffDate);

        String totalSql = "SELECT COUNT(*) AS total_count FROM items";
        String olderSql = "SELECT COUNT(*) AS older_count FROM items WHERE acquisition_date < ?";

        int totalCount = 0;
        int olderCount = 0;

        try (Statement totalStmt = connection.createStatement();
             ResultSet totalRs = totalStmt.executeQuery(totalSql)) {
            if (totalRs.next()) {
                totalCount = totalRs.getInt("total_count");
            }
        }

        try (PreparedStatement olderStmt = connection.prepareStatement(olderSql)) {
            olderStmt.setDate(1, cutoff);
            try (ResultSet olderRs = olderStmt.executeQuery()) {
                if (olderRs.next()) {
                    olderCount = olderRs.getInt("older_count");
                }
            }
        }

        if (totalCount == 0) return 0.0;

        return (olderCount * 100.0) / totalCount;
    }
}
