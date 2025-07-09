package com.librarysystem.dao;

import com.librarysystem.model.Copy;
import com.librarysystem.model.Book; // Assuming for book details
import com.librarysystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CopyDAO {

    // Create a new copy
    public Copy createCopy(Copy copy) throws SQLException {
        String sql = "INSERT INTO Copies (BookID, CopyBarcode, AcquisitionDate, Cost, Condition, Status, Location, ItemTypeID, CurrentLoanID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, copy.getBookID());
            pstmt.setString(2, copy.getCopyBarcode());
            if (copy.getAcquisitionDate() != null) {
                pstmt.setDate(3, copy.getAcquisitionDate());
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            pstmt.setDouble(4, copy.getCost());
            pstmt.setString(5, copy.getCondition());
            pstmt.setString(6, copy.getStatus() != null ? copy.getStatus() : "Available");
            pstmt.setString(7, copy.getLocation());
            // Assuming ItemTypeID is an Integer in the model and can be null if not set
            if (copy.getItemTypeID() != null) {
                pstmt.setInt(8, copy.getItemTypeID());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            // Assuming CurrentLoanID is a Long in the model and can be null
            if (copy.getCurrentLoanID() != null) {
                pstmt.setLong(9, copy.getCurrentLoanID());
            } else {
                pstmt.setNull(9, Types.BIGINT);
            }

            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    copy.setCopyID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating copy failed, no ID obtained.");
                }
            }
            return copy;
        }
    }

    // Get a copy by its ID
    public Optional<Copy> getCopyById(int copyId) throws SQLException {
        String sql = "SELECT c.*, b.Title as BookTitle, b.ISBN as BookISBN " + // Basic book info
                     "FROM Copies c JOIN Books b ON c.BookID = b.BookID WHERE c.CopyID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, copyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCopyWithBookDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    // Get a copy by its barcode
    public Optional<Copy> getCopyByBarcode(String barcode) throws SQLException {
        String sql = "SELECT c.*, b.Title as BookTitle, b.ISBN as BookISBN " +
                     "FROM Copies c JOIN Books b ON c.BookID = b.BookID WHERE c.CopyBarcode = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCopyWithBookDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    // Get all copies for a specific book ID
    public List<Copy> getCopiesByBookId(int bookId) throws SQLException {
        List<Copy> copies = new ArrayList<>();
        String sql = "SELECT * FROM Copies WHERE BookID = ? ORDER BY CopyBarcode";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    copies.add(mapRowToCopy(rs)); // Simpler mapping for lists
                }
            }
        }
        return copies;
    }

    // Get all copies (consider pagination for large datasets)
    public List<Copy> getAllCopies() throws SQLException {
        List<Copy> copies = new ArrayList<>();
        String sql = "SELECT c.*, b.Title as BookTitle, b.ISBN as BookISBN FROM Copies c JOIN Books b ON c.BookID = b.BookID ORDER BY c.BookID, c.CopyBarcode";
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                copies.add(mapRowToCopyWithBookDetails(rs));
            }
        }
        return copies;
    }


    // Update an existing copy's details
    public boolean updateCopy(Copy copy) throws SQLException {
        String sql = "UPDATE Copies SET BookID = ?, CopyBarcode = ?, AcquisitionDate = ?, Cost = ?, Condition = ?, Status = ?, Location = ?, ItemTypeID = ?, CurrentLoanID = ? WHERE CopyID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, copy.getBookID());
            pstmt.setString(2, copy.getCopyBarcode());
            pstmt.setDate(3, copy.getAcquisitionDate());
            pstmt.setDouble(4, copy.getCost());
            pstmt.setString(5, copy.getCondition());
            pstmt.setString(6, copy.getStatus());
            pstmt.setString(7, copy.getLocation());
            if (copy.getItemTypeID() != null) pstmt.setInt(8, copy.getItemTypeID()); else pstmt.setNull(8, Types.INTEGER);
            if (copy.getCurrentLoanID() != null) pstmt.setLong(9, copy.getCurrentLoanID()); else pstmt.setNull(9, Types.BIGINT);
            pstmt.setInt(10, copy.getCopyID());
            return pstmt.executeUpdate() > 0;
        }
    }

    // Update a copy's status
    public boolean updateCopyStatus(int copyId, String status) throws SQLException {
        String sql = "UPDATE Copies SET Status = ? WHERE CopyID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, copyId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Update a copy's current loan ID (e.g., when checked out or returned)
    public boolean updateCopyCurrentLoan(int copyId, Long loanId) throws SQLException {
        String sql = "UPDATE Copies SET CurrentLoanID = ? WHERE CopyID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (loanId != null) {
                pstmt.setLong(1, loanId);
            } else {
                pstmt.setNull(1, Types.BIGINT);
            }
            pstmt.setInt(2, copyId);
            return pstmt.executeUpdate() > 0;
        }
    }


    // Delete a copy by its ID
    public boolean deleteCopy(int copyId) throws SQLException {
        // Consider implications: cannot delete if on loan or has active holds tied to this specific copy.
        // For now, simple delete.
        String sql = "DELETE FROM Copies WHERE CopyID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, copyId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public int getAvailableCopyCountForBook(int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Copies WHERE BookID = ? AND Status = 'Available'";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public Optional<Copy> findFirstAvailableCopyForBook(int bookId) throws SQLException {
        String sql = "SELECT * FROM Copies WHERE BookID = ? AND Status = 'Available' ORDER BY CopyID LIMIT 1"; // Derby specific LIMIT
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCopy(rs));
                }
            }
        }
        return Optional.empty();
    }


    private Copy mapRowToCopy(ResultSet rs) throws SQLException {
        Copy copy = new Copy();
        copy.setCopyID(rs.getInt("CopyID"));
        copy.setBookID(rs.getInt("BookID"));
        copy.setCopyBarcode(rs.getString("CopyBarcode"));
        copy.setAcquisitionDate(rs.getDate("AcquisitionDate"));
        copy.setCost(rs.getDouble("Cost"));
        copy.setCondition(rs.getString("Condition"));
        copy.setStatus(rs.getString("Status"));
        copy.setLocation(rs.getString("Location"));
        copy.setItemTypeID(rs.getObject("ItemTypeID", Integer.class));
        copy.setCurrentLoanID(rs.getObject("CurrentLoanID", Long.class));
        return copy;
    }

    private Copy mapRowToCopyWithBookDetails(ResultSet rs) throws SQLException {
        Copy copy = mapRowToCopy(rs);
        Book book = new Book(); // Assuming a basic Book model
        book.setBookID(copy.getBookID());
        book.setTitle(rs.getString("BookTitle"));
        book.setIsbn(rs.getString("BookISBN"));
        copy.setBook(book); // Assuming Copy model has a setBook(Book book) method
        return copy;
    }

    // Getter for ItemTypeID from Copy model
    // This static inner class was a placeholder.
    // It should be removed if com.librarysystem.model.Copy is now correctly defined and used.
    // For now, I'm commenting it out to ensure the main model.Copy is preferred.
    /*
    public static class Copy {
        private int copyID;
        private int bookID;
        private String copyBarcode;
        private java.sql.Date acquisitionDate;
        private double cost;
        private String condition;
        private String status;
        private String location;
        private Integer itemTypeID; // Use Integer to allow null
        private Long currentLoanID; // Use Long to allow null
        private Book book;


        // Getters and Setters
        public int getCopyID() { return copyID; }
        public void setCopyID(int copyID) { this.copyID = copyID; }
        public int getBookID() { return bookID; }
        public void setBookID(int bookID) { this.bookID = bookID; }
        public String getCopyBarcode() { return copyBarcode; }
        public void setCopyBarcode(String copyBarcode) { this.copyBarcode = copyBarcode; }
        public java.sql.Date getAcquisitionDate() { return acquisitionDate; }
        public void setAcquisitionDate(java.sql.Date acquisitionDate) { this.acquisitionDate = acquisitionDate; }
        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Integer getItemTypeID() { return itemTypeID; }
        public void setItemTypeID(Integer itemTypeID) { this.itemTypeID = itemTypeID; }
        public Long getCurrentLoanID() { return currentLoanID; }
        public void setCurrentLoanID(Long currentLoanID) { this.currentLoanID = currentLoanID; }
        public Book getBook() { return book; }
        public void setBook(Book book) { this.book = book; }
    }
    */

    // Method to get available copies summary per branch for a book
    public Map<Integer, Integer> getAvailableCopiesCountPerLocation(int bookId) throws SQLException {
        Map<Integer, Integer> availabilityMap = new java.util.HashMap<>();
        String sql = "SELECT LocationID, COUNT(*) as AvailableCount FROM Copies " +
                     "WHERE BookID = ? AND Status = 'Available' GROUP BY LocationID";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    availabilityMap.put(rs.getInt("LocationID"), rs.getInt("AvailableCount"));
                }
            }
        }
        return availabilityMap;
    }
     public List<com.librarysystem.model.Copy> findCopiesByCriteria(int bookId, Integer locationId, String status) throws SQLException {
        List<com.librarysystem.model.Copy> copies = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT c.*, b.Title as BookTitle, b.ISBN as BookISBN FROM Copies c JOIN Books b ON c.BookID = b.BookID WHERE c.BookID = ?");
        if (locationId != null) {
            sqlBuilder.append(" AND c.LocationID = ?");
        }
        if (status != null && !status.isEmpty()) {
            sqlBuilder.append(" AND c.Status = ?");
        }
        sqlBuilder.append(" ORDER BY c.CopyBarcode");

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            int paramIndex = 1;
            pstmt.setInt(paramIndex++, bookId);
            if (locationId != null) {
                pstmt.setInt(paramIndex++, locationId);
            }
            if (status != null && !status.isEmpty()) {
                pstmt.setString(paramIndex++, status);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    copies.add(mapRowToCopyWithBookDetails(rs)); // Use the main model Copy
                }
            }
        }
        return copies;
    }

}
