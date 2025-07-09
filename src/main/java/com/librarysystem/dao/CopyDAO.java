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

    // --- Reporting Methods for CopyDAO ---

    public List<com.librarysystem.dto.NewAcquisitionDTO> getNewAcquisitions(Date startDate, Date endDate, Integer categoryId, Integer supplierId) throws SQLException {
        List<com.librarysystem.dto.NewAcquisitionDTO> acquisitions = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT cp.CopyID, bk.Title AS BookTitle, bk.ISBN, cp.CopyBarcode, cp.AcquisitionDate, cp.Cost, " +
            "cat.CategoryName, sup.SupplierName " +
            "FROM Copies cp " +
            "JOIN Books bk ON cp.BookID = bk.BookID " +
            "LEFT JOIN GoodsReceiptNoteItems grni ON grni.BookID = bk.BookID " + // This join might be tricky if a copy isn't directly from a GRN item, or if multiple GRNIs for one book
            "LEFT JOIN GoodsReceiptNotes grn ON grni.GRN_ID = grn.GRN_ID " + // And then to PO for supplier
            "LEFT JOIN PurchaseOrders po ON grn.PO_ID = po.PO_ID " +
            "LEFT JOIN Suppliers sup ON po.SupplierID = sup.SupplierID " +
            // For category, we need to join through BookCategories
            "LEFT JOIN BookCategories bc ON bk.BookID = bc.BookID " +
            "LEFT JOIN Categories cat ON bc.CategoryID = cat.CategoryID " +
            "WHERE cp.AcquisitionDate >= ? AND cp.AcquisitionDate <= ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(startDate);
        params.add(endDate);

        if (categoryId != null) {
            sql.append("AND bc.CategoryID = ? ");
            params.add(categoryId);
        }
        if (supplierId != null) {
             // This assumes the join path to suppliers is valid for all copies.
             // If copies can be acquired without a PO/Supplier, this filter might exclude them.
            sql.append("AND sup.SupplierID = ? ");
            params.add(supplierId);
        }
        // To avoid duplicates if a book is in multiple categories but we only want one line per copy:
        // One way is to use GROUP BY copy details and aggregate categories (e.g. LISTAGG or GROUP_CONCAT if DB supports)
        // Or, if one category is primary, join specifically for that.
        // For simplicity, this might return multiple rows per copy if it's in multiple categories and categoryId is null.
        // If categoryId is NOT null, it correctly filters.
        // A more robust approach for multi-category books might involve fetching categories separately or a subquery.

        sql.append("ORDER BY cp.AcquisitionDate DESC, bk.Title ASC");

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            // System.out.println("Executing NewAcquisitions SQL: " + pstmt.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    acquisitions.add(new com.librarysystem.dto.NewAcquisitionDTO(
                        rs.getInt("CopyID"),
                        rs.getString("BookTitle"),
                        rs.getString("ISBN"),
                        rs.getString("CopyBarcode"),
                        rs.getDate("AcquisitionDate"),
                        rs.getBigDecimal("Cost"),
                        rs.getString("CategoryName"), // Will be null if not joined correctly or book has no category
                        rs.getString("SupplierName")  // Will be null if not from a PO or join fails
                    ));
                }
            }
        }
        // To handle potential duplicates from category join if categoryId is null:
        // This is a post-processing step, less efficient than SQL grouping if possible.
        if (categoryId == null && !acquisitions.isEmpty()) {
            return acquisitions.stream()
                .collect(java.util.stream.Collectors.toMap(
                    com.librarysystem.dto.NewAcquisitionDTO::getCopyId,
                    dto -> dto,
                    (dto1, dto2) -> { // Merge strategy: take first, or combine categories if DTO supports List<String>
                        if (dto1.getCategoryName() == null) return dto2;
                        if (dto2.getCategoryName() == null) return dto1;
                        // Simple: just take the first one encountered
                        return dto1;
                    }
                )).values().stream().collect(java.util.stream.Collectors.toList());
        }
        return acquisitions;
    }

    public List<com.librarysystem.dto.InventoryValuationDTO> getInventoryValuation(Integer branchId, Integer categoryId, String condition) throws SQLException {
        List<com.librarysystem.dto.InventoryValuationDTO> valuations = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT SUM(cp.Cost) AS TotalValue, COUNT(cp.CopyID) AS ItemCount"
            // Dimension will be added based on grouping
        );
        List<Object> params = new ArrayList<>();
        String groupByDimension;
        String dimensionSelect;

        // Determine primary grouping dimension (e.g. if branchId is provided, group by branch)
        // This example will group by one dimension at a time. More complex reports might need multiple groupings.
        if (branchId != null) { // Valuation for a specific branch (could be further broken down by category/condition if needed)
            dimensionSelect = ", loc.BranchName AS Dimension ";
            sql.append(dimensionSelect);
            sql.append("FROM Copies cp JOIN Locations loc ON cp.LocationID = loc.LocationID ");
            groupByDimension = "loc.BranchName";
            sql.append("WHERE cp.LocationID = ? ");
            params.add(branchId);
        } else if (categoryId != null) { // Valuation for a specific category
            dimensionSelect = ", cat.CategoryName AS Dimension ";
            sql.append(dimensionSelect);
            sql.append("FROM Copies cp JOIN Books bk ON cp.BookID = bk.BookID JOIN BookCategories bc ON bk.BookID = bc.BookID JOIN Categories cat ON bc.CategoryID = cat.CategoryID ");
            groupByDimension = "cat.CategoryName";
            sql.append("WHERE bc.CategoryID = ? ");
            params.add(categoryId);
        } else if (condition != null && !condition.isEmpty()) { // Valuation for a specific condition
            dimensionSelect = ", cp.Condition AS Dimension ";
            sql.append(dimensionSelect);
            sql.append("FROM Copies cp ");
            groupByDimension = "cp.Condition";
            sql.append("WHERE cp.Condition = ? ");
            params.add(condition);
        } else { // Overall valuation (no specific grouping, or default to e.g. by branch)
            dimensionSelect = ", loc.BranchName AS Dimension "; // Default: Group by branch if no other specific filter
            sql.append(dimensionSelect);
            sql.append("FROM Copies cp LEFT JOIN Locations loc ON cp.LocationID = loc.LocationID ");
            groupByDimension = "loc.BranchName";
            sql.append("WHERE 1=1 "); // Start WHERE clause
        }

        // Add secondary filters if primary grouping is already set
        if (branchId == null && categoryId != null) { // If primary was not branch, but category filter is applied
             if (!sql.toString().contains("JOIN Categories cat")) { // Ensure joins if not already there
                sql.append("JOIN Books bk ON cp.BookID = bk.BookID JOIN BookCategories bc ON bk.BookID = bc.BookID JOIN Categories cat ON bc.CategoryID = cat.CategoryID ");
            }
            sql.append("AND bc.CategoryID = ? ");
            params.add(categoryId);
        }
        if (branchId == null && condition != null && !condition.isEmpty()) { // If primary was not branch, but condition filter applied
            sql.append("AND cp.Condition = ? ");
            params.add(condition);
        }
        // ... other combinations of filters ...

        sql.append("GROUP BY ").append(groupByDimension).append(" ORDER BY Dimension");

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            // System.out.println("Executing InventoryValuation SQL: " + pstmt.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dim = rs.getString("Dimension");
                    if (dim == null) dim = "(Not Set/Unknown)";
                    valuations.add(new com.librarysystem.dto.InventoryValuationDTO(
                        dim,
                        rs.getLong("ItemCount"),
                        rs.getBigDecimal("TotalValue") != null ? rs.getBigDecimal("TotalValue") : BigDecimal.ZERO
                    ));
                }
            }
        }
        return valuations;
    }

    public List<com.librarysystem.dto.CollectionAgeAnalysisDTO> getCollectionAgeAnalysis(List<Integer> ageThresholdsYears) throws SQLException {
        List<com.librarysystem.dto.CollectionAgeAnalysisDTO> analysis = new ArrayList<>();
        String currentYear = Integer.toString(java.time.Year.now().getValue());

        // Get total number of items in collection (Books with at least one Copy)
        long totalItems = 0;
        String countSql = "SELECT COUNT(DISTINCT bk.BookID) FROM Books bk JOIN Copies cp ON bk.BookID = cp.BookID"; // Or COUNT(cp.CopyID) if by copy
        try(Connection conn = DBConnectionUtil.getConnection();
            PreparedStatement countPstmt = conn.prepareStatement(countSql);
            ResultSet countRs = countPstmt.executeQuery()){
            if(countRs.next()){
                totalItems = countRs.getLong(1);
            }
        }
        if(totalItems == 0) return analysis; // Avoid division by zero

        // Build CASE statement for age groups
        StringBuilder caseStmt = new StringBuilder("CASE ");
        for (int i = 0; i < ageThresholdsYears.size(); i++) {
            int lowerBound = (i == 0) ? 0 : ageThresholdsYears.get(i-1);
            int upperBound = ageThresholdsYears.get(i);
            caseStmt.append("WHEN (").append(currentYear).append(" - bk.PublicationYear) >= ").append(lowerBound)
                    .append(" AND (").append(currentYear).append(" - bk.PublicationYear) < ").append(upperBound)
                    .append(" THEN '").append(lowerBound).append("-").append(upperBound).append(" Years' ");
        }
        caseStmt.append("ELSE 'Older than ").append(ageThresholdsYears.get(ageThresholdsYears.size()-1)).append(" Years' END AS AgeGroup");

        String sql = "SELECT " + caseStmt.toString() + ", COUNT(DISTINCT bk.BookID) AS ItemCount " +
                     "FROM Books bk JOIN Copies cp ON bk.BookID = cp.BookID " + // Ensure we only count books that have copies
                     "WHERE bk.PublicationYear IS NOT NULL " +
                     "GROUP BY AgeGroup ORDER BY AgeGroup"; // Order might need custom logic for string "Older than..."

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // System.out.println("Executing CollectionAgeAnalysis SQL: " + pstmt.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String ageGroup = rs.getString("AgeGroup");
                    long itemCount = rs.getLong("ItemCount");
                    double percentage = (totalItems > 0) ? ((double)itemCount / totalItems) * 100.0 : 0.0;
                    analysis.add(new com.librarysystem.dto.CollectionAgeAnalysisDTO(ageGroup, itemCount, percentage));
                }
            }
        }
        return analysis;
    }

}
