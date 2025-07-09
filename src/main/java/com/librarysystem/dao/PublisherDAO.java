package com.librarysystem.dao;

import com.librarysystem.model.Publisher;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PublisherDAO {

    public Publisher createPublisher(Publisher publisher) throws SQLException {
        String sql = "INSERT INTO Publishers (PublisherName, Address, Website) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, publisher.getPublisherName());
            pstmt.setString(2, publisher.getAddress()); // Can be null
            pstmt.setString(3, publisher.getWebsite()); // Can be null
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    publisher.setPublisherID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating publisher failed, no ID obtained.");
                }
            }
            return publisher;
        }
    }

    public Optional<Publisher> getPublisherById(int publisherId) throws SQLException {
        String sql = "SELECT * FROM Publishers WHERE PublisherID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, publisherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPublisher(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Publisher> getPublisherByName(String name) throws SQLException {
        String sql = "SELECT * FROM Publishers WHERE PublisherName = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPublisher(rs));
                }
            }
        }
        return Optional.empty();
    }


    public List<Publisher> getAllPublishers() throws SQLException {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT * FROM Publishers ORDER BY PublisherName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                publishers.add(mapRowToPublisher(rs));
            }
        }
        return publishers;
    }

    public boolean updatePublisher(Publisher publisher) throws SQLException {
        String sql = "UPDATE Publishers SET PublisherName = ?, Address = ?, Website = ? WHERE PublisherID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, publisher.getPublisherName());
            pstmt.setString(2, publisher.getAddress());
            pstmt.setString(3, publisher.getWebsite());
            pstmt.setInt(4, publisher.getPublisherID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deletePublisher(int publisherId) throws SQLException {
        // Consider checking Books table for dependencies before deletion
        String sql = "DELETE FROM Publishers WHERE PublisherID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, publisherId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Publisher mapRowToPublisher(ResultSet rs) throws SQLException {
        Publisher publisher = new Publisher();
        publisher.setPublisherID(rs.getInt("PublisherID"));
        publisher.setPublisherName(rs.getString("PublisherName"));
        publisher.setAddress(rs.getString("Address"));
        publisher.setWebsite(rs.getString("Website"));
        return publisher;
    }
}
