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
    public UserDAO(RoleDAO roleDAO, UserProfileDAO userProfileDAO) {
        this.roleDAO = roleDAO;
        this.userProfileDAO = userProfileDAO;
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
        user.setRoleID(rs.getInt("RoleID")); // Store RoleID
        user.setActive(rs.getBoolean("IsActive"));
        user.setProfilePhotoURL(rs.getString("ProfilePhotoURL"));
        user.setDateRegistered(rs.getTimestamp("DateRegistered"));
        user.setLastLoginDate(rs.getTimestamp("LastLoginDate"));
        return user;
    }

    private User mapRowToUserWithDetails(ResultSet rs) throws SQLException {
        User user = mapRowToUser(rs);

        // Populate Role object
        Role role = new Role();
        role.setRoleID(rs.getInt("RoleID"));
        role.setRoleName(rs.getString("RoleName")); // Assumes RoleName is joined
        user.setRole(role);

        // Optionally, fetch and set UserProfile here if always needed
        // This makes an extra DB call per user if not joined; consider joining if performance is critical
        // For now, UserProfile is fetched on demand by services/servlets
        // userProfileDAO.getUserProfileByUserId(user.getUserID()).ifPresent(user::setUserProfile);

        return user;
    }
}
