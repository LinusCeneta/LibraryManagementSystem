package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.PurchaseRequest;
import utils.DBConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PurchaseRequestDAO {

	public void addPurchaseRequest(PurchaseRequest request) throws SQLException {
	    String sql = "INSERT INTO PurchaseRequest (title, requested_by, request_date, status) VALUES (?, ?, ?, ?)";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        conn.setAutoCommit(true);
	        stmt.setString(1, request.getTitle());
	        stmt.setString(2, request.getRequestedBy());
	        stmt.setDate(3, request.getRequestDate());
	        stmt.setString(4, request.getStatus() != null ? request.getStatus() : "Pending"); // Ensure status is set
	        stmt.executeUpdate();
	        
	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                request.setRequestId(generatedKeys.getInt(1));
	            }
	        }
	    }
	}

    public List<PurchaseRequest> getApprovedRequests() throws SQLException {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM PurchaseRequest WHERE status = 'Approved'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            conn.setAutoCommit(true);
            while (rs.next()) {
                PurchaseRequest r = new PurchaseRequest();
                r.setRequestId(rs.getInt("request_id"));
                r.setTitle(rs.getString("title"));
                r.setRequestedBy(rs.getString("requested_by"));
                r.setRequestDate(rs.getDate("request_date"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        }
        return list;
    }

    public void updateStatus(int requestId, String status) throws SQLException {
        System.out.println("=== DEBUG: Entering updateStatus ===");
        System.out.println("Request ID: " + requestId);
        System.out.println("New Status: " + status);
        
        String sql = "UPDATE PurchaseRequest SET status=? WHERE request_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
        }
        System.out.println("=== DEBUG: Exiting updateStatus ===");
    }

    public PurchaseRequest getRequestById(int id) throws SQLException {
        String sql = "SELECT * FROM PurchaseRequest WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PurchaseRequest r = new PurchaseRequest();
                    r.setRequestId(rs.getInt("request_id"));
                    r.setTitle(rs.getString("title"));
                    r.setRequestedBy(rs.getString("requested_by"));
                    r.setRequestDate(rs.getDate("request_date"));
                    r.setStatus(rs.getString("status"));
                    return r;
                }
            }
        }
        return null;
    }

    public void updateRequest(PurchaseRequest request) throws SQLException {
        String sql = "UPDATE PurchaseRequest SET title=?, requested_by=?, request_date=?, status=? WHERE request_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, request.getTitle());
            stmt.setString(2, request.getRequestedBy());
            stmt.setDate(3, request.getRequestDate());
            stmt.setString(4, request.getStatus());
            stmt.setInt(5, request.getRequestId());
            stmt.executeUpdate();
        }
    }
    
    public List<PurchaseRequest> getAllRequests() throws SQLException {
        List<PurchaseRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM PurchaseRequest";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            conn.setAutoCommit(true);
            while (rs.next()) {
                PurchaseRequest r = new PurchaseRequest();
                r.setRequestId(rs.getInt("request_id"));
                r.setTitle(rs.getString("title"));
                r.setRequestedBy(rs.getString("requested_by"));
                r.setRequestDate(rs.getDate("request_date"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        }
        return list;
    }

    public boolean deleteRequest(int requestId) throws SQLException {
        String sql = "DELETE FROM PurchaseRequest WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(true);
            stmt.setInt(1, requestId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // returns true if a row was deleted
        }
    }
}