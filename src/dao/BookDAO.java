package dao;

import model.*;
import utils.DBConnection;
import model.Book.Format;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDAO {
	
    // SQL Constants with consistent table naming
    private static final String TABLE_NAME = "Book";
    private static final String INSERT_BOOK_SQL = "INSERT INTO " + TABLE_NAME + 
        " (isbn, title, subtitle, publisher_id, publication_year, edition, " +
        "language, call_number, number_of_pages, summary, format) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_BOOK_SQL = "UPDATE " + TABLE_NAME + 
        " SET title = ?, subtitle = ?, publisher_id = ?, publication_year = ?, " +
        "edition = ?, language = ?, call_number = ?, number_of_pages = ?, " +
        "summary = ?, format = ? WHERE isbn = ?";
    
    private static final String FIND_BY_TITLE_SQL = "SELECT b.*, p.name as publisher_name FROM Book b " +
            "LEFT JOIN Publisher p ON b.publisher_id = p.id WHERE LOWER(b.title) LIKE LOWER(?)";
    
    private static final String FIND_BY_AUTHOR_SQL = "SELECT b.*, p.name as publisher_name FROM Book b " +
            "LEFT JOIN Publisher p ON b.publisher_id = p.id " +
            "JOIN BookAuthor ba ON b.isbn = ba.book_isbn " +
            "JOIN Author a ON ba.author_id = a.id WHERE LOWER(a.name) LIKE LOWER(?)";
    
    private static final String FIND_BY_PUBLISHER_SQL = "SELECT b.*, p.name as publisher_name FROM Book b " +
            "LEFT JOIN Publisher p ON b.publisher_id = p.id WHERE b.publisher_id = ?";
    
    private static final String COUNT_ALL_SQL = "SELECT COUNT(*) FROM Book";
    private static final String EXISTS_SQL = "SELECT 1 FROM Book WHERE isbn = ?";
    private static final String REMOVE_AUTHOR_SQL = "DELETE FROM BookAuthor WHERE book_isbn = ? AND author_id = ?";
    private static final String REMOVE_CATEGORY_SQL = "DELETE FROM BookCategory WHERE book_isbn = ? AND category_id = ?";

    private static final String DELETE_BOOK_SQL = "DELETE FROM " + TABLE_NAME + " WHERE isbn = ?";

    private static final String FIND_BY_ISBN_SQL = "SELECT b.*, p.name as publisher_name FROM " + 
        TABLE_NAME + " b LEFT JOIN Publisher p ON b.publisher_id = p.id WHERE b.isbn = ?";

    private static final String FIND_ALL_SQL = "SELECT b.*, p.name as publisher_name FROM " + 
        TABLE_NAME + " b LEFT JOIN Publisher p ON b.publisher_id = p.id";

    // Author and Category related SQL
    private static final String INSERT_AUTHOR_SQL = "INSERT INTO Author (name) VALUES (?)";
    private static final String FIND_AUTHOR_SQL = "SELECT id FROM Author WHERE name = ?";
    private static final String INSERT_CATEGORY_SQL = "INSERT INTO Category (name) VALUES (?)";
    private static final String FIND_CATEGORY_SQL = "SELECT id FROM Category WHERE name = ?";

    private static final String LINK_AUTHOR_SQL = "INSERT INTO BookAuthor (book_isbn, author_id) VALUES (?, ?)";
    private static final String LINK_CATEGORY_SQL = "INSERT INTO BookCategory (book_isbn, category_id) VALUES (?, ?)";
    private static final String CLEAR_AUTHORS_SQL = "DELETE FROM BookAuthor WHERE book_isbn = ?";
    private static final String CLEAR_CATEGORIES_SQL = "DELETE FROM BookCategory WHERE book_isbn = ?";

    private static final String GET_AUTHORS_SQL = "SELECT a.id, a.name FROM BookAuthor ba " +
        "JOIN Author a ON ba.author_id = a.id WHERE ba.book_isbn = ?";
    private static final String GET_CATEGORIES_SQL = "SELECT c.id, c.name FROM BookCategory bc " +
        "JOIN Category c ON bc.category_id = c.id WHERE bc.book_isbn = ?";

    // Core CRUD Operations with consistent transaction handling
    public Optional<Book> findByIsbn(String isbn) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(true); // Ensure auto-commit for read operations
            
            try (PreparedStatement stmt = conn.prepareStatement(FIND_BY_ISBN_SQL)) {
                stmt.setString(1, isbn);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Book book = mapBookFromResultSet(rs);
                        book.setAuthors(getAuthors(conn, isbn));
                        book.setCategories(getCategories(conn, isbn));
                        return Optional.of(book);
                    }
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR finding book by ISBN " + isbn + ": " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    public void create(Book book, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_BOOK_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setBookParameters(ps, book);
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void linkAuthors(String isbn, List<Author> authors, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(LINK_AUTHOR_SQL)) {
            for (Author author : authors) {
                ps.setString(1, isbn);
                ps.setInt(2, author.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(true); // Ensure auto-commit for read operations
            
            System.out.println("[DEBUG] Got database connection");
            
            String sql = "SELECT b.*, p.name as publisher_name FROM Book b " +
                        "LEFT JOIN Publisher p ON b.publisher_id = p.id";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                System.out.println("[DEBUG] Executed query");
                
                while (rs.next()) {
                    System.out.println("[DEBUG] Found book: " + rs.getString("isbn"));
                    Book book = new Book();
                    book.setIsbn(rs.getString("isbn"));
                    book.setTitle(rs.getString("title"));
                    book.setSubtitle(rs.getString("subtitle"));

                    Publisher publisher = new Publisher();
                    publisher.setId(rs.getInt("publisher_id"));
                    publisher.setName(rs.getString("publisher_name"));
                    book.setPublisher(publisher);

                    book.setPublicationYear(rs.getInt("publication_year"));
                    book.setEdition(rs.getString("edition"));
                    book.setLanguage(rs.getString("language"));
                    book.setCallNumber(rs.getString("call_number"));
                    book.setNumberOfPages(rs.getInt("number_of_pages"));
                    book.setSummary(rs.getString("summary"));
                    
                    String formatStr = rs.getString("format");
                    book.setFormat(formatStr != null ? Format.valueOf(formatStr) : Format.PAPERBACK);
                    
                    book.setAuthors(getAuthors(conn, book.getIsbn()));
                    book.setCategories(getCategories(conn, book.getIsbn()));

                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.out.println("[ERROR] Database error:");
            e.printStackTrace();
        }
        
        System.out.println("[DEBUG] Number of books fetched: " + books.size());
        return books;
    }

    private Book mapBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setSubtitle(rs.getString("subtitle"));
        
        Publisher publisher = new Publisher();
        publisher.setId((int) rs.getLong("publisher_id"));
        publisher.setName(rs.getString("publisher_name"));
        book.setPublisher(publisher);
        
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setEdition(rs.getString("edition"));
        book.setLanguage(rs.getString("language"));
        book.setCallNumber(rs.getString("call_number"));
        book.setNumberOfPages(rs.getInt("number_of_pages"));
        book.setSummary(rs.getString("summary"));
        book.setFormat(Book.Format.valueOf(rs.getString("format")));
        
        return book;
    }
    
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                loadAssociations(conn, book);
                books.add(book);
            }
        } catch (SQLException e) {
            handleError("Error retrieving all books", e);
        }
        return Collections.unmodifiableList(books);
    }

    public boolean save(Book book) {
        if (book == null || isNullOrBlank(book.getIsbn())) {
            return false;
        }

        // Check if book exists to determine insert vs update
        boolean exists = findByIsbn(book.getIsbn()).isPresent();
        return exists ? update(book) : insert(book);
    }

    private boolean insert(Book book) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(INSERT_BOOK_SQL)) {
                setBookParameters(ps, book);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("Failed to insert book");
                }
            }

            saveAssociations(conn, book);
            conn.commit();
            return true;
        } catch (SQLException e) {
            rollbackTransaction(conn);
            handleError("Error saving book with ISBN: " + book.getIsbn(), e);
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    public boolean update(Book book) {
        if (book == null || isNullOrBlank(book.getIsbn())) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(UPDATE_BOOK_SQL)) {
                setUpdateParameters(ps, book);
                ps.setString(11, book.getIsbn());
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("Failed to update book");
                }
            }

            clearAssociations(conn, book.getIsbn());
            saveAssociations(conn, book);

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollbackTransaction(conn);
            handleError("Error updating book with ISBN: " + book.getIsbn(), e);
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    public boolean delete(String isbn) {
        if (isNullOrBlank(isbn)) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            clearAssociations(conn, isbn);

            try (PreparedStatement ps = conn.prepareStatement(DELETE_BOOK_SQL)) {
                ps.setString(1, isbn);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("Failed to delete book");
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            rollbackTransaction(conn);
            handleError("Error deleting book with ISBN: " + isbn, e);
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    // Association Management
    public void linkAuthors(Book book, List<Author> authors) {
        if (book == null || authors == null || isNullOrBlank(book.getIsbn())) {
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Clear existing authors
            try (PreparedStatement ps = conn.prepareStatement(CLEAR_AUTHORS_SQL)) {
                ps.setString(1, book.getIsbn());
                ps.executeUpdate();
            }

            // Link new authors
            try (PreparedStatement ps = conn.prepareStatement(LINK_AUTHOR_SQL)) {
                for (Author author : authors) {
                    int authorId = resolveAuthorId(conn, author);
                    ps.setString(1, book.getIsbn());
                    ps.setInt(2, authorId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            rollbackTransaction(conn);
            handleError("Error linking authors to book", e);
        } finally {
            closeConnection(conn);
        }
    }

    public void linkCategories(Book book, List<Category> categories) {
        if (book == null || categories == null || isNullOrBlank(book.getIsbn())) {
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Clear existing categories
            try (PreparedStatement ps = conn.prepareStatement(CLEAR_CATEGORIES_SQL)) {
                ps.setString(1, book.getIsbn());
                ps.executeUpdate();
            }

            // Link new categories
            try (PreparedStatement ps = conn.prepareStatement(LINK_CATEGORY_SQL)) {
                for (Category category : categories) {
                    int categoryId = resolveCategoryId(conn, category);
                    ps.setString(1, book.getIsbn());
                    ps.setInt(2, categoryId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            rollbackTransaction(conn);
            handleError("Error linking categories to book", e);
        } finally {
            closeConnection(conn);
        }
    }

    // Helper Methods
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setSubtitle(rs.getString("subtitle"));

        Publisher publisher = new Publisher();
        publisher.setId(rs.getInt("publisher_id"));
        publisher.setName(rs.getString("publisher_name"));
        book.setPublisher(publisher);

        book.setPublicationYear(rs.getInt("publication_year"));
        book.setEdition(rs.getString("edition"));
        book.setLanguage(rs.getString("language"));
        book.setCallNumber(rs.getString("call_number"));
        book.setNumberOfPages(rs.getInt("number_of_pages"));
        book.setSummary(rs.getString("summary"));
        
        String formatStr = rs.getString("format");
        if (formatStr != null) {
            try {
                book.setFormat(Book.Format.valueOf(formatStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                book.setFormat(Book.Format.PAPERBACK); // Default value
            }
        }

        return book;
    }

    private void setBookParameters(PreparedStatement ps, Book book) throws SQLException {
        ps.setString(1, book.getIsbn());
        ps.setString(2, book.getTitle());
        ps.setString(3, book.getSubtitle());
        ps.setObject(4, book.getPublisher() != null ? book.getPublisher().getId() : null, Types.INTEGER);
        ps.setInt(5, book.getPublicationYear());
        ps.setString(6, book.getEdition());
        ps.setString(7, book.getLanguage());
        ps.setString(8, book.getCallNumber());
        ps.setInt(9, book.getNumberOfPages());
        ps.setString(10, book.getSummary());
        ps.setString(11, book.getFormat() != null ? book.getFormat().name() : null);
    }

    private void setUpdateParameters(PreparedStatement ps, Book book) throws SQLException {
        ps.setString(1, book.getTitle());
        ps.setString(2, book.getSubtitle());
        ps.setObject(3, book.getPublisher() != null ? book.getPublisher().getId() : null, Types.INTEGER);
        ps.setInt(4, book.getPublicationYear());
        ps.setString(5, book.getEdition());
        ps.setString(6, book.getLanguage());
        ps.setString(7, book.getCallNumber());
        ps.setInt(8, book.getNumberOfPages());
        ps.setString(9, book.getSummary());
        ps.setString(10, book.getFormat() != null ? book.getFormat().name() : null);
    }

    private void loadAssociations(Connection conn, Book book) throws SQLException {
        if (book == null || conn == null) return;
        
        book.setAuthors(getAuthors(conn, book.getIsbn()));
        book.setCategories(getCategories(conn, book.getIsbn()));
    }

    private List<Author> getAuthors(Connection conn, String isbn) throws SQLException {
        List<Author> authors = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(GET_AUTHORS_SQL)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    authors.add(new Author(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return Collections.unmodifiableList(authors);
    }

    private List<Category> getCategories(Connection conn, String isbn) throws SQLException {
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(GET_CATEGORIES_SQL)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(new Category(rs.getInt("id"), rs.getString("name")));
                }
            }
        }
        return Collections.unmodifiableList(categories);
    }

    private void saveAssociations(Connection conn, Book book) throws SQLException {
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            processAuthors(conn, book);
        }

        if (book.getCategories() != null && !book.getCategories().isEmpty()) {
            processCategories(conn, book);
        }
    }

    private void processAuthors(Connection conn, Book book) throws SQLException {
        if (book.getAuthors() == null || book.getAuthors().isEmpty()) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(LINK_AUTHOR_SQL)) {
            for (Author author : book.getAuthors()) {
                int authorId = resolveAuthorId(conn, author);
                ps.setString(1, book.getIsbn());
                ps.setInt(2, authorId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void processCategories(Connection conn, Book book) throws SQLException {
        if (book.getCategories() == null || book.getCategories().isEmpty()) {
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(LINK_CATEGORY_SQL)) {
            for (Category category : book.getCategories()) {
                int categoryId = resolveCategoryId(conn, category);
                ps.setString(1, book.getIsbn());
                ps.setInt(2, categoryId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private int resolveAuthorId(Connection conn, Author author) throws SQLException {
        // Try to find existing author first
        try (PreparedStatement ps = conn.prepareStatement(FIND_AUTHOR_SQL)) {
            ps.setString(1, author.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        // Create new author if not found
        try (PreparedStatement ps = conn.prepareStatement(INSERT_AUTHOR_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author.getName());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to resolve author ID");
    }

    private int resolveCategoryId(Connection conn, Category category) throws SQLException {
        // Try to find existing category first
        try (PreparedStatement ps = conn.prepareStatement(FIND_CATEGORY_SQL)) {
            ps.setString(1, category.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        // Create new category if not found
        try (PreparedStatement ps = conn.prepareStatement(INSERT_CATEGORY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, category.getName());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to resolve category ID");
    }

    public void clearAssociations(Connection conn, String isbn) throws SQLException {
        try (PreparedStatement ps1 = conn.prepareStatement(CLEAR_AUTHORS_SQL);
             PreparedStatement ps2 = conn.prepareStatement(CLEAR_CATEGORIES_SQL)) {
            ps1.setString(1, isbn);
            ps2.setString(1, isbn);
            ps1.executeUpdate();
            ps2.executeUpdate();
        }
    }

    // Utility Methods
    private boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                handleError("Error during rollback", e);
            }
        }
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                handleError("Error closing connection", e);
            }
        }
    }

    private void handleError(String message, Exception e) {
        System.err.println("[ERROR] " + message);
        e.printStackTrace();
    }
    
    // 1. Search Methods
    public List<Book> findByTitle(String title) {
        if (isNullOrBlank(title)) return Collections.emptyList();
        
        List<Book> books = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_TITLE_SQL)) {
            
            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = mapResultSetToBook(rs);
                    loadAssociations(conn, book);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            handleError("Error searching books by title: " + title, e);
        }
        return Collections.unmodifiableList(books);
    }

    public List<Book> findByAuthor(String authorName) {
        if (isNullOrBlank(authorName)) return Collections.emptyList();
        
        List<Book> books = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_AUTHOR_SQL)) {
            
            ps.setString(1, "%" + authorName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = mapResultSetToBook(rs);
                    loadAssociations(conn, book);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            handleError("Error searching books by author: " + authorName, e);
        }
        return Collections.unmodifiableList(books);
    }

    public List<Book> findByPublisher(int publisherId) {
        if (publisherId <= 0) return Collections.emptyList();
        
        List<Book> books = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_PUBLISHER_SQL)) {
            
            ps.setInt(1, publisherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = mapResultSetToBook(rs);
                    loadAssociations(conn, book);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            handleError("Error searching books by publisher ID: " + publisherId, e);
        }
        return Collections.unmodifiableList(books);
    }

    // 2. Pagination Support
    public List<Book> findAll(int offset, int limit) {
        if (offset < 0 || limit <= 0) return Collections.emptyList();
        
        String paginatedSql = FIND_ALL_SQL + " LIMIT ? OFFSET ?";
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(paginatedSql)) {
            
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Book book = mapResultSetToBook(rs);
                    loadAssociations(conn, book);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            handleError("Error retrieving paginated books", e);
        }
        return Collections.unmodifiableList(books);
    }

    // 3. Batch Operations
    public boolean saveAll(List<Book> books) {
        if (books == null || books.isEmpty()) return false;
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            for (Book book : books) {
                if (book == null || isNullOrBlank(book.getIsbn())) continue;
                
                boolean exists = findByIsbn(book.getIsbn()).isPresent();
                String sql = exists ? UPDATE_BOOK_SQL : INSERT_BOOK_SQL;
                
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (exists) {
                        setUpdateParameters(ps, book);
                        ps.setString(11, book.getIsbn());
                    } else {
                        setBookParameters(ps, book);
                    }
                    ps.executeUpdate();
                }
                
                clearAssociations(conn, book.getIsbn());
                saveAssociations(conn, book);
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            rollbackTransaction(conn);
            handleError("Error saving batch of books", e);
            return false;
        } finally {
            closeConnection(conn);
        }
    }

    // 4. Association Checks
    public boolean hasAuthor(String isbn, int authorId) {
        if (isNullOrBlank(isbn) || authorId <= 0) return false;
        
        String sql = "SELECT 1 FROM BookAuthor WHERE book_isbn = ? AND author_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, isbn);
            ps.setInt(2, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            handleError("Error checking author association", e);
            return false;
        }
    }

    public boolean removeAuthor(String isbn, int authorId) {
        if (isNullOrBlank(isbn) || authorId <= 0) return false;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(REMOVE_AUTHOR_SQL)) {
            
            ps.setString(1, isbn);
            ps.setInt(2, authorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleError("Error removing author association", e);
            return false;
        }
    }

    // 5. Utility Methods
    public int countAll() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(COUNT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            handleError("Error counting books", e);
            return 0;
        }
    }

    public boolean exists(String isbn) {
        if (isNullOrBlank(isbn)) return false;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_SQL)) {
            
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            handleError("Error checking book existence", e);
            return false;
        }
    }
    
    public boolean removeCategory(String isbn, int categoryId) {
        if (isNullOrBlank(isbn) || categoryId <= 0) return false;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(REMOVE_CATEGORY_SQL)) {
            
            ps.setString(1, isbn);
            ps.setInt(2, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            handleError("Error removing category association", e);
            return false;
        }
    }

    public Book findById(int id) {
        if (id <= 0) {
            return null;
        }

        final String sql = "SELECT b.*, p.name as publisher_name FROM Book b " +
                          "LEFT JOIN Publisher p ON b.publisher_id = p.id WHERE b.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book book = mapResultSetToBook(rs);
                    loadAssociations(conn, book);
                    return book;
                }
            }
        } catch (SQLException e) {
            handleError("Error finding book by ID: " + id, e);
        }
        return null;
    }

    public void create(Book book) {
        if (book == null || isNullOrBlank(book.getIsbn())) {
            throw new IllegalArgumentException("Book and ISBN cannot be null");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert basic book information
            try (PreparedStatement ps = conn.prepareStatement(INSERT_BOOK_SQL, Statement.RETURN_GENERATED_KEYS)) {
                setBookParameters(ps, book);
                int affectedRows = ps.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating book failed, no rows affected.");
                }

                // Get generated book ID if available
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                    }
                }
            }

            // 2. Insert associations
            saveAssociations(conn, book);

            conn.commit();
        } catch (SQLException e) {
            rollbackTransaction(conn);
            handleError("Error creating book with ISBN: " + book.getIsbn(), e);
            throw new RuntimeException("Failed to create book", e);
        } finally {
            closeConnection(conn);
        }
    }

    public void update(Book book, Connection conn) throws SQLException {
        if (book == null || isNullOrBlank(book.getIsbn())) {
            throw new IllegalArgumentException("Book and ISBN cannot be null");
        }

        // First check if book exists
        if (!exists(book.getIsbn(), conn)) {
            throw new SQLException("No book found with ISBN: " + book.getIsbn());
        }

        try (PreparedStatement ps = conn.prepareStatement(UPDATE_BOOK_SQL)) {
            setUpdateParameters(ps, book);
            ps.setString(11, book.getIsbn());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating book failed. No rows affected for ISBN: " + book.getIsbn());
            }
        }
    }

    private boolean exists(String isbn, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(EXISTS_SQL)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void linkCategories(String isbn, List<Category> categories, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(LINK_CATEGORY_SQL)) {
            for (Category category : categories) {
                int categoryId = resolveCategoryId(conn, category);
                ps.setString(1, isbn);
                ps.setInt(2, categoryId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
}
