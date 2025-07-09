package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.PurchaseOrder;
import utils.DBConnection;

public class PurchaseOrderDAO {

    public int createPurchaseOrder(PurchaseOrder po) throws SQLException {
        String sql = "INSERT INTO PurchaseOrder (po_number, supplier_id, created_date, expected_delivery_date, status) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, po.getPoNumber());
                stmt.setInt(2, po.getSupplierId());
                stmt.setDate(3, po.getCreatedDate());
                stmt.setDate(4, po.getExpectedDeliveryDate());
                stmt.setString(5, po.getStatus());
                
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        conn.commit();
                        return generatedId;
                    }
                }
            }
            conn.rollback();
            return -1;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public List<PurchaseOrder> getAllPurchaseOrders() throws SQLException {
        List<PurchaseOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM PurchaseOrder";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(true);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    PurchaseOrder po = new PurchaseOrder();
                    po.setPoId(rs.getInt("po_id"));
                    po.setPoNumber(rs.getString("po_number"));
                    po.setSupplierId(rs.getInt("supplier_id"));
                    po.setCreatedDate(rs.getDate("created_date"));
                    po.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
                    po.setStatus(rs.getString("status"));
                    list.add(po);
                }
            }
        }
        return list;
    }

    public void updateStatus(int poId, String status) throws SQLException {
        String sql = "UPDATE PurchaseOrder SET status=? WHERE po_id=?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setInt(2, poId);
                stmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public void deletePurchaseOrder(int poId) throws SQLException {
        String sql = "DELETE FROM PurchaseOrder WHERE po_id=?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, poId);
                stmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                if (conn != null) {
                    conn.rollback();
                }
                throw e;
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    public List<PurchaseOrder> getPurchaseOrdersByRequestId(int requestId) throws SQLException {
        List<PurchaseOrder> list = new ArrayList<>();
        String sql = "SELECT * FROM PurchaseOrder WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder po = new PurchaseOrder();
                    po.setPoId(rs.getInt("po_id"));
                    po.setPoNumber(rs.getString("po_number"));
                    po.setSupplierId(rs.getInt("supplier_id"));
                    po.setCreatedDate(rs.getDate("created_date"));
                    po.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
                    po.setStatus(rs.getString("status"));
                    po.setRequestId(rs.getInt("request_id"));
                    list.add(po);
                }
            }
        }
        return list;
    }
    
    public PurchaseOrder getPurchaseOrderById(int poId) throws SQLException {
        String sql = "SELECT * FROM PurchaseOrder WHERE po_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, poId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PurchaseOrder po = new PurchaseOrder();
                    po.setPoId(rs.getInt("po_id"));
                    po.setPoNumber(rs.getString("po_number"));
                    po.setSupplierId(rs.getInt("supplier_id"));
                    po.setCreatedDate(rs.getDate("created_date"));
                    po.setExpectedDeliveryDate(rs.getDate("expected_delivery_date"));
                    po.setStatus(rs.getString("status"));
                    po.setRequestId(rs.getInt("request_id"));
                    return po;
                }
            }
        }
        return null;
    }
}