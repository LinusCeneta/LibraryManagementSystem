package dao;

import model.Supplier;
import utils.DBConnection;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SupplierDAO {
    private static final Logger logger = Logger.getLogger(SupplierDAO.class.getName());
    
    // SQL Constants
    private static final String TABLE_NAME = "Supplier";
    private static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME + 
        " (name, contact_person, address, phone, email, payment_terms) " +
        "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE " + TABLE_NAME + 
        " SET name = ?, contact_person = ?, address = ?, phone = ?, " +
        "email = ?, payment_terms = ? WHERE supplier_id = ?";
    private static final String DELETE_SQL = "DELETE FROM " + TABLE_NAME + " WHERE supplier_id = ?";
    private static final String FIND_BY_ID_SQL = "SELECT * FROM " + TABLE_NAME + " WHERE supplier_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM " + TABLE_NAME;
    private static final String EXISTS_SQL = "SELECT 1 FROM " + TABLE_NAME + " WHERE supplier_id = ?";
    private static final String COUNT_SQL = "SELECT COUNT(*) FROM " + TABLE_NAME;

    // Core CRUD Operations
    public boolean create(Supplier supplier) throws SQLException {
        if (supplier == null || isNullOrBlank(supplier.getName())) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                setSupplierParameters(ps, supplier);
                
                if (ps.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        supplier.setSupplierId(rs.getInt(1));
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error creating supplier", e);
                throw e;
            }
        }
    }

    public Optional<Supplier> findById(int id) throws SQLException {
        if (id <= 0) {
            return Optional.empty();
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapResultSetToSupplier(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding supplier by ID: " + id, e);
            throw e;
        }
    }

    public List<Supplier> findAll() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(true); // Critical fix for read operations
            
            try (PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
            return Collections.unmodifiableList(suppliers);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving all suppliers", e);
            throw e;
        }
    }

    public boolean update(Supplier supplier) throws SQLException {
        if (supplier == null || supplier.getSupplierId() <= 0) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                setSupplierParameters(ps, supplier);
                ps.setInt(7, supplier.getSupplierId());
                
                if (ps.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error updating supplier ID: " + supplier.getSupplierId(), e);
                throw e;
            }
        }
    }

    public boolean delete(int id) throws SQLException {
        if (id <= 0) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
                ps.setInt(1, id);
                
                if (ps.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                logger.log(Level.SEVERE, "Error deleting supplier ID: " + id, e);
                throw e;
            }
        }
    }

    // Additional Operations
    public boolean exists(int id) throws SQLException {
        if (id <= 0) {
            return false;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_SQL)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking supplier existence", e);
            throw e;
        }
    }

    public int count() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(COUNT_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting suppliers", e);
            throw e;
        }
    }

    public List<Supplier> findByName(String name) throws SQLException {
        if (isNullOrBlank(name)) {
            return Collections.emptyList();
        }

        String searchSql = FIND_ALL_SQL + " WHERE LOWER(name) LIKE LOWER(?)";
        List<Supplier> suppliers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(searchSql)) {
            
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
            return Collections.unmodifiableList(suppliers);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching suppliers by name", e);
            throw e;
        }
    }

    public List<Supplier> findAll(int offset, int limit) throws SQLException {
        if (offset < 0 || limit <= 0) {
            return Collections.emptyList();
        }

        String paginatedSql = FIND_ALL_SQL + " LIMIT ? OFFSET ?";
        List<Supplier> suppliers = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(paginatedSql)) {
            
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
            return Collections.unmodifiableList(suppliers);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving paginated suppliers", e);
            throw e;
        }
    }

    // Utility Methods
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        return new Supplier(
            rs.getInt("supplier_id"),
            rs.getString("name"),
            rs.getString("contact_person"),
            rs.getString("address"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("payment_terms")
        );
    }

    private void setSupplierParameters(PreparedStatement ps, Supplier supplier) throws SQLException {
        ps.setString(1, supplier.getName());
        ps.setString(2, supplier.getContactPerson());
        ps.setString(3, supplier.getAddress());
        ps.setString(4, supplier.getPhone());
        ps.setString(5, supplier.getEmail());
        ps.setString(6, supplier.getPaymentTerms());
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}