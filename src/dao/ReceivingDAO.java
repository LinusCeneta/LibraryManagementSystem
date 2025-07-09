package dao;

import java.sql.*;
import model.GoodsReceiptNote;
import utils.DBConnection;

public class ReceivingDAO {

    public void recordReceipt(GoodsReceiptNote grn) throws SQLException {
        String sql = "INSERT INTO GoodsReceiptNote (po_id, receipt_date, invoice_number, invoice_date, notes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grn.getPoId());
            stmt.setDate(2, grn.getReceiptDate());
            stmt.setString(3, grn.getInvoiceNumber());
            stmt.setDate(4, grn.getInvoiceDate());
            stmt.setString(5, grn.getNotes());
            stmt.executeUpdate();
        }
    }
}
