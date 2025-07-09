package com.librarysystem.dao;

import com.librarysystem.model.User;
import com.librarysystem.model.Role; // Assuming Role model exists
import com.librarysystem.model.UserProfile; // Assuming UserProfile model exists
import com.librarysystem.util.DBConnectionUtil;
import com.librarysystem.util.PasswordUtil; // For password hashing

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO {

    private RoleDAO roleDAO; // To fetch Role objects
    private UserProfileDAO userProfileDAO; // To manage associated profiles

    public UserDAO() {
        this.roleDAO = new RoleDAO(); // Initialize RoleDAO
        this.userProfileDAO = new UserProfileDAO(); // Initialize UserProfileDAO
    }

    // Constructor for injecting DAOs (useful for testing or DI frameworks)
    // Updated to handle potentially null DAOs if default constructor is used by other DAOs
    public UserDAO(RoleDAO roleDAO, UserProfileDAO userProfileDAO) {
        this.roleDAO = (roleDAO != null) ? roleDAO : new RoleDAO();
        this.userProfileDAO = (userProfileDAO != null) ? userProfileDAO : new UserProfileDAO();
    }

    public User createUser(User user, String plainPassword) throws SQLException {
        // Ensure Role object is populated if only RoleID was set
        if (user.getRole() == null && user.getRoleID() > 0) {
            Optional<Role> roleOpt = roleDAO.getRoleById(user.getRoleID());
            user.setRole(roleOpt.orElseThrow(() -> new SQLException("Role not found for RoleID: " + user.getRoleID())));
        } else if (user.getRole() == null || user.getRole().getRoleID() == 0) {
             throw new SQLException("User role must be set before creating user.");
        }

        if (!PasswordUtil.isPasswordStrong(plainPassword)) {
            // This check should ideally be in the service/servlet layer before calling DAO,
            // but adding here for defense in depth or if DAO is called directly elsewhere.
            throw new SQLException("Password does not meet strength requirements. " + PasswordUtil.getPasswordStrengthCriteriaMessage());
        }
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        String sql = "INSERT INTO Users (Username, PasswordHash, Email, FirstName, LastName, RoleID, IsActive, ProfilePhotoURL, DateRegistered) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());
            pstmt.setInt(6, user.getRole().getRoleID()); // Use RoleID from Role object
            pstmt.setBoolean(7, user.isActive());
            pstmt.setString(8, user.getProfilePhotoURL());
            pstmt.setTimestamp(9, new Timestamp(System.currentTimeMillis())); // DateRegistered

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            // If UserProfile data is part of the User object, save it
            if (user.getUserProfile() != null) {
                UserProfile profile = user.getUserProfile();
                profile.setUserID(user.getUserID()); // Link profile to this new user
                userProfileDAO.createUserProfile(profile); // This uses its own connection, or pass conn
            }

            conn.commit();
            return user;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Optional<User> findUserByUsername(String username) throws SQLException {
        String sql = "SELECT u.*, r.RoleName FROM Users u JOIN Roles r ON u.RoleID = r.RoleID WHERE u.Username = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUserWithDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findUserByEmail(String email) throws SQLException {
        String sql = "SELECT u.*, r.RoleName FROM Users u JOIN Roles r ON u.RoleID = r.RoleID WHERE u.Email = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUserWithDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findUserById(int userId) throws SQLException {
        String sql = "SELECT u.*, r.RoleName FROM Users u JOIN Roles r ON u.RoleID = r.RoleID WHERE u.UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUserWithDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<User> findAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.*, r.RoleName FROM Users u JOIN Roles r ON u.RoleID = r.RoleID ORDER BY u.LastName, u.FirstName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapRowToUserWithDetails(rs));
            }
        }
        return users;
    }

    public boolean updateUserCoreDetails(User user) throws SQLException {
        // Does not update password or role here, use dedicated methods for those.
        String sql = "UPDATE Users SET Username = ?, Email = ?, FirstName = ?, LastName = ?, IsActive = ?, ProfilePhotoURL = ? WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setBoolean(5, user.isActive());
            pstmt.setString(6, user.getProfilePhotoURL());
            pstmt.setInt(7, user.getUserID());

            int affectedRows = pstmt.executeUpdate();

            // Update associated UserProfile if it exists on the User object
            if (affectedRows > 0 && user.getUserProfile() != null) {
                UserProfile profile = user.getUserProfile();
                profile.setUserID(user.getUserID()); // Ensure UserID is set
                // Check if profile exists, then update or create
                if (userProfileDAO.getUserProfileByUserId(user.getUserID()).isPresent()) {
                    userProfileDAO.updateUserProfile(profile);
                } else {
                    userProfileDAO.createUserProfile(profile);
                }
            }
            return affectedRows > 0;
        }
    }

    public boolean updateUserPassword(int userId, String newPlainPassword) throws SQLException {
        if (!PasswordUtil.isPasswordStrong(newPlainPassword)) {
            // Or throw a custom exception like WeakPasswordException
            throw new SQLException("Password does not meet strength requirements. " + PasswordUtil.getPasswordStrengthCriteriaMessage());
        }
        String newHashedPassword = PasswordUtil.hashPassword(newPlainPassword);
        String sql = "UPDATE Users SET PasswordHash = ? WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newHashedPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateUserRole(int userId, int newRoleId) throws SQLException {
        // Validate newRoleId exists
        roleDAO.getRoleById(newRoleId).orElseThrow(() -> new SQLException("Role not found for RoleID: " + newRoleId));

        String sql = "UPDATE Users SET RoleID = ? WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newRoleId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        // UserProfile and PasswordResetTokens should be handled by ON DELETE CASCADE
        // AuditLog entries have ON DELETE SET NULL for UserID
        String sql = "DELETE FROM Users WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean recordLogin(int userId) throws SQLException {
        String sql = "UPDATE Users SET LastLoginDate = ? WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public Optional<String> getPasswordHash(int userId) throws SQLException {
        String sql = "SELECT PasswordHash FROM Users WHERE UserID = ?";
         try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("PasswordHash"));
                }
            }
        }
        return Optional.empty();
    }


    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setUsername(rs.getString("Username"));
        user.setPasswordHash(rs.getString("PasswordHash")); // Usually not needed in User object after auth
        user.setEmail(rs.getString("Email"));
        user.setFirstName(rs.getString("FirstName"));
        user.setLastName(rs.getString("LastName"));
        user.setRoleID(rs.getInt("RoleID")); // Store RoleID for internal use
        user.setActive(rs.getBoolean("IsActive"));
        user.setProfilePhotoURL(rs.getString("ProfilePhotoURL"));
        user.setDateRegistered(rs.getTimestamp("DateRegistered"));
        user.setLastLoginDate(rs.getTimestamp("LastLoginDate"));

        // Map new circulation-related fields if they are in the Users table
        // These might require ALTER TABLE Users from circulation_schema.sql to be applied first
        if (hasColumn(rs, "MembershipTierID")) {
            user.setMembershipTierID(rs.getObject("MembershipTierID", Integer.class));
        }
        if (hasColumn(rs, "MembershipStatus")) {
            user.setMembershipStatus(rs.getString("MembershipStatus"));
        }
        if (hasColumn(rs, "CurrentFineBalance")) {
            user.setCurrentFineBalance(rs.getBigDecimal("CurrentFineBalance"));
        }
        return user;
    }

    private User mapRowToUserWithDetails(ResultSet rs) throws SQLException {
        User user = mapRowToUser(rs);

        // Populate Role object
        Role role = new Role();
        role.setRoleID(user.getRoleID()); // Use RoleID from user object
        // RoleName might be joined or fetched separately. Assuming joined for now.
        if (hasColumn(rs, "RoleName")) {
             role.setRoleName(rs.getString("RoleName"));
        } else {
            // Fallback: fetch role if not joined (less efficient for lists)
            if (roleDAO == null) this.roleDAO = new RoleDAO(); // Ensure roleDAO is initialized
            roleDAO.getRoleById(user.getRoleID()).ifPresent(r -> role.setRoleName(r.getRoleName()));
        }
        user.setRole(role);

        // Populate MembershipTier object if MembershipTierID exists
        if (user.getMembershipTierID() != null) {
            MembershipTier tier = new MembershipTier();
            tier.setMembershipTierID(user.getMembershipTierID());
            // If Tier details are joined in the query that called this method:
            if (hasColumn(rs, "TierName")) tier.setTierName(rs.getString("TierName"));
            if (hasColumn(rs, "BorrowingLimit")) tier.setBorrowingLimit(rs.getInt("BorrowingLimit"));
            if (hasColumn(rs, "LoanDurationDays")) tier.setLoanDurationDays(rs.getInt("LoanDurationDays"));
            if (hasColumn(rs, "RenewalLimit")) tier.setRenewalLimit(rs.getInt("RenewalLimit"));

            // If TierName is still null (not joined), fetch the tier separately
            if (tier.getTierName() == null) {
                MembershipTierDAO tierDAO = new MembershipTierDAO(); // Or inject if available
                tierDAO.getMembershipTierById(user.getMembershipTierID()).ifPresent(user::setMembershipTier);
            } else {
                 user.setMembershipTier(tier);
            }
        }

        // UserProfile is typically fetched on demand (e.g., in UserServlet when viewing profile)
        // to avoid N+1 queries on lists of users.
        // userProfileDAO.getUserProfileByUserId(user.getUserID()).ifPresent(user::setUserProfile);

        return user;
    }

    // Helper method to check if a ResultSet contains a specific column
    // This is useful when columns might be optional (e.g. from ALTER TABLE) or from joins
    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    // Methods to update new User fields (MembershipStatus, CurrentFineBalance)
    public boolean updateMembershipStatus(int userId, String status) throws SQLException {
        String sql = "UPDATE Users SET MembershipStatus = ? WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateUserFineBalance(int userId, BigDecimal newFineBalance) throws SQLException {
        String sql = "UPDATE Users SET CurrentFineBalance = ? WHERE UserID = ?";
         try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newFineBalance);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean assignMembershipTier(int userId, int tierId) throws SQLException {
        // Optional: Validate tierId exists using MembershipTierDAO first
        String sql = "UPDATE Users SET MembershipTierID = ? WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tierId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // --- Reporting Methods for UserDAO ---

    public List<com.librarysystem.dto.ActiveInactiveMemberDTO> getActiveVsInactiveMembers(int periodInMonths) throws SQLException {
        List<com.librarysystem.dto.ActiveInactiveMemberDTO> result = new ArrayList<>();

        // Calculate the date threshold for inactivity
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, -periodInMonths);
        Timestamp activityThresholdDate = new Timestamp(cal.getTimeInMillis());

        // Query to count active members (those with loans within the period)
        // This definition of "active" is based on loan activity.
        // Another definition could be based on Users.LastLoginDate.
        // Or Users.MembershipStatus if that's diligently updated.
        // For this example, active means having a loan issued after the threshold date.
        String activeSql = "SELECT COUNT(DISTINCT u.UserID) AS MemberCount " +
                           "FROM Users u JOIN Loans l ON u.UserID = l.MemberID " +
                           "WHERE u.RoleID = (SELECT RoleID FROM Roles WHERE RoleName = 'ROLE_MEMBER') " + // Assuming members have ROLE_MEMBER
                           "AND l.IssueDate >= ?";

        long activeCount = 0;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmtActive = conn.prepareStatement(activeSql)) {
            pstmtActive.setTimestamp(1, activityThresholdDate);
            try (ResultSet rsActive = pstmtActive.executeQuery()) {
                if (rsActive.next()) {
                    activeCount = rsActive.getLong("MemberCount");
                }
            }
        }
        result.add(new com.librarysystem.dto.ActiveInactiveMemberDTO("Active (Loan in last " + periodInMonths + " months)", activeCount));

        // Query to count total members
        String totalMembersSql = "SELECT COUNT(u.UserID) AS TotalMemberCount " +
                                 "FROM Users u " +
                                 "WHERE u.RoleID = (SELECT RoleID FROM Roles WHERE RoleName = 'ROLE_MEMBER')";
        long totalMemberCount = 0;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmtTotal = conn.prepareStatement(totalMembersSql)) {
            try (ResultSet rsTotal = pstmtTotal.executeQuery()) {
                if (rsTotal.next()) {
                    totalMemberCount = rsTotal.getLong("TotalMemberCount");
                }
            }
        }

        long inactiveCount = totalMemberCount - activeCount;
        result.add(new com.librarysystem.dto.ActiveInactiveMemberDTO("Inactive (No loan in last " + periodInMonths + " months)", inactiveCount));

        return result;
    }
}
