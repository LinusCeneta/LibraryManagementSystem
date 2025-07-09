package com.librarysystem.dao;

import com.librarysystem.model.PasswordResetToken;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PasswordResetTokenDAO {

    private static final long DEFAULT_EXPIRY_DURATION_MINUTES = 60; // Token valid for 1 hour

    public PasswordResetToken createPasswordResetToken(int userId) throws SQLException {
        String tokenString = UUID.randomUUID().toString();
        Timestamp expiryDate = new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(DEFAULT_EXPIRY_DURATION_MINUTES));

        PasswordResetToken prToken = new PasswordResetToken(userId, tokenString, expiryDate);

        String sql = "INSERT INTO PasswordResetTokens (UserID, Token, ExpiryDate, IsUsed) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, prToken.getUserID());
            pstmt.setString(2, prToken.getToken());
            pstmt.setTimestamp(3, prToken.getExpiryDate());
            pstmt.setBoolean(4, prToken.isUsed());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prToken.setTokenID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating password reset token failed, no ID obtained.");
                }
            }
            return prToken;
        }
    }

    public Optional<PasswordResetToken> findByToken(String token) throws SQLException {
        String sql = "SELECT * FROM PasswordResetTokens WHERE Token = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPasswordResetToken(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<PasswordResetToken> findByUserIdAndValid(int userId) throws SQLException {
        String sql = "SELECT * FROM PasswordResetTokens WHERE UserID = ? AND ExpiryDate > ? AND IsUsed = FALSE ORDER BY ExpiryDate DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // Get the latest valid token
                    return Optional.of(mapRowToPasswordResetToken(rs));
                }
            }
        }
        return Optional.empty();
    }


    public boolean markTokenAsUsed(String token) throws SQLException {
        String sql = "UPDATE PasswordResetTokens SET IsUsed = TRUE WHERE Token = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteToken(String token) throws SQLException {
         String sql = "DELETE FROM PasswordResetTokens WHERE Token = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            return pstmt.executeUpdate() > 0;
        }
    }

    public int deleteExpiredTokens() throws SQLException {
        String sql = "DELETE FROM PasswordResetTokens WHERE ExpiryDate <= ? OR IsUsed = TRUE";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            return pstmt.executeUpdate();
        }
    }

    private PasswordResetToken mapRowToPasswordResetToken(ResultSet rs) throws SQLException {
        PasswordResetToken prToken = new PasswordResetToken();
        prToken.setTokenID(rs.getInt("TokenID"));
        prToken.setUserID(rs.getInt("UserID"));
        prToken.setToken(rs.getString("Token"));
        prToken.setExpiryDate(rs.getTimestamp("ExpiryDate"));
        prToken.setUsed(rs.getBoolean("IsUsed"));
        return prToken;
    }
}
