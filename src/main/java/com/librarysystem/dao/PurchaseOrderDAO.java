package com.librarysystem.dao;

import com.librarysystem.model.PurchaseOrder;
import com.librarysystem.model.PurchaseOrderLine;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDAO {

    // Create Purchase Order (Header and Lines)
    public PurchaseOrder createPurchaseOrder(PurchaseOrder po) throws SQLException {
        String poSql = "INSERT INTO PurchaseOrders (PONumber, SupplierID, OrderDate, ExpectedDeliveryDate, Status, TotalAmount, CreatedBy) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String lineSql = "INSERT INTO PurchaseOrderLines (PO_ID, BookID, RequestedBookTitle, Quantity, UnitPrice) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmtPo = null;
        PreparedStatement pstmtLine = null;

        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert PO Header
            pstmtPo = conn.prepareStatement(poSql, Statement.RETURN_GENERATED_KEYS);
            pstmtPo.setString(1, po.getPoNumber());
            pstmtPo.setInt(2, po.getSupplierID());
            pstmtPo.setDate(3, po.getOrderDate());
            pstmtPo.setDate(4, po.getExpectedDeliveryDate());
            pstmtPo.setString(5, po.getStatus());
            pstmtPo.setBigDecimal(6, po.getTotalAmount());
            pstmtPo.setInt(7, po.getCreatedBy());
            pstmtPo.executeUpdate();

            ResultSet generatedKeys = pstmtPo.getGeneratedKeys();
            if (generatedKeys.next()) {
                po.setPoID(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating purchase order failed, no PO_ID obtained.");
            }

            // Insert PO Lines
            if (po.getOrderLines() != null && !po.getOrderLines().isEmpty()) {
                pstmtLine = conn.prepareStatement(lineSql, Statement.RETURN_GENERATED_KEYS);
                for (PurchaseOrderLine line : po.getOrderLines()) {
                    line.setPoID(po.getPoID()); // Ensure PO_ID is set from the header
                    pstmtLine.setInt(1, line.getPoID());
                    if (line.getBookID() != null) {
                        pstmtLine.setInt(2, line.getBookID());
                    } else {
                        pstmtLine.setNull(2, Types.INTEGER);
                    }
                    pstmtLine.setString(3, line.getRequestedBookTitle());
                    pstmtLine.setInt(4, line.getQuantity());
                    pstmtLine.setBigDecimal(5, line.getUnitPrice());
                    pstmtLine.addBatch();
                }
                pstmtLine.executeBatch();

                // Get generated keys for lines if needed (e.g., to set POLineID on objects)
                ResultSet lineKeys = pstmtLine.getGeneratedKeys();
                int i = 0;
                while(lineKeys.next() && i < po.getOrderLines().size()){
                    po.getOrderLines().get(i).setPoLineID(lineKeys.getInt(1));
                    i++;
                }
            }

            // Recalculate total amount based on database-calculated line totals if schema supports it,
            // or ensure the sum of client-side calculated line totals is accurate.
            // For now, we trust the client-side calculation passed in po.getTotalAmount().
            // If LineTotal is a generated column in DB, we might need to re-fetch PO.

            conn.commit(); // Commit transaction
            return po;
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
            if (pstmtPo != null) pstmtPo.close();
            if (pstmtLine != null) pstmtLine.close();
            if (conn != null) {
                conn.setAutoCommit(true); // Reset auto-commit
                conn.close();
            }
        }
    }

    public PurchaseOrder getPurchaseOrderById(int poId) throws SQLException {
        String poSql = "SELECT * FROM PurchaseOrders WHERE PO_ID = ?";
        String lineSql = "SELECT POLineID, PO_ID, BookID, RequestedBookTitle, Quantity, UnitPrice, (Quantity * UnitPrice) as LineTotal FROM PurchaseOrderLines WHERE PO_ID = ?";
        PurchaseOrder po = null;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmtPo = conn.prepareStatement(poSql)) {

            pstmtPo.setInt(1, poId);
            ResultSet rsPo = pstmtPo.executeQuery();

            if (rsPo.next()) {
                po = mapRowToPurchaseOrder(rsPo);
                // Fetch lines
                try (PreparedStatement pstmtLine = conn.prepareStatement(lineSql)) {
                    pstmtLine.setInt(1, poId);
                    ResultSet rsLines = pstmtLine.executeQuery();
                    List<PurchaseOrderLine> lines = new ArrayList<>();
                    while (rsLines.next()) {
                        lines.add(mapRowToPurchaseOrderLine(rsLines));
                    }
                    po.setOrderLines(lines);
                }
            }
        }
        return po;
    }

    public List<PurchaseOrder> getAllPurchaseOrders() throws SQLException {
        List<PurchaseOrder> pos = new ArrayList<>();
        // Simpler query for list view, details fetched when a specific PO is selected
        String sql = "SELECT * FROM PurchaseOrders ORDER BY OrderDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                pos.add(mapRowToPurchaseOrder(rs));
                // For full details including lines, you'd call getPurchaseOrderById for each or do a more complex join.
                // For performance, typically a list view shows summary, and lines are lazy-loaded.
            }
        }
        return pos;
    }

    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) throws SQLException {
        List<PurchaseOrder> pos = new ArrayList<>();
        String sql = "SELECT * FROM PurchaseOrders WHERE Status = ? ORDER BY OrderDate DESC";
         try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pos.add(mapRowToPurchaseOrder(rs));
                }
            }
        }
        return pos;
    }

    public boolean updatePurchaseOrderStatus(int poId, String newStatus) throws SQLException {
        String sql = "UPDATE PurchaseOrders SET Status = ? WHERE PO_ID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, poId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // More comprehensive update would involve updating lines (add, remove, modify quantity/price)
    // This is a simplified update for header fields like status or expected delivery date.
    public boolean updatePurchaseOrderHeader(PurchaseOrder po) throws SQLException {
        String sql = "UPDATE PurchaseOrders SET SupplierID = ?, OrderDate = ?, ExpectedDeliveryDate = ?, Status = ?, TotalAmount = ?, PONumber = ? WHERE PO_ID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, po.getSupplierID());
            pstmt.setDate(2, po.getOrderDate());
            pstmt.setDate(3, po.getExpectedDeliveryDate());
            pstmt.setString(4, po.getStatus());
            pstmt.setBigDecimal(5, po.getTotalAmount());
            pstmt.setString(6, po.getPoNumber());
            pstmt.setInt(7, po.getPoID());
            return pstmt.executeUpdate() > 0;
        }
    }


    // Deleting a PO might be restricted based on status (e.g., cannot delete if partially received)
    // Also, lines should be deleted first or handled by DB cascade.
    public boolean deletePurchaseOrder(int poId) throws SQLException {
        // Assumes ON DELETE CASCADE for PurchaseOrderLines or manual deletion of lines first
        String deleteLinesSql = "DELETE FROM PurchaseOrderLines WHERE PO_ID = ?";
        String deletePoSql = "DELETE FROM PurchaseOrders WHERE PO_ID = ?";
        Connection conn = null;
        PreparedStatement pstmtLines = null;
        PreparedStatement pstmtPo = null;
        boolean deleted = false;

        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            // Delete lines
            pstmtLines = conn.prepareStatement(deleteLinesSql);
            pstmtLines.setInt(1, poId);
            pstmtLines.executeUpdate();

            // Delete PO header
            pstmtPo = conn.prepareStatement(deletePoSql);
            pstmtPo.setInt(1, poId);
            int rowsAffected = pstmtPo.executeUpdate();
            if (rowsAffected > 0) {
                deleted = true;
            }

            conn.commit();
            return deleted;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmtLines != null) pstmtLines.close();
            if (pstmtPo != null) pstmtPo.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }


    private PurchaseOrder mapRowToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoID(rs.getInt("PO_ID"));
        po.setPoNumber(rs.getString("PONumber"));
        po.setSupplierID(rs.getInt("SupplierID"));
        po.setOrderDate(rs.getDate("OrderDate"));
        po.setExpectedDeliveryDate(rs.getDate("ExpectedDeliveryDate"));
        po.setStatus(rs.getString("Status"));
        po.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        po.setCreatedBy(rs.getInt("CreatedBy"));
        // Lines are typically fetched separately or via a join in a dedicated method
        return po;
    }

    private PurchaseOrderLine mapRowToPurchaseOrderLine(ResultSet rs) throws SQLException {
        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setPoLineID(rs.getInt("POLineID"));
        line.setPoID(rs.getInt("PO_ID"));
        line.setBookID(rs.getObject("BookID", Integer.class)); // Handle potential NULL
        line.setRequestedBookTitle(rs.getString("RequestedBookTitle"));
        line.setQuantity(rs.getInt("Quantity"));
        line.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        // line.setLineTotal(rs.getBigDecimal("LineTotal")); // If LineTotal is directly selected
        return line;
    }
}
