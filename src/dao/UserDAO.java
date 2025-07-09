package dao;


import model.User;
import utils.DBConnection;
import utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

	public boolean registerUser(User user) {
	    String sql = "INSERT INTO Users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
	    Connection conn = null;
	    try {
	        conn = DBConnection.getConnection();
	        conn.setAutoCommit(false); // Start transaction
	        
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, user.getUsername());
	            stmt.setString(2, user.getPasswordHash());
	            stmt.setString(3, user.getEmail());
	            stmt.setString(4, user.getRole());
	            
	            int rows = stmt.executeUpdate();
	            conn.commit(); // Explicit commit
	            return rows > 0;
	        } catch (SQLException e) {
	            if (conn != null) {
	                conn.rollback(); // Explicit rollback
	            }
	            System.err.println("Error registering user: " + e.getMessage());
	            return false;
	        }
	    } catch (SQLException e) {
	        System.err.println("Database connection error: " + e.getMessage());
	        return false;
	    } finally {
	        if (conn != null) {
	            try {
	                conn.setAutoCommit(true); // Reset to default
	                conn.close(); // Explicit close
	            } catch (SQLException e) {
	                System.err.println("Error closing connection: " + e.getMessage());
	            }
	        }
	    }
	}

    public User findById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // Explicitly set auto-commit to true for read operations
            conn.setAutoCommit(true);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return extractUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + username);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int[] batchInsert(List<User> users) throws SQLException {
        String sql = "INSERT INTO Users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (User user : users) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPasswordHash());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getRole());
                stmt.addBatch();
            }
            int[] result = stmt.executeBatch();
            conn.commit();
            return result;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUser(User user) {
        String sql = "UPDATE Users SET email = ?, password_hash = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setInt(3, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE Users SET password_hash = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveResetToken(String username, String token) {
        String sql = "UPDATE Users SET reset_token = ?, reset_token_expiry = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            Timestamp expiry = new Timestamp(System.currentTimeMillis() + 3600 * 1000); // 1 hour from now
            stmt.setTimestamp(2, expiry);
            stmt.setString(3, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User findByResetToken(String token) {
        String sql = "SELECT * FROM Users WHERE reset_token = ? AND reset_token_expiry > NOW()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        try {
            user.setResetToken(rs.getString("reset_token"));
        } catch (SQLException ignored) {}
        try {
            user.setResetTokenExpiry(rs.getTimestamp("reset_token_expiry"));
        } catch (SQLException ignored) {}
        return user;
    }

    public User authenticateUser(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            return null;  // Early return for invalid input
        }

        String sql = "SELECT * FROM Users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = extractUser(rs);
                    String storedHash = user.getPasswordHash();
                    
                    // Verify the provided password against the stored hash
                    if (PasswordUtils.verifyPassword(password, storedHash)) {
                        return user;  // Authentication successful
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Authentication error for email: " + email);
            e.printStackTrace();
        }
        
        return null;  // Authentication failed
    }
    
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        User testUser = new User("testuser", PasswordUtils.hashPassword("testpass"), "test@email.com", "ROLE_MEMBER");

        if (dao.registerUser(testUser)) {
            System.out.println("✅ Test user registered.");
        } else {
            System.out.println("❌ Failed to register test user.");
        }
    }
    
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM Users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
