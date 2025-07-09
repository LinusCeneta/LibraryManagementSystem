package dao;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Member;
import utils.DBConnection;
import utils.SecurityUtils;

public class MemberDAO {
    private static final Logger logger = Logger.getLogger(MemberDAO.class.getName());

    private static final String REGISTER_SQL = """
        INSERT INTO members (name, email, phone, member_id, status, membership_type) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;
    
    private static final String SEARCH_SQL = """
        SELECT * FROM Members 
        WHERE (name LIKE ? OR email LIKE ? OR phone LIKE ? OR member_id LIKE ?)
        AND status = 'Active'
        """;
    
    private static final String GET_BY_ID_SQL = "SELECT * FROM Members WHERE member_id = ?";
    private static final String RENEW_SQL = "UPDATE Members SET status = 'Active' WHERE id = ?";

    public boolean registerMember(Member member) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(REGISTER_SQL);
            
            // Set only the essential parameters
            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getEmail());
            pstmt.setString(3, member.getPhone());
            pstmt.setString(4, member.getMemberId());
            pstmt.setString(5, member.getStatus());
            pstmt.setString(6, member.getMembershipType());
            
            int rowsAffected = pstmt.executeUpdate();
            conn.commit(); // Explicit commit
            
            logger.log(Level.INFO, "Registered member {0}. Rows affected: {1}", 
                new Object[]{member.getName(), rowsAffected});
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Rollback failed", ex);
            }
            logger.log(Level.SEVERE, "Error registering member", e);
            return false;
        } finally {
            DBConnection.closeResources(conn, pstmt, null);
        }
    }

    public Member getMemberById(String memberId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(true); // Ensure auto-commit is on for read operations
            
            stmt = conn.prepareStatement(GET_BY_ID_SQL);
            stmt.setString(1, memberId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapRowToMember(rs);
            }
            return null;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving member with ID: " + memberId, e);
            throw new RuntimeException("Database error retrieving member", e);
        } finally {
            // Close resources in reverse order
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing resources", e);
            }
        }
    }

    public List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return members;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_SQL)) {
            
            String searchPattern = "%" + sanitizeSearchInput(keyword) + "%";
            for (int i = 1; i <= 4; i++) {
                stmt.setString(i, searchPattern);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapRowToMember(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Search failed for keyword: " + keyword, e);
            throw new RuntimeException("Database error during search", e);
        }
        return members;
    }

    public boolean renewMembership(int memberId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(RENEW_SQL)) {
            
            stmt.setInt(1, memberId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error renewing membership for ID: " + memberId, e);
            throw new RuntimeException("Database error renewing membership", e);
        }
    }

    // ===== Helper Methods =====
    private String sanitizeSearchInput(String input) {
        if (input != null) {
            return input.trim().replace("%", "\\%").replace("_", "\\_");
        }
        return "";
    }

    private Member mapRowToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setMemberId(rs.getString("member_id"));
        member.setMembershipType(rs.getString("membership_type"));
        member.setStatus(rs.getString("status"));
        return member;
    }
}