package com.librarysystem.dao;

import com.librarysystem.model.Supplier;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {

    public Supplier addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Suppliers (SupplierName, ContactPerson, Address, PhoneNumber, Email, PaymentTerms) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getContactPerson());
            pstmt.setString(3, supplier.getAddress());
            pstmt.setString(4, supplier.getPhoneNumber());
            pstmt.setString(5, supplier.getEmail());
            pstmt.setString(6, supplier.getPaymentTerms());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplier.setSupplierID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating supplier failed, no ID obtained.");
                }
            }
            return supplier;
        }
    }

    public Supplier getSupplierById(int supplierId) throws SQLException {
        String sql = "SELECT * FROM Suppliers WHERE SupplierID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToSupplier(rs);
            }
        }
        return null;
    }

    public List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers ORDER BY SupplierName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                suppliers.add(mapRowToSupplier(rs));
            }
        }
        return suppliers;
    }

    public boolean updateSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE Suppliers SET SupplierName = ?, ContactPerson = ?, Address = ?, PhoneNumber = ?, Email = ?, PaymentTerms = ? WHERE SupplierID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getContactPerson());
            pstmt.setString(3, supplier.getAddress());
            pstmt.setString(4, supplier.getPhoneNumber());
            pstmt.setString(5, supplier.getEmail());
            pstmt.setString(6, supplier.getPaymentTerms());
            pstmt.setInt(7, supplier.getSupplierID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteSupplier(int supplierId) throws SQLException {
        String sql = "DELETE FROM Suppliers WHERE SupplierID = ?";
        // Consider checking for dependencies (e.g., existing POs) before deleting
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Supplier> findSuppliersByName(String name) throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Suppliers WHERE LOWER(SupplierName) LIKE ? ORDER BY SupplierName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapRowToSupplier(rs));
                }
            }
        }
        return suppliers;
    }

    private Supplier mapRowToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierID(rs.getInt("SupplierID"));
        supplier.setSupplierName(rs.getString("SupplierName"));
        supplier.setContactPerson(rs.getString("ContactPerson"));
        supplier.setAddress(rs.getString("Address"));
        supplier.setPhoneNumber(rs.getString("PhoneNumber"));
        supplier.setEmail(rs.getString("Email"));
        supplier.setPaymentTerms(rs.getString("PaymentTerms"));
        return supplier;
    }
}
