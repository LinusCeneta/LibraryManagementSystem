package com.librarysystem.dao;

import com.librarysystem.model.Author;
import com.librarysystem.model.Book;
import com.librarysystem.model.Category;
import com.librarysystem.model.Publisher; // Assuming Publisher model
import com.librarysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookDAO {

    private AuthorDAO authorDAO;
    private CategoryDAO categoryDAO;
    private PublisherDAO publisherDAO;
    private CopyDAO copyDAO; // For availability checks

    public BookDAO() {
        // Initialize DAOs. For a real app, use dependency injection.
        this.authorDAO = new AuthorDAO();
        this.categoryDAO = new CategoryDAO();
        this.publisherDAO = new PublisherDAO();
        this.copyDAO = new CopyDAO();
    }

    // Constructor for DI
    public BookDAO(AuthorDAO authorDAO, CategoryDAO categoryDAO, PublisherDAO publisherDAO, CopyDAO copyDAO) {
        this.authorDAO = authorDAO;
        this.categoryDAO = categoryDAO;
        this.publisherDAO = publisherDAO;
        this.copyDAO = copyDAO;
    }


    public Optional<Book> getBookById(int bookId) throws SQLException {
        String sql = "SELECT b.*, p.PublisherName FROM Books b LEFT JOIN Publishers p ON b.PublisherID = p.PublisherID WHERE b.BookID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Book book = mapRowToBook(rs);
                    // Populate authors and categories
                    book.setBookAuthors(authorDAO.getAuthorsForBook(bookId));
                    book.setBookCategories(categoryDAO.getCategoriesForBook(bookId));
                    // Publisher is partially mapped in mapRowToBook if joined
                    return Optional.of(book);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Book> getBookByISBN(String isbn) throws SQLException {
        String sql = "SELECT b.*, p.PublisherName FROM Books b LEFT JOIN Publishers p ON b.PublisherID = p.PublisherID WHERE b.ISBN = ?";
         try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                     Book book = mapRowToBook(rs);
                    book.setBookAuthors(authorDAO.getAuthorsForBook(book.getBookID()));
                    book.setBookCategories(categoryDAO.getCategoriesForBook(book.getBookID()));
                    return Optional.of(book);
                }
            }
        }
        return Optional.empty();
    }


    // Basic search by title (can be expanded for full-text and more fields)
    public List<Book> findBooksByTitle(String title) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.*, p.PublisherName FROM Books b LEFT JOIN Publishers p ON b.PublisherID = p.PublisherID WHERE LOWER(b.Title) LIKE ? ORDER BY b.Title";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // For lists, typically don't fetch all authors/categories per book due to N+1.
                    // Fetch them on demand when viewing book details.
                    books.add(mapRowToBook(rs));
                }
            }
        }
        return books;
    }

    // CRUD operations for Books
    public Book createBook(Book book) throws SQLException {
        String sql = "INSERT INTO Books (ISBN, Title, Subtitle, PublisherID, PublicationYear, Edition, Language, Format, PageCount, Description, CoverImageURL, DeweyDecimal, LOCCallNumber, CheckoutCount, HoldCount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            mapBookToStatement(book, pstmt);
            pstmt.setInt(14, book.getCheckoutCount()); // CheckoutCount
            pstmt.setInt(15, book.getHoldCount());   // HoldCount
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setBookID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating book failed, no ID obtained.");
                }
            }

            // Link authors and categories
            updateBookAuthors(book.getBookID(), book.getBookAuthors(), conn);
            updateBookCategories(book.getBookID(), book.getBookCategories(), conn);

            conn.commit();
            return book;
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

    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE Books SET ISBN = ?, Title = ?, Subtitle = ?, PublisherID = ?, PublicationYear = ?, Edition = ?, Language = ?, Format = ?, PageCount = ?, Description = ?, CoverImageURL = ?, DeweyDecimal = ?, LOCCallNumber = ?, CheckoutCount = ?, HoldCount = ? WHERE BookID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnectionUtil.getConnection();
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);
            mapBookToStatement(book, pstmt);
            pstmt.setInt(14, book.getCheckoutCount());
            pstmt.setInt(15, book.getHoldCount());
            pstmt.setInt(16, book.getBookID());      // WHERE clause

            boolean updated = pstmt.executeUpdate() > 0;

            // Update authors and categories: clear existing and add new
            removeAllAuthorsForBook(book.getBookID(), conn);
            updateBookAuthors(book.getBookID(), book.getBookAuthors(), conn);

            removeAllCategoriesForBook(book.getBookID(), conn);
            updateBookCategories(book.getBookID(), book.getBookCategories(), conn);

            conn.commit();
            return updated;
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

    private void mapBookToStatement(Book book, PreparedStatement pstmt) throws SQLException {
        pstmt.setString(1, book.getIsbn());
        pstmt.setString(2, book.getTitle());
        pstmt.setString(3, book.getSubtitle());
        if (book.getPublisher() != null && book.getPublisher().getPublisherID() > 0) {
            pstmt.setInt(4, book.getPublisher().getPublisherID());
        } else if (book.getPublisherName() != null && !book.getPublisherName().isEmpty()) {
             // Attempt to find/create publisher by name
            if (publisherDAO == null) this.publisherDAO = new PublisherDAO();
            Optional<Publisher> pubOpt = publisherDAO.getPublisherByName(book.getPublisherName());
            if (pubOpt.isPresent()) {
                pstmt.setInt(4, pubOpt.get().getPublisherID());
            } else {
                // Or create new publisher if policy allows, then get ID
                // For now, setting null if not found by exact name
                pstmt.setNull(4, Types.INTEGER);
            }
        } else {
             pstmt.setNull(4, Types.INTEGER);
        }
        pstmt.setInt(5, book.getPublicationYear());
        pstmt.setString(6, book.getEdition());
        pstmt.setString(7, book.getLanguage());
        pstmt.setString(8, book.getFormat());
        pstmt.setInt(9, book.getNumberOfPages());
        pstmt.setString(10, book.getSummary());
        pstmt.setString(11, book.getCoverImageURL());
        pstmt.setString(12, book.getDeweyDecimal());
        pstmt.setString(13, book.getLocCallNumber());
    }


    public boolean deleteBook(int bookId) throws SQLException {
        // Assumes ON DELETE CASCADE for BookAuthors, BookCategories, BookCollectionItems, Copies.
        // If not, manual deletion from linking tables is needed first.
        String sql = "DELETE FROM Books WHERE BookID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Methods for linking tables (BookAuthors, BookCategories)
    public void updateBookAuthors(int bookId, List<Author> authors, Connection conn) throws SQLException {
        if (authors == null || authors.isEmpty()) return;
        String sql = "INSERT INTO BookAuthors (BookID, AuthorID) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Author author : authors) {
                // Ensure author exists or create them (AuthorDAO should handle this)
                // For simplicity, assuming author.getAuthorID() is valid.
                if (author.getAuthorID() == 0 && author.getAuthorName() != null) { // New author
                    if(authorDAO == null) this.authorDAO = new AuthorDAO();
                    Author createdAuthor = authorDAO.createAuthor(author); // Uses its own conn, or pass conn
                    author.setAuthorID(createdAuthor.getAuthorID());
                }
                if (author.getAuthorID() > 0) {
                    pstmt.setInt(1, bookId);
                    pstmt.setInt(2, author.getAuthorID());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }

    public void removeAllAuthorsForBook(int bookId, Connection conn) throws SQLException {
         String sql = "DELETE FROM BookAuthors WHERE BookID = ?";
          try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        }
    }

    public void updateBookCategories(int bookId, List<Category> categories, Connection conn) throws SQLException {
        if (categories == null || categories.isEmpty()) return;
        String sql = "INSERT INTO BookCategories (BookID, CategoryID) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Category category : categories) {
                if (category.getCategoryID() == 0 && category.getCategoryName() != null) { // New category
                     if(categoryDAO == null) this.categoryDAO = new CategoryDAO();
                    Category createdCategory = categoryDAO.createCategory(category); // Uses its own conn, or pass conn
                    category.setCategoryID(createdCategory.getCategoryID());
                }
                if (category.getCategoryID() > 0) {
                    pstmt.setInt(1, bookId);
                    pstmt.setInt(2, category.getCategoryID());
                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
        }
    }

    public void removeAllCategoriesForBook(int bookId, Connection conn) throws SQLException {
        String sql = "DELETE FROM BookCategories WHERE BookID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
        }
    }

    // Increment/Decrement CheckoutCount and HoldCount
    public boolean incrementCheckoutCount(int bookId) throws SQLException {
        String sql = "UPDATE Books SET CheckoutCount = CheckoutCount + 1 WHERE BookID = ?";
        try(Connection conn = DBConnectionUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        }
    }
    public boolean incrementHoldCount(int bookId) throws SQLException {
        String sql = "UPDATE Books SET HoldCount = HoldCount + 1 WHERE BookID = ?";
         try(Connection conn = DBConnectionUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        }
    }
     public boolean decrementHoldCount(int bookId) throws SQLException {
        String sql = "UPDATE Books SET HoldCount = CASE WHEN HoldCount > 0 THEN HoldCount - 1 ELSE 0 END WHERE BookID = ?";
         try(Connection conn = DBConnectionUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, bookId);
            return pstmt.executeUpdate() > 0;
        }
    }


    private Book mapRowToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookID(rs.getInt("BookID"));
        book.setIsbn(rs.getString("ISBN"));
        book.setTitle(rs.getString("Title"));
        book.setSubtitle(rs.getString("Subtitle"));

        int publisherId = rs.getInt("PublisherID");
        if (!rs.wasNull() && publisherDAO != null) { // publisherDAO might be null if default constructor was used by another DAO
            // If PublisherName is joined, we can use it directly
            if (hasColumn(rs, "PublisherName")) {
                Publisher p = new Publisher(rs.getString("PublisherName"));
                p.setPublisherID(publisherId);
                book.setPublisher(p);
            } else {
                 // Fallback: fetch publisher by ID (can be slow if called repeatedly in a list)
                publisherDAO.getPublisherById(publisherId).ifPresent(book::setPublisher);
            }
        } else if (hasColumn(rs, "PublisherName") && rs.getString("PublisherName") != null) {
            // If only name is present from join but ID was null or DAO not available
             Publisher p = new Publisher(rs.getString("PublisherName"));
             book.setPublisher(p);
        }


        book.setPublicationYear(rs.getInt("PublicationYear"));
        book.setEdition(rs.getString("Edition"));
        book.setLanguage(rs.getString("Language"));
        book.setFormat(rs.getString("Format"));
        book.setNumberOfPages(rs.getInt("PageCount"));
        book.setSummary(rs.getString("Description"));
        book.setCoverImageURL(rs.getString("CoverImageURL"));
        book.setDeweyDecimal(rs.getString("DeweyDecimal"));
        book.setLocCallNumber(rs.getString("LOCCallNumber"));
        book.setCheckoutCount(rs.getInt("CheckoutCount"));
        book.setHoldCount(rs.getInt("HoldCount"));

        // Authors and Categories are typically fetched in separate calls for detailed views
        // or joined with more complex queries for search results.
        return book;
    }

    // Helper to check if column exists in result set
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

    // TODO: Implement main searchBooks method with filters, full-text, facets, pagination

    // Methods to get distinct values for facets
    public List<String> getAllLanguages() throws SQLException {
        List<String> languages = new ArrayList<>();
        String sql = "SELECT DISTINCT Language FROM Books WHERE Language IS NOT NULL ORDER BY Language";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                languages.add(rs.getString("Language"));
            }
        }
        return languages;
    }

    public List<String> getAllFormats() throws SQLException {
        List<String> formats = new ArrayList<>();
        String sql = "SELECT DISTINCT Format FROM Books WHERE Format IS NOT NULL ORDER BY Format";
        // If format is linked to ItemTypes, query ItemTypes table instead
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                formats.add(rs.getString("Format"));
            }
        }
        return formats;
    }

    public List<Integer> getDistinctPublicationYears() throws SQLException {
        List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT PublicationYear FROM Books WHERE PublicationYear IS NOT NULL ORDER BY PublicationYear DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                years.add(rs.getInt("PublicationYear"));
            }
        }
        return years;
    }

    // Main search method (basic implementation, can be significantly enhanced)
    public List<Book> searchBooks(String searchText, Map<String, List<String>> filters, String sortBy, int page, int pageSize) throws SQLException {
        List<Book> books = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT b.*, p.PublisherName FROM Books b ");
        sql.append("LEFT JOIN Publishers p ON b.PublisherID = p.PublisherID ");
        sql.append("LEFT JOIN BookAuthors ba ON b.BookID = ba.BookID LEFT JOIN Authors a ON ba.AuthorID = a.AuthorID ");
        sql.append("LEFT JOIN BookCategories bc ON b.BookID = bc.BookID LEFT JOIN Categories cat ON bc.CategoryID = cat.CategoryID ");
        // Add more joins if searching keywords, etc.

        List<Object> params = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder();

        // Full-text search (basic LIKE, can be replaced by DB's FTS)
        if (searchText != null && !searchText.trim().isEmpty()) {
            String likePattern = "%" + searchText.toLowerCase() + "%";
            whereClause.append("(LOWER(b.Title) LIKE ? OR LOWER(b.Subtitle) LIKE ? OR LOWER(a.AuthorName) LIKE ? OR b.ISBN LIKE ? OR LOWER(b.Description) LIKE ?)");
            for(int i=0; i<5; i++) params.add(likePattern); // Add 5 times for each field in OR
        }

        // Apply filters
        if (filters != null) {
            for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
                String filterKey = entry.getKey();
                List<String> filterValues = entry.getValue();
                if (filterValues != null && !filterValues.isEmpty()) {
                    if (whereClause.length() > 0) whereClause.append(" AND ");

                    // Handle different filter keys
                    switch(filterKey) {
                        case "author":
                            whereClause.append("a.AuthorName IN (").append(String.join(",", filterValues.stream().map(v -> "?").collect(Collectors.toList()))).append(")");
                            params.addAll(filterValues);
                            break;
                        case "category":
                             whereClause.append("cat.CategoryName IN (").append(String.join(",", filterValues.stream().map(v -> "?").collect(Collectors.toList()))).append(")");
                            params.addAll(filterValues);
                            break;
                        case "language":
                            whereClause.append("b.Language IN (").append(String.join(",", filterValues.stream().map(v -> "?").collect(Collectors.toList()))).append(")");
                            params.addAll(filterValues);
                            break;
                        case "format":
                             whereClause.append("b.Format IN (").append(String.join(",", filterValues.stream().map(v -> "?").collect(Collectors.toList()))).append(")");
                            params.addAll(filterValues);
                            break;
                        case "year": // Assuming single year or range handled by caller into list
                            whereClause.append("b.PublicationYear IN (").append(String.join(",", filterValues.stream().map(v -> "?").collect(Collectors.toList()))).append(")");
                            filterValues.forEach(val -> params.add(Integer.parseInt(val)));
                            break;
                        // TODO: Add 'availability' and 'location' filters (requires joining Copies and Locations)
                        default:
                            // Unknown filter, log or ignore
                            break;
                    }
                }
            }
        }

        if (whereClause.length() > 0) {
            sql.append(" WHERE ").append(whereClause);
        }

        // Sort By (simple implementation)
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            // Sanitize sortBy to prevent SQL injection if it's directly from user input
            // For now, assume valid column names like "Title", "PublicationYear DESC"
            sql.append(" ORDER BY ").append(sortBy);
        } else {
            sql.append(" ORDER BY b.Title"); // Default sort
        }

        // Pagination (Derby specific)
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize); // offset
        params.add(pageSize);             // limit

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            System.out.println("Executing Search SQL: " + pstmt.toString()); // For debugging

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = mapRowToBook(rs);
                    // For search results, you might not populate full authors/categories here to avoid N+1.
                    // Instead, provide concatenated strings or fetch on detail page.
                    // For this example, let's assume mapRowToBook handles basic publisher name.
                    // We might add a specific "mapRowToBookSearchResult" if needed.
                    books.add(book);
                }
            }
        }
        return books;
    }

    // TODO: Add method to count total results for searchBooks for pagination
    public int countSearchBooks(String searchText, Map<String, List<String>> filters) throws SQLException {
        // Similar logic to searchBooks but with COUNT(DISTINCT b.BookID) and no pagination/ordering on main fields
        // ... implementation needed ...
        return 0; // Placeholder
    }


}
