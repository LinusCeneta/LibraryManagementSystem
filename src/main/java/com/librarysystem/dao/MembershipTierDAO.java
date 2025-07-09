package com.librarysystem.dao;

import com.librarysystem.model.MembershipTier;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MembershipTierDAO {

    public MembershipTier createMembershipTier(MembershipTier tier) throws SQLException {
        String sql = "INSERT INTO MembershipTiers (TierName, BorrowingLimit, LoanDurationDays, RenewalLimit) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, tier.getTierName());
            pstmt.setInt(2, tier.getBorrowingLimit());
            pstmt.setInt(3, tier.getLoanDurationDays());
            pstmt.setInt(4, tier.getRenewalLimit());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tier.setMembershipTierID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating membership tier failed, no ID obtained.");
                }
            }
            return tier;
        }
    }

    public Optional<MembershipTier> getMembershipTierById(int tierId) throws SQLException {
        String sql = "SELECT * FROM MembershipTiers WHERE MembershipTierID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToMembershipTier(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<MembershipTier> getMembershipTierByName(String tierName) throws SQLException {
        String sql = "SELECT * FROM MembershipTiers WHERE TierName = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tierName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToMembershipTier(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<MembershipTier> getAllMembershipTiers() throws SQLException {
        List<MembershipTier> tiers = new ArrayList<>();
        String sql = "SELECT * FROM MembershipTiers ORDER BY TierName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tiers.add(mapRowToMembershipTier(rs));
            }
        }
        return tiers;
    }

    public boolean updateMembershipTier(MembershipTier tier) throws SQLException {
        String sql = "UPDATE MembershipTiers SET TierName = ?, BorrowingLimit = ?, LoanDurationDays = ?, RenewalLimit = ? WHERE MembershipTierID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tier.getTierName());
            pstmt.setInt(2, tier.getBorrowingLimit());
            pstmt.setInt(3, tier.getLoanDurationDays());
            pstmt.setInt(4, tier.getRenewalLimit());
            pstmt.setInt(5, tier.getMembershipTierID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMembershipTier(int tierId) throws SQLException {
        // Consider checking if any users are assigned this tier before deletion
        String sql = "DELETE FROM MembershipTiers WHERE MembershipTierID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tierId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public void ensureDefaultTiersExist() throws SQLException {
        // Example default tiers
        MembershipTier adult = new MembershipTier("Adult Regular", 10, 21, 2);
        MembershipTier student = new MembershipTier("Student", 15, 28, 3);
        MembershipTier senior = new MembershipTier("Senior", 8, 28, 2);
        MembershipTier child = new MembershipTier("Child", 5, 14, 1);
        MembershipTier staffInternal = new MembershipTier("Staff Internal", 20, 60, 5);

        MembershipTier[] defaultTiers = {adult, student, senior, child, staffInternal};

        for (MembershipTier tier : defaultTiers) {
            if (!getMembershipTierByName(tier.getTierName()).isPresent()) {
                createMembershipTier(tier);
                System.out.println("Created default membership tier: " + tier.getTierName());
            }
        }
    }


    private MembershipTier mapRowToMembershipTier(ResultSet rs) throws SQLException {
        MembershipTier tier = new MembershipTier();
        tier.setMembershipTierID(rs.getInt("MembershipTierID"));
        tier.setTierName(rs.getString("TierName"));
        tier.setBorrowingLimit(rs.getInt("BorrowingLimit"));
        tier.setLoanDurationDays(rs.getInt("LoanDurationDays"));
        tier.setRenewalLimit(rs.getInt("RenewalLimit"));
        return tier;
    }
}
