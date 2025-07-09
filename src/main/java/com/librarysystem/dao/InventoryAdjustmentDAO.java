package com.librarysystem.dao;

import com.librarysystem.model.InventoryAdjustment;
// Assuming Copy model and DBConnectionUtil exist
import com.librarysystem.util.DBConnectionUtil;
// import com.librarysystem.model.Copy;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InventoryAdjustmentDAO {

    // Create Inventory Adjustment
    // This should also trigger an update to the Copy's status or quantity if applicable.
    public InventoryAdjustment createAdjustment(InventoryAdjustment adjustment) throws SQLException {
        String sql = "INSERT INTO InventoryAdjustments (CopyID, AdjustmentDate, Reason, AdjustedBy, QuantityChange, Notes) VALUES (?, ?, ?, ?, ?, ?)";
        // String updateCopySql = "UPDATE Copies SET Status = ?, CurrentStock = CurrentStock + ? WHERE CopyID = ?";
        // Actual update to Copy depends on how inventory is tracked (e.g. status like 'Lost', 'Damaged', or a quantity field)

        Connection conn = null;
        PreparedStatement pstmtAdjustment = null;
        // PreparedStatement pstmtUpdateCopy = null;

        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert Adjustment Record
            pstmtAdjustment = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmtAdjustment.setInt(1, adjustment.getCopyID());
            pstmtAdjustment.setDate(2, adjustment.getAdjustmentDate());
            pstmtAdjustment.setString(3, adjustment.getReason());
            pstmtAdjustment.setInt(4, adjustment.getAdjustedBy());
            pstmtAdjustment.setInt(5, adjustment.getQuantityChange());
            pstmtAdjustment.setString(6, adjustment.getNotes());
            pstmtAdjustment.executeUpdate();

            ResultSet generatedKeys = pstmtAdjustment.getGeneratedKeys();
            if (generatedKeys.next()) {
                adjustment.setAdjustmentID(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating inventory adjustment failed, no ID obtained.");
            }

            // TODO: Update the associated Copy record.
            // This logic is critical and depends on the Copy table structure.
            // For example, if a copy is 'Lost', its status should change.
            // If it's a quantity adjustment for a bulk item (less common for library books), quantity changes.
            // For now, this part is conceptual. A CopyDAO method would be called here.
            /*
            CopyDAO copyDAO = new CopyDAO(); // Assuming CopyDAO
            Copy copy = copyDAO.getCopyById(adjustment.getCopyID());
            if (copy != null) {
                // Example: if reason is "Lost", change status
                if ("Lost".equalsIgnoreCase(adjustment.getReason()) && adjustment.getQuantityChange() < 0) {
                    copy.setStatus("Lost");
                    copyDAO.updateCopyStatus(copy.getCopyID(), "Lost");
                } else if ("Damaged".equalsIgnoreCase(adjustment.getReason()) && adjustment.getQuantityChange() < 0) {
                    copy.setStatus("Damaged");
                    // copy.setCondition("Damaged"); // Potentially update condition too
                    copyDAO.updateCopyStatus(copy.getCopyID(), "Damaged");
                } else if ("Found".equalsIgnoreCase(adjustment.getReason()) && adjustment.getQuantityChange() > 0) {
                    // If found, it might become "Available" or go to a review state
                    copy.setStatus("Available");
                    copyDAO.updateCopyStatus(copy.getCopyID(), "Available");
                }
                // More complex logic for quantity changes if your Copy model supports it
            }
            */

            conn.commit(); // Commit transaction
            return adjustment;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException ex) {
                    // log ex
                }
            }
            throw e;
        } finally {
            if (pstmtAdjustment != null) pstmtAdjustment.close();
            // if (pstmtUpdateCopy != null) pstmtUpdateCopy.close();
            if (conn != null) {
                conn.setAutoCommit(true); // Reset auto-commit
                conn.close();
            }
        }
    }

    public InventoryAdjustment getAdjustmentById(int adjustmentId) throws SQLException {
        String sql = "SELECT * FROM InventoryAdjustments WHERE AdjustmentID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, adjustmentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToAdjustment(rs);
            }
        }
        return null;
    }

    public List<InventoryAdjustment> getAllAdjustments() throws SQLException {
        List<InventoryAdjustment> adjustments = new ArrayList<>();
        String sql = "SELECT * FROM InventoryAdjustments ORDER BY AdjustmentDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                adjustments.add(mapRowToAdjustment(rs));
            }
        }
        return adjustments;
    }

    public List<InventoryAdjustment> getAdjustmentsByCopyId(int copyId) throws SQLException {
        List<InventoryAdjustment> adjustments = new ArrayList<>();
        String sql = "SELECT * FROM InventoryAdjustments WHERE CopyID = ? ORDER BY AdjustmentDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, copyId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                adjustments.add(mapRowToAdjustment(rs));
            }
        }
        return adjustments;
    }

    public List<InventoryAdjustment> getAdjustmentsByReason(String reason) throws SQLException {
        List<InventoryAdjustment> adjustments = new ArrayList<>();
        String sql = "SELECT * FROM InventoryAdjustments WHERE Reason = ? ORDER BY AdjustmentDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reason);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                adjustments.add(mapRowToAdjustment(rs));
            }
        }
        return adjustments;
    }

    // Updates to adjustments are generally not done; instead, a counter-adjustment is made.
    // Deletion is also problematic due to its impact on inventory history.
    // These methods are therefore omitted or would need careful consideration.

    private InventoryAdjustment mapRowToAdjustment(ResultSet rs) throws SQLException {
        InventoryAdjustment adj = new InventoryAdjustment();
        adj.setAdjustmentID(rs.getInt("AdjustmentID"));
        adj.setCopyID(rs.getInt("CopyID"));
        adj.setAdjustmentDate(rs.getDate("AdjustmentDate"));
        adj.setReason(rs.getString("Reason"));
        adj.setAdjustedBy(rs.getInt("AdjustedBy"));
        adj.setQuantityChange(rs.getInt("QuantityChange"));
        adj.setNotes(rs.getString("Notes"));
        return adj;
    }

    // Dummy CopyDAO and Copy classes for compilation.
    // private static class CopyDAO {
    //     public Copy getCopyById(int copyId) { return null; }
    //     public void updateCopyStatus(int copyId, String status) {}
    // }
    // private static class Copy {
    //     private int copyID;
    //     private String status;
    //     public int getCopyID() { return copyID; }
    //     public String getStatus() { return status; }
    //     public void setStatus(String status) { this.status = status; }
    // }
}
