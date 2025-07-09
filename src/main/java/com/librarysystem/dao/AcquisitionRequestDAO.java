package com.librarysystem.dao;

import com.librarysystem.model.AcquisitionRequest;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AcquisitionRequestDAO {

    public AcquisitionRequest createRequest(AcquisitionRequest request) throws SQLException {
        String sql = "INSERT INTO AcquisitionRequests (BookTitle, Author, ISBN, Publisher, PublicationYear, RequestedBy, RequestDate, Status, Notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, request.getBookTitle());
            pstmt.setString(2, request.getAuthor());
            pstmt.setString(3, request.getIsbn());
            pstmt.setString(4, request.getPublisher());
            if (request.getPublicationYear() != null) {
                pstmt.setInt(5, request.getPublicationYear());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setInt(6, request.getRequestedBy());
            pstmt.setDate(7, request.getRequestDate());
            pstmt.setString(8, request.getStatus());
            pstmt.setString(9, request.getNotes());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    request.setRequestID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating acquisition request failed, no ID obtained.");
                }
            }
            return request;
        }
    }

    public AcquisitionRequest getRequestById(int requestId) throws SQLException {
        String sql = "SELECT * FROM AcquisitionRequests WHERE RequestID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToRequest(rs);
            }
        }
        return null;
    }

    public List<AcquisitionRequest> getAllRequests() throws SQLException {
        List<AcquisitionRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM AcquisitionRequests ORDER BY RequestDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                requests.add(mapRowToRequest(rs));
            }
        }
        return requests;
    }

    public List<AcquisitionRequest> getRequestsByStatus(String status) throws SQLException {
        List<AcquisitionRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM AcquisitionRequests WHERE Status = ? ORDER BY RequestDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                requests.add(mapRowToRequest(rs));
            }
        }
        return requests;
    }

    public List<AcquisitionRequest> getRequestsByUserId(int userId) throws SQLException {
        List<AcquisitionRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM AcquisitionRequests WHERE RequestedBy = ? ORDER BY RequestDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                requests.add(mapRowToRequest(rs));
            }
        }
        return requests;
    }

    public boolean updateRequestStatus(int requestId, String newStatus) throws SQLException {
        String sql = "UPDATE AcquisitionRequests SET Status = ? WHERE RequestID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateRequest(AcquisitionRequest request) throws SQLException {
        String sql = "UPDATE AcquisitionRequests SET BookTitle = ?, Author = ?, ISBN = ?, Publisher = ?, PublicationYear = ?, RequestedBy = ?, RequestDate = ?, Status = ?, Notes = ? WHERE RequestID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, request.getBookTitle());
            pstmt.setString(2, request.getAuthor());
            pstmt.setString(3, request.getIsbn());
            pstmt.setString(4, request.getPublisher());
            if (request.getPublicationYear() != null) {
                pstmt.setInt(5, request.getPublicationYear());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            pstmt.setInt(6, request.getRequestedBy());
            pstmt.setDate(7, request.getRequestDate());
            pstmt.setString(8, request.getStatus());
            pstmt.setString(9, request.getNotes());
            pstmt.setInt(10, request.getRequestID());
            return pstmt.executeUpdate() > 0;
        }
    }


    public boolean deleteRequest(int requestId) throws SQLException {
        String sql = "DELETE FROM AcquisitionRequests WHERE RequestID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private AcquisitionRequest mapRowToRequest(ResultSet rs) throws SQLException {
        AcquisitionRequest request = new AcquisitionRequest();
        request.setRequestID(rs.getInt("RequestID"));
        request.setBookTitle(rs.getString("BookTitle"));
        request.setAuthor(rs.getString("Author"));
        request.setIsbn(rs.getString("ISBN"));
        request.setPublisher(rs.getString("Publisher"));
        request.setPublicationYear(rs.getObject("PublicationYear", Integer.class)); // Handle potential NULL
        request.setRequestedBy(rs.getInt("RequestedBy"));
        request.setRequestDate(rs.getDate("RequestDate"));
        request.setStatus(rs.getString("Status"));
        request.setNotes(rs.getString("Notes"));
        return request;
    }
}
