package com.librarysystem.dao;

import com.librarysystem.model.Location;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocationDAO {

    public Location createLocation(Location location) throws SQLException {
        String sql = "INSERT INTO Locations (BranchName, Address, PhoneNumber, OperatingHours) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, location.getBranchName());
            pstmt.setString(2, location.getAddress());
            pstmt.setString(3, location.getPhoneNumber());
            pstmt.setString(4, location.getOperatingHours());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    location.setLocationID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating location failed, no ID obtained.");
                }
            }
            return location;
        }
    }

    public Optional<Location> getLocationById(int locationId) throws SQLException {
        String sql = "SELECT * FROM Locations WHERE LocationID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToLocation(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Location> getAllLocations() throws SQLException {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT * FROM Locations ORDER BY BranchName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                locations.add(mapRowToLocation(rs));
            }
        }
        return locations;
    }

    public boolean updateLocation(Location location) throws SQLException {
        String sql = "UPDATE Locations SET BranchName = ?, Address = ?, PhoneNumber = ?, OperatingHours = ? WHERE LocationID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, location.getBranchName());
            pstmt.setString(2, location.getAddress());
            pstmt.setString(3, location.getPhoneNumber());
            pstmt.setString(4, location.getOperatingHours());
            pstmt.setInt(5, location.getLocationID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteLocation(int locationId) throws SQLException {
        // Consider checking dependencies (e.g., Copies assigned to this location)
        String sql = "DELETE FROM Locations WHERE LocationID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, locationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Location mapRowToLocation(ResultSet rs) throws SQLException {
        Location location = new Location();
        location.setLocationID(rs.getInt("LocationID"));
        location.setBranchName(rs.getString("BranchName"));
        location.setAddress(rs.getString("Address"));
        location.setPhoneNumber(rs.getString("PhoneNumber"));
        location.setOperatingHours(rs.getString("OperatingHours"));
        return location;
    }
}
