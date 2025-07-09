package com.librarysystem.dao;

import com.librarysystem.model.UserProfile;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Optional;

public class UserProfileDAO {

    public UserProfile createUserProfile(UserProfile profile) throws SQLException {
        String sql = "INSERT INTO UserProfiles (UserID, AddressLine1, AddressLine2, City, StateProvince, PostalCode, Country, PhoneNumber, BirthDate, EmergencyContactName, EmergencyContactPhone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, profile.getUserID());
            pstmt.setString(2, profile.getAddressLine1());
            pstmt.setString(3, profile.getAddressLine2());
            pstmt.setString(4, profile.getCity());
            pstmt.setString(5, profile.getStateProvince());
            pstmt.setString(6, profile.getPostalCode());
            pstmt.setString(7, profile.getCountry());
            pstmt.setString(8, profile.getPhoneNumber());

            if (profile.getBirthDate() != null) {
                pstmt.setDate(9, profile.getBirthDate());
            } else {
                pstmt.setNull(9, Types.DATE);
            }

            pstmt.setString(10, profile.getEmergencyContactName());
            pstmt.setString(11, profile.getEmergencyContactPhone());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    profile.setProfileID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user profile failed, no ID obtained.");
                }
            }
            return profile;
        }
    }

    public Optional<UserProfile> getUserProfileByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM UserProfiles WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUserProfile(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<UserProfile> getUserProfileByProfileId(int profileId) throws SQLException {
        String sql = "SELECT * FROM UserProfiles WHERE ProfileID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUserProfile(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean updateUserProfile(UserProfile profile) throws SQLException {
        String sql = "UPDATE UserProfiles SET AddressLine1 = ?, AddressLine2 = ?, City = ?, StateProvince = ?, PostalCode = ?, Country = ?, PhoneNumber = ?, BirthDate = ?, EmergencyContactName = ?, EmergencyContactPhone = ? WHERE UserID = ?";
        // Or use ProfileID if that's preferred: WHERE ProfileID = ?
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, profile.getAddressLine1());
            pstmt.setString(2, profile.getAddressLine2());
            pstmt.setString(3, profile.getCity());
            pstmt.setString(4, profile.getStateProvince());
            pstmt.setString(5, profile.getPostalCode());
            pstmt.setString(6, profile.getCountry());
            pstmt.setString(7, profile.getPhoneNumber());

            if (profile.getBirthDate() != null) {
                pstmt.setDate(8, profile.getBirthDate());
            } else {
                pstmt.setNull(8, Types.DATE);
            }

            pstmt.setString(9, profile.getEmergencyContactName());
            pstmt.setString(10, profile.getEmergencyContactPhone());
            pstmt.setInt(11, profile.getUserID()); // Assuming update by UserID due to UNIQUE constraint

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUserProfileByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM UserProfiles WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUserProfileByProfileId(int profileId) throws SQLException {
        String sql = "DELETE FROM UserProfiles WHERE ProfileID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, profileId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private UserProfile mapRowToUserProfile(ResultSet rs) throws SQLException {
        UserProfile profile = new UserProfile();
        profile.setProfileID(rs.getInt("ProfileID"));
        profile.setUserID(rs.getInt("UserID"));
        profile.setAddressLine1(rs.getString("AddressLine1"));
        profile.setAddressLine2(rs.getString("AddressLine2"));
        profile.setCity(rs.getString("City"));
        profile.setStateProvince(rs.getString("StateProvince"));
        profile.setPostalCode(rs.getString("PostalCode"));
        profile.setCountry(rs.getString("Country"));
        profile.setPhoneNumber(rs.getString("PhoneNumber"));
        profile.setBirthDate(rs.getDate("BirthDate"));
        profile.setEmergencyContactName(rs.getString("EmergencyContactName"));
        profile.setEmergencyContactPhone(rs.getString("EmergencyContactPhone"));
        return profile;
    }
}
