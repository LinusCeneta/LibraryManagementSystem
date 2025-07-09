package com.librarysystem.dao;

import com.librarysystem.model.ItemType;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemTypeDAO {

    public ItemType createItemType(ItemType itemType) throws SQLException {
        String sql = "INSERT INTO ItemTypes (TypeName, IsLoanable) VALUES (?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, itemType.getTypeName());
            pstmt.setBoolean(2, itemType.isLoanable());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    itemType.setItemTypeID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating item type failed, no ID obtained.");
                }
            }
            return itemType;
        }
    }

    public Optional<ItemType> getItemTypeById(int itemTypeId) throws SQLException {
        String sql = "SELECT * FROM ItemTypes WHERE ItemTypeID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemTypeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToItemType(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<ItemType> getItemTypeByName(String name) throws SQLException {
        String sql = "SELECT * FROM ItemTypes WHERE TypeName = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToItemType(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<ItemType> getAllItemTypes() throws SQLException {
        List<ItemType> itemTypes = new ArrayList<>();
        String sql = "SELECT * FROM ItemTypes ORDER BY TypeName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                itemTypes.add(mapRowToItemType(rs));
            }
        }
        return itemTypes;
    }

    public boolean updateItemType(ItemType itemType) throws SQLException {
        String sql = "UPDATE ItemTypes SET TypeName = ?, IsLoanable = ? WHERE ItemTypeID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, itemType.getTypeName());
            pstmt.setBoolean(2, itemType.isLoanable());
            pstmt.setInt(3, itemType.getItemTypeID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteItemType(int itemTypeId) throws SQLException {
        // Consider checking dependencies (Copies, FinePolicies) before deletion
        String sql = "DELETE FROM ItemTypes WHERE ItemTypeID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemTypeId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public void ensureDefaultItemTypesExist() throws SQLException {
        ItemType[] defaultTypes = {
            new ItemType("General Book", true),
            new ItemType("DVD/Blu-ray", true),
            new ItemType("Magazine", true),
            new ItemType("Reference Material", false),
            new ItemType("Childrens Book", true),
            new ItemType("eBook", true),
            new ItemType("Audiobook", true)
        };

        for (ItemType type : defaultTypes) {
            if (!getItemTypeByName(type.getTypeName()).isPresent()) {
                createItemType(type);
                System.out.println("Created default item type: " + type.getTypeName());
            }
        }
    }

    private ItemType mapRowToItemType(ResultSet rs) throws SQLException {
        ItemType itemType = new ItemType();
        itemType.setItemTypeID(rs.getInt("ItemTypeID"));
        itemType.setTypeName(rs.getString("TypeName"));
        itemType.setLoanable(rs.getBoolean("IsLoanable"));
        return itemType;
    }
}
