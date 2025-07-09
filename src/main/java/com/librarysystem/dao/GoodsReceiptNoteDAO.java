package com.librarysystem.dao;

import com.librarysystem.model.Copy; // Assuming Copy model exists
import com.librarysystem.model.GoodsReceiptNote;
import com.librarysystem.model.GoodsReceiptNoteItem;
import com.librarysystem.util.DBConnectionUtil; // Assuming DBConnectionUtil exists

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoodsReceiptNoteDAO {

    // Create Goods Receipt Note (Header and Items)
    // This will also involve creating Copy records for accepted items.
    public GoodsReceiptNote createGoodsReceiptNote(GoodsReceiptNote grn) throws SQLException {
        String grnSql = "INSERT INTO GoodsReceiptNotes (PO_ID, SupplierID, InvoiceNumber, InvoiceDate, ReceivedDate, ReceivedBy, Notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String itemSql = "INSERT INTO GoodsReceiptNoteItems (GRN_ID, POLineID, BookID, ReceivedQuantity, AcceptedQuantity, Condition, Notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // String copySql = "INSERT INTO Copies (BookID, AcquisitionDate, Cost, Condition, Location, Status, CopyBarcode) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Copy creation logic will be more complex, potentially involving a CopyDAO.
        // For now, we focus on GRN and GRNItems. Copy creation is a subsequent step.

        Connection conn = null;
        PreparedStatement pstmtGrn = null;
        PreparedStatement pstmtItem = null;

        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert GRN Header
            pstmtGrn = conn.prepareStatement(grnSql, Statement.RETURN_GENERATED_KEYS);
            pstmtGrn.setInt(1, grn.getPoID());
            pstmtGrn.setInt(2, grn.getSupplierID());
            pstmtGrn.setString(3, grn.getInvoiceNumber());
            pstmtGrn.setDate(4, grn.getInvoiceDate());
            pstmtGrn.setDate(5, grn.getReceivedDate());
            pstmtGrn.setInt(6, grn.getReceivedBy());
            pstmtGrn.setString(7, grn.getNotes());
            pstmtGrn.executeUpdate();

            ResultSet generatedKeys = pstmtGrn.getGeneratedKeys();
            if (generatedKeys.next()) {
                grn.setGrnID(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating GRN failed, no GRN_ID obtained.");
            }

            // Insert GRN Items
            if (grn.getItems() != null && !grn.getItems().isEmpty()) {
                pstmtItem = conn.prepareStatement(itemSql, Statement.RETURN_GENERATED_KEYS);
                CopyDAO copyDAO = new CopyDAO(); // Assuming CopyDAO exists for creating copies

                for (GoodsReceiptNoteItem item : grn.getItems()) {
                    item.setGrnID(grn.getGrnID()); // Ensure GRN_ID is set
                    pstmtItem.setInt(1, item.getGrnID());
                    if (item.getPoLineID() != null) {
                        pstmtItem.setInt(2, item.getPoLineID());
                    } else {
                        pstmtItem.setNull(2, Types.INTEGER);
                    }
                    pstmtItem.setInt(3, item.getBookID()); // BookID must be resolved by this point
                    pstmtItem.setInt(4, item.getReceivedQuantity());
                    pstmtItem.setInt(5, item.getAcceptedQuantity());
                    pstmtItem.setString(6, item.getCondition());
                    pstmtItem.setString(7, item.getNotes());
                    pstmtItem.addBatch();

                    // TODO: Logic for creating 'Copy' records for each accepted item.
                    // This is a placeholder and should be more robust.
                    // For each acceptedQuantity, a new Copy record needs to be made.
                    // This might happen in a service layer after the GRN item is saved.
                    // For now, we'll assume this happens elsewhere or is simplified.
                    /*
                    for (int i = 0; i < item.getAcceptedQuantity(); i++) {
                        Copy newCopy = new Copy();
                        newCopy.setBookID(item.getBookID());
                        newCopy.setAcquisitionDate(grn.getReceivedDate());
                        // newCopy.setCost(...); // Need to get cost from PO line or book record
                        newCopy.setCondition(item.getCondition());
                        newCopy.setStatus("Available"); // Default status for new copies
                        // newCopy.setLocation(...); // Default location or prompt
                        // newCopy.setCopyBarcode(...); // Generate or assign barcode
                        // copyDAO.addCopy(newCopy); // Call to CopyDAO
                    }
                    */
                }
                pstmtItem.executeBatch();

                ResultSet itemKeys = pstmtItem.getGeneratedKeys();
                int i = 0;
                while(itemKeys.next() && i < grn.getItems().size()){
                    grn.getItems().get(i).setGrnItemID(itemKeys.getInt(1));
                    i++;
                }
            }

            conn.commit(); // Commit transaction
            return grn;
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
            if (pstmtGrn != null) pstmtGrn.close();
            if (pstmtItem != null) pstmtItem.close();
            if (conn != null) {
                conn.setAutoCommit(true); // Reset auto-commit
                conn.close();
            }
        }
    }

    public GoodsReceiptNote getGoodsReceiptNoteById(int grnId) throws SQLException {
        String grnSql = "SELECT * FROM GoodsReceiptNotes WHERE GRN_ID = ?";
        String itemSql = "SELECT * FROM GoodsReceiptNoteItems WHERE GRN_ID = ?";
        GoodsReceiptNote grn = null;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmtGrn = conn.prepareStatement(grnSql)) {

            pstmtGrn.setInt(1, grnId);
            ResultSet rsGrn = pstmtGrn.executeQuery();

            if (rsGrn.next()) {
                grn = mapRowToGoodsReceiptNote(rsGrn);
                // Fetch items
                try (PreparedStatement pstmtItem = conn.prepareStatement(itemSql)) {
                    pstmtItem.setInt(1, grnId);
                    ResultSet rsItems = pstmtItem.executeQuery();
                    List<GoodsReceiptNoteItem> items = new ArrayList<>();
                    while (rsItems.next()) {
                        items.add(mapRowToGoodsReceiptNoteItem(rsItems));
                    }
                    grn.setItems(items);
                }
            }
        }
        return grn;
    }

    public List<GoodsReceiptNote> getAllGoodsReceiptNotes() throws SQLException {
        List<GoodsReceiptNote> grns = new ArrayList<>();
        String sql = "SELECT * FROM GoodsReceiptNotes ORDER BY ReceivedDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // For list view, usually only header info is needed. Items can be lazy-loaded.
                grns.add(mapRowToGoodsReceiptNote(rs));
            }
        }
        return grns;
    }

    public List<GoodsReceiptNote> getGoodsReceiptNotesByPOId(int poId) throws SQLException {
        List<GoodsReceiptNote> grns = new ArrayList<>();
        String sql = "SELECT * FROM GoodsReceiptNotes WHERE PO_ID = ? ORDER BY ReceivedDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, poId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Fetch full details including items for each GRN related to the PO
                    GoodsReceiptNote grn = mapRowToGoodsReceiptNote(rs);
                    grn.setItems(getGoodsReceiptNoteItemsByGRNId(grn.getGrnID(), conn)); // Pass connection to reuse
                    grns.add(grn);
                }
            }
        }
        return grns;
    }

    private List<GoodsReceiptNoteItem> getGoodsReceiptNoteItemsByGRNId(int grnId, Connection conn) throws SQLException {
        // Helper method to fetch items for a given GRN_ID, using an existing connection
        String itemSql = "SELECT * FROM GoodsReceiptNoteItems WHERE GRN_ID = ?";
        List<GoodsReceiptNoteItem> items = new ArrayList<>();
        try (PreparedStatement pstmtItem = conn.prepareStatement(itemSql)) {
            pstmtItem.setInt(1, grnId);
            ResultSet rsItems = pstmtItem.executeQuery();
            while (rsItems.next()) {
                items.add(mapRowToGoodsReceiptNoteItem(rsItems));
            }
        }
        return items;
    }


    // Update and Delete for GRN might be complex due to inventory implications (Copies created).
    // Generally, GRNs are append-only or require careful handling for corrections (e.g., reverse GRN).
    // Basic stubs are provided.

    public boolean updateGoodsReceiptNoteHeader(GoodsReceiptNote grn) throws SQLException {
        String sql = "UPDATE GoodsReceiptNotes SET PO_ID = ?, SupplierID = ?, InvoiceNumber = ?, InvoiceDate = ?, ReceivedDate = ?, ReceivedBy = ?, Notes = ? WHERE GRN_ID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grn.getPoID());
            pstmt.setInt(2, grn.getSupplierID());
            pstmt.setString(3, grn.getInvoiceNumber());
            pstmt.setDate(4, grn.getInvoiceDate());
            pstmt.setDate(5, grn.getReceivedDate());
            pstmt.setInt(6, grn.getReceivedBy());
            pstmt.setString(7, grn.getNotes());
            pstmt.setInt(8, grn.getGrnID());
            return pstmt.executeUpdate() > 0;
        }
    }

    // Deleting a GRN would logically require reversing the inventory additions (Copies).
    // This is highly complex and usually handled by specific business processes (e.g., return to vendor).
    // A simple delete is problematic. For now, this is a placeholder.
    public boolean deleteGoodsReceiptNote(int grnId) throws SQLException {
        // WARNING: This does not handle inventory implications (created Copies).
        // Proper implementation would require reversing stock entries or archiving.
        String deleteItemsSql = "DELETE FROM GoodsReceiptNoteItems WHERE GRN_ID = ?";
        String deleteGrnSql = "DELETE FROM GoodsReceiptNotes WHERE GRN_ID = ?";
        Connection conn = null;

        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            try(PreparedStatement pstmtItems = conn.prepareStatement(deleteItemsSql)) {
                pstmtItems.setInt(1, grnId);
                pstmtItems.executeUpdate();
            }

            int rowsAffected = 0;
            try(PreparedStatement pstmtGrn = conn.prepareStatement(deleteGrnSql)) {
                pstmtGrn.setInt(1, grnId);
                rowsAffected = pstmtGrn.executeUpdate();
            }

            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                 conn.setAutoCommit(true);
                 conn.close();
            }
        }
    }


    private GoodsReceiptNote mapRowToGoodsReceiptNote(ResultSet rs) throws SQLException {
        GoodsReceiptNote grn = new GoodsReceiptNote();
        grn.setGrnID(rs.getInt("GRN_ID"));
        grn.setPoID(rs.getInt("PO_ID"));
        grn.setSupplierID(rs.getInt("SupplierID"));
        grn.setInvoiceNumber(rs.getString("InvoiceNumber"));
        grn.setInvoiceDate(rs.getDate("InvoiceDate"));
        grn.setReceivedDate(rs.getDate("ReceivedDate"));
        grn.setReceivedBy(rs.getInt("ReceivedBy"));
        grn.setNotes(rs.getString("Notes"));
        // Items are typically fetched separately
        return grn;
    }

    private GoodsReceiptNoteItem mapRowToGoodsReceiptNoteItem(ResultSet rs) throws SQLException {
        GoodsReceiptNoteItem item = new GoodsReceiptNoteItem();
        item.setGrnItemID(rs.getInt("GRNItemID"));
        item.setGrnID(rs.getInt("GRN_ID"));
        item.setPoLineID(rs.getObject("POLineID", Integer.class));
        item.setBookID(rs.getInt("BookID"));
        item.setReceivedQuantity(rs.getInt("ReceivedQuantity"));
        item.setAcceptedQuantity(rs.getInt("AcceptedQuantity"));
        item.setCondition(rs.getString("Condition"));
        item.setNotes(rs.getString("Notes"));
        return item;
    }

    // Dummy CopyDAO class for compilation. Replace with actual implementation.
    // This should be in its own file: com/librarysystem/dao/CopyDAO.java
    private static class CopyDAO {
        public void addCopy(Copy copy) throws SQLException {
            // Dummy implementation
            System.out.println("CopyDAO: Adding copy for BookID " + copy.getBookID());
        }
    }
}
