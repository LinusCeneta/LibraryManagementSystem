package dao;

import model.Condition;
import model.Copy;
import model.Status;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CopyDAO {
    private static final Logger logger = Logger.getLogger(CopyDAO.class.getName());
    
    private static final String SAVE_SQL = "INSERT INTO Copies (copy_id, isbn, acquisition_date, cost, condition, location, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM Copies WHERE copy_id = ?";
    private static final String UPDATE_SQL = "UPDATE Copies SET isbn = ?, acquisition_date = ?, cost = ?, condition = ?, location = ?, status = ? WHERE copy_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Copies WHERE copy_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM Copies";
    private static final String FIND_BY_STATUS_SQL = "SELECT * FROM Copies WHERE status = ?";

    public boolean save(Copy copy) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            String sql = "INSERT INTO copies (copy_id, isbn, acquisition_date, cost, condition, location, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, copy.getCopyId());
            stmt.setString(2, copy.getIsbn());
            stmt.setDate(3, new java.sql.Date(copy.getAcquisitionDate().getTime()));
            stmt.setDouble(4, copy.getCost());
            stmt.setString(5, copy.getCondition().name());
            stmt.setString(6, copy.getLocation());
            stmt.setString(7, copy.getStatus().name());
            
            int rowsAffected = stmt.executeUpdate();
            conn.commit(); // Commit transaction
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error rolling back transaction", ex);
            }
            logger.log(Level.SEVERE, "Error saving copy", e);
            return false;
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing resources", e);
            }
        }
    }

    public Copy findById(String copyId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(true); // Read-only operation
            ps = conn.prepareStatement(FIND_BY_ID_SQL);
            ps.setString(1, copyId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapRowToCopy(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding copy by ID: " + copyId, e);
            return null;
        } finally {
            closeResources(conn, ps, rs);
        }
    }

    public boolean update(Copy copy) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(UPDATE_SQL);
            
            ps.setString(1, copy.getIsbn());
            ps.setDate(2, new java.sql.Date(copy.getAcquisitionDate().getTime()));
            ps.setDouble(3, copy.getCost());
            ps.setString(4, copy.getCondition().name());
            ps.setString(5, copy.getLocation());
            ps.setString(6, copy.getStatus().name());
            ps.setString(7, copy.getCopyId());
            
            int rowsAffected = ps.executeUpdate();
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            rollback(conn);
            logger.log(Level.SEVERE, "Error updating copy with ID: " + copy.getCopyId(), e);
            return false;
        } finally {
            closeResources(conn, ps, null);
        }
    }

    public boolean delete(String copyId) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(DELETE_SQL);
            ps.setString(1, copyId);
            
            int rowsAffected = ps.executeUpdate();
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            rollback(conn);
            logger.log(Level.SEVERE, "Error deleting copy with ID: " + copyId, e);
            return false;
        } finally {
            closeResources(conn, ps, null);
        }
    }

    public List<Copy> getAllCopies() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Copy> copies = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(true); // Read-only operation
            ps = conn.prepareStatement(FIND_ALL_SQL);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                copies.add(mapRowToCopy(rs));
            }
            return copies;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting all copies", e);
            return copies;
        } finally {
            closeResources(conn, ps, rs);
        }
    }
    
    public List<Copy> findByStatus(Status status) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Copy> copies = new ArrayList<>();
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(true); // Read-only operation
            ps = conn.prepareStatement(FIND_BY_STATUS_SQL);
            ps.setString(1, status.name());
            rs = ps.executeQuery();
            
            while (rs.next()) {
                copies.add(mapRowToCopy(rs));
            }
            return copies;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding copies by status: " + status, e);
            return copies;
        } finally {
            closeResources(conn, ps, rs);
        }
    }

    private Copy mapRowToCopy(ResultSet rs) throws SQLException {
        Copy copy = new Copy();
        copy.setCopyId(rs.getString("copy_id"));
        copy.setIsbn(rs.getString("isbn"));
        copy.setAcquisitionDate(rs.getDate("acquisition_date"));
        copy.setCost(rs.getDouble("cost"));
        copy.setCondition(Condition.valueOf(rs.getString("condition")));
        copy.setLocation(rs.getString("location"));
        copy.setStatus(Status.valueOf(rs.getString("status")));
        return copy;
    }

    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) {
                if (!conn.getAutoCommit()) {
                    conn.setAutoCommit(true); // Reset before closing
                }
                conn.close();
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error closing resources", e);
        }
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error during rollback", e);
            }
        }
    }
}