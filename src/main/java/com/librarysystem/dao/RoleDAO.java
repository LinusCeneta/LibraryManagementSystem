package com.librarysystem.dao;

import com.librarysystem.model.Role;
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleDAO {

    public Role createRole(Role role) throws SQLException {
        String sql = "INSERT INTO Roles (RoleName) VALUES (?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, role.getRoleName());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    role.setRoleID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating role failed, no ID obtained.");
                }
            }
            return role;
        }
    }

    public Optional<Role> getRoleById(int roleId) throws SQLException {
        String sql = "SELECT * FROM Roles WHERE RoleID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToRole(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Role> getRoleByName(String roleName) throws SQLException {
        String sql = "SELECT * FROM Roles WHERE RoleName = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roleName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToRole(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Role> getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM Roles ORDER BY RoleName";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                roles.add(mapRowToRole(rs));
            }
        }
        return roles;
    }

    public boolean updateRole(Role role) throws SQLException {
        String sql = "UPDATE Roles SET RoleName = ? WHERE RoleID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role.getRoleName());
            pstmt.setInt(2, role.getRoleID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteRole(int roleId) throws SQLException {
        // Consider checking if any users are assigned this role before deletion
        // Or handle via database constraints (e.g., ON DELETE RESTRICT)
        String sql = "DELETE FROM Roles WHERE RoleID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roleId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Helper method to ensure default roles exist in the database.
     * Call this during application startup.
     */
    public void ensureDefaultRolesExist() throws SQLException {
        String[] defaultRoles = {"ROLE_ADMIN", "ROLE_STAFF", "ROLE_MEMBER"};
        for (String roleName : defaultRoles) {
            if (!getRoleByName(roleName).isPresent()) {
                createRole(new Role(0, roleName)); // ID will be auto-generated
                System.out.println("Created default role: " + roleName);
            }
        }
    }


    private Role mapRowToRole(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setRoleID(rs.getInt("RoleID"));
        role.setRoleName(rs.getString("RoleName"));
        return role;
    }
}
