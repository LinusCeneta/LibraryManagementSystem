package com.librarysystem.dao;

import com.librarysystem.model.FinePolicy;
import com.librarysystem.model.ItemType; // Assuming ItemType model
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FinePolicyDAO {

    private ItemTypeDAO itemTypeDAO; // To fetch ItemType details

    public FinePolicyDAO() {
        this.itemTypeDAO = new ItemTypeDAO();
    }

    public FinePolicyDAO(ItemTypeDAO itemTypeDAO) {
        this.itemTypeDAO = itemTypeDAO;
    }

    public FinePolicy createFinePolicy(FinePolicy policy) throws SQLException {
        String sql = "INSERT INTO FinePolicies (ItemTypeID, PolicyName, OverdueFinePerDay, MaxFineAmount, LostItemFeeFixed, LostItemFeePercentage, GracePeriodDays) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (policy.getItemTypeID() != null) {
                pstmt.setInt(1, policy.getItemTypeID());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, policy.getPolicyName());
            pstmt.setBigDecimal(3, policy.getOverdueFinePerDay());
            pstmt.setBigDecimal(4, policy.getMaxFineAmount()); // Can be null
            pstmt.setBigDecimal(5, policy.getLostItemFeeFixed()); // Can be null
            pstmt.setBigDecimal(6, policy.getLostItemFeePercentage()); // Can be null
            pstmt.setInt(7, policy.getGracePeriodDays());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    policy.setPolicyID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating fine policy failed, no ID obtained.");
                }
            }
            return policy;
        }
    }

    public Optional<FinePolicy> getFinePolicyById(int policyId) throws SQLException {
        String sql = "SELECT fp.*, it.TypeName as ItemTypeName, it.IsLoanable FROM FinePolicies fp LEFT JOIN ItemTypes it ON fp.ItemTypeID = it.ItemTypeID WHERE fp.PolicyID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, policyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToFinePolicyWithDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<FinePolicy> getFinePolicyByItemTypeId(int itemTypeId) throws SQLException {
        String sql = "SELECT fp.*, it.TypeName as ItemTypeName, it.IsLoanable FROM FinePolicies fp JOIN ItemTypes it ON fp.ItemTypeID = it.ItemTypeID WHERE fp.ItemTypeID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemTypeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // Assuming one policy per item type, or take the first one
                    return Optional.of(mapRowToFinePolicyWithDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<FinePolicy> getGeneralFinePolicy() throws SQLException {
        // Policy where ItemTypeID IS NULL
        String sql = "SELECT * FROM FinePolicies WHERE ItemTypeID IS NULL LIMIT 1"; // Derby specific LIMIT
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToFinePolicy(rs)); // No ItemType details to join
                }
            }
        }
        return Optional.empty();
    }


    public List<FinePolicy> getAllFinePolicies() throws SQLException {
        List<FinePolicy> policies = new ArrayList<>();
        String sql = "SELECT fp.*, it.TypeName as ItemTypeName, it.IsLoanable FROM FinePolicies fp LEFT JOIN ItemTypes it ON fp.ItemTypeID = it.ItemTypeID ORDER BY fp.PolicyName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                policies.add(mapRowToFinePolicyWithDetails(rs));
            }
        }
        return policies;
    }

    public boolean updateFinePolicy(FinePolicy policy) throws SQLException {
        String sql = "UPDATE FinePolicies SET ItemTypeID = ?, PolicyName = ?, OverdueFinePerDay = ?, MaxFineAmount = ?, LostItemFeeFixed = ?, LostItemFeePercentage = ?, GracePeriodDays = ? WHERE PolicyID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (policy.getItemTypeID() != null) {
                pstmt.setInt(1, policy.getItemTypeID());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, policy.getPolicyName());
            pstmt.setBigDecimal(3, policy.getOverdueFinePerDay());
            pstmt.setBigDecimal(4, policy.getMaxFineAmount());
            pstmt.setBigDecimal(5, policy.getLostItemFeeFixed());
            pstmt.setBigDecimal(6, policy.getLostItemFeePercentage());
            pstmt.setInt(7, policy.getGracePeriodDays());
            pstmt.setInt(8, policy.getPolicyID());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteFinePolicy(int policyId) throws SQLException {
        // Consider dependencies (e.g., if any fines were levied under this policy, though not directly linked)
        String sql = "DELETE FROM FinePolicies WHERE PolicyID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, policyId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public void ensureDefaultFinePoliciesExist() throws SQLException {
        if (itemTypeDAO == null) this.itemTypeDAO = new ItemTypeDAO(); // Ensure DAO is available

        // Ensure ItemTypes exist first (ItemTypeDAO should handle this)
        itemTypeDAO.ensureDefaultItemTypesExist();

        // General Policy (if no item-specific policy found)
        Optional<FinePolicy> generalPolicyOpt = getFinePolicyByName("Default General Policy");
        if (!generalPolicyOpt.isPresent()) {
            FinePolicy generalPolicy = new FinePolicy();
            generalPolicy.setPolicyName("Default General Policy");
            generalPolicy.setOverdueFinePerDay(new java.math.BigDecimal("0.25"));
            generalPolicy.setMaxFineAmount(new java.math.BigDecimal("10.00"));
            generalPolicy.setLostItemFeePercentage(new java.math.BigDecimal("1.0")); // 100% of item cost
            generalPolicy.setGracePeriodDays(2);
            createFinePolicy(generalPolicy);
            System.out.println("Created default general fine policy.");
        }

        // DVD Policy
        Optional<ItemType> dvdTypeOpt = itemTypeDAO.getItemTypeByName("DVD/Blu-ray");
        if (dvdTypeOpt.isPresent()) {
            Optional<FinePolicy> dvdPolicyOpt = getFinePolicyByItemTypeId(dvdTypeOpt.get().getItemTypeID());
            if (!dvdPolicyOpt.isPresent()) {
                 FinePolicy dvdPolicy = new FinePolicy();
                 dvdPolicy.setItemTypeID(dvdTypeOpt.get().getItemTypeID());
                 dvdPolicy.setPolicyName("DVD/Blu-ray Policy");
                 dvdPolicy.setOverdueFinePerDay(new java.math.BigDecimal("1.00"));
                 dvdPolicy.setMaxFineAmount(new java.math.BigDecimal("15.00"));
                 dvdPolicy.setLostItemFeePercentage(new java.math.BigDecimal("1.0"));
                 dvdPolicy.setGracePeriodDays(0);
                 createFinePolicy(dvdPolicy);
                 System.out.println("Created default DVD/Blu-ray fine policy.");
            }
        }
    }


    private FinePolicy mapRowToFinePolicy(ResultSet rs) throws SQLException {
        FinePolicy policy = new FinePolicy();
        policy.setPolicyID(rs.getInt("PolicyID"));
        policy.setItemTypeID(rs.getObject("ItemTypeID", Integer.class)); // Handles NULL
        policy.setPolicyName(rs.getString("PolicyName"));
        policy.setOverdueFinePerDay(rs.getBigDecimal("OverdueFinePerDay"));
        policy.setMaxFineAmount(rs.getBigDecimal("MaxFineAmount"));
        policy.setLostItemFeeFixed(rs.getBigDecimal("LostItemFeeFixed"));
        policy.setLostItemFeePercentage(rs.getBigDecimal("LostItemFeePercentage"));
        policy.setGracePeriodDays(rs.getInt("GracePeriodDays"));
        return policy;
    }

    private FinePolicy mapRowToFinePolicyWithDetails(ResultSet rs) throws SQLException {
        FinePolicy policy = mapRowToFinePolicy(rs);
        if (policy.getItemTypeID() != null) {
            ItemType itemType = new ItemType();
            itemType.setItemTypeID(policy.getItemTypeID());
            itemType.setTypeName(rs.getString("ItemTypeName")); // Joined column
            itemType.setLoanable(rs.getBoolean("IsLoanable")); // Joined column
            policy.setItemType(itemType);
        }
        return policy;
    }
}
