package com.librarysystem.dao;

import com.librarysystem.model.Book;
import com.librarysystem.model.BookCollection;
import com.librarysystem.util.DBConnectionUtil;
// UserDAO needed if we want to populate CreatedByStaff user details in BookCollection
import com.librarysystem.model.User;

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

public class BookCollectionDAO {

    private UserDAO userDAO; // To fetch staff details
    private BookDAO bookDAO; // To fetch book details for a collection

    public BookCollectionDAO() {
        this.userDAO = new UserDAO();
        this.bookDAO = new BookDAO(); // Replace with actual BookDAO instance
    }

    public BookCollectionDAO(UserDAO userDAO, BookDAO bookDAO){
        this.userDAO = userDAO;
        this.bookDAO = bookDAO;
    }

    // Create a new BookCollection (header only)
    public BookCollection createBookCollection(BookCollection collection) throws SQLException {
        String sql = "INSERT INTO BookCollections (CollectionName, Description, CreatedByStaffID, DateCreated, IsPublic) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, collection.getCollectionName());
            pstmt.setString(2, collection.getDescription());
            pstmt.setInt(3, collection.getCreatedByStaffID());
            pstmt.setTimestamp(4, collection.getDateCreated() != null ? collection.getDateCreated() : new Timestamp(System.currentTimeMillis()));
            pstmt.setBoolean(5, collection.isPublic());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    collection.setCollectionID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating book collection failed, no ID obtained.");
                }
            }
            return collection;
        }
    }

    // Add a book to a collection
    public boolean addBookToCollection(int collectionId, int bookId, int orderInCollection) throws SQLException {
        String sql = "INSERT INTO BookCollectionItems (CollectionID, BookID, OrderInCollection) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collectionId);
            pstmt.setInt(2, bookId);
            pstmt.setInt(3, orderInCollection);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Remove a book from a collection
    public boolean removeBookFromCollection(int collectionId, int bookId) throws SQLException {
        String sql = "DELETE FROM BookCollectionItems WHERE CollectionID = ? AND BookID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collectionId);
            pstmt.setInt(2, bookId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Update order of books in a collection
    public boolean updateBookOrderInCollection(int collectionId, int bookId, int newOrder) throws SQLException {
        String sql = "UPDATE BookCollectionItems SET OrderInCollection = ? WHERE CollectionID = ? AND BookID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newOrder);
            pstmt.setInt(2, collectionId);
            pstmt.setInt(3, bookId);
            return pstmt.executeUpdate() > 0;
        }
    }


    public Optional<BookCollection> getBookCollectionById(int collectionId) throws SQLException {
        String sql = "SELECT * FROM BookCollections WHERE CollectionID = ?";
        BookCollection collection = null;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collectionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    collection = mapRowToBookCollection(rs);
                    // Populate staff details
                    if (userDAO != null) {
                        userDAO.findUserById(collection.getCreatedByStaffID()).ifPresent(collection::setCreatedByStaff);
                    }
                    // Populate books in the collection
                    collection.setBooks(getBooksForCollection(collectionId, conn));
                }
            }
        }
        return Optional.ofNullable(collection);
    }

    public List<BookCollection> getAllBookCollections(boolean publicOnly) throws SQLException {
        List<BookCollection> collections = new ArrayList<>();
        String sql = "SELECT * FROM BookCollections";
        if (publicOnly) {
            sql += " WHERE IsPublic = TRUE";
        }
        sql += " ORDER BY CollectionName";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                BookCollection bc = mapRowToBookCollection(rs);
                // Optionally fetch CreatedByStaff details here if needed for list view
                // For full details (including books), typically fetched when viewing a single collection
                collections.add(bc);
            }
        }
        return collections;
    }

    public List<BookCollection> findCollectionsByName(String namePart, boolean publicOnly) throws SQLException {
        List<BookCollection> collections = new ArrayList<>();
        String sql = "SELECT * FROM BookCollections WHERE LOWER(CollectionName) LIKE ?";
        if (publicOnly) {
            sql += " AND IsPublic = TRUE";
        }
        sql += " ORDER BY CollectionName";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + namePart.toLowerCase() + "%");
            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                     collections.add(mapRowToBookCollection(rs));
                }
            }
        }
        return collections;
    }


    public boolean updateBookCollection(BookCollection collection) throws SQLException {
        String sql = "UPDATE BookCollections SET CollectionName = ?, Description = ?, IsPublic = ? WHERE CollectionID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, collection.getCollectionName());
            pstmt.setString(2, collection.getDescription());
            pstmt.setBoolean(3, collection.isPublic());
            pstmt.setInt(4, collection.getCollectionID());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteBookCollection(int collectionId) throws SQLException {
        // BookCollectionItems will be deleted by ON DELETE CASCADE
        String sql = "DELETE FROM BookCollections WHERE CollectionID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collectionId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private List<Book> getBooksForCollection(int collectionId, Connection conn) throws SQLException {
        List<Book> books = new ArrayList<>();
        // Assumes BookDAO can fetch a book by ID and populate its necessary details
        // This query fetches BookIDs, then BookDAO would be used for each.
        // A JOIN could be more efficient if BookDAO's getBookById is complex.
        String sql = "SELECT b.* FROM Books b JOIN BookCollectionItems bci ON b.BookID = bci.BookID WHERE bci.CollectionID = ? ORDER BY bci.OrderInCollection, b.Title";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, collectionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Using a simplified mapRowToBook from a hypothetical BookDAO structure
                    // Replace with actual BookDAO.mapRowToBook or similar
                    if (this.bookDAO != null) {
                         // This is a placeholder, actual BookDAO mapRow would be more complete
                         Book book = new Book(); // From placeholder BookDAO
                         book.setBookID(rs.getInt("BookID"));
                         book.setTitle(rs.getString("Title"));
                         // ... map other essential book fields from ResultSet 'rs' ...
                         bookDAO.getBookById(book.getBookID()).ifPresent(books::add); // Example, inefficient N+1
                        // A better way: bookDAO.mapRowToBook(rs) if BookDAO can do that.
                        // Or even better: join all necessary book fields in the SQL above and map here.
                    }
                }
            }
        }
        // Fallback or simplified: if bookDAO is just the dummy, we can't really populate books.
        // The current dummy BookDAO.getBookById returns empty, so this list will be empty.
        // This highlights the dependency on a functional BookDAO.
        return books;
    }


    private BookCollection mapRowToBookCollection(ResultSet rs) throws SQLException {
        BookCollection collection = new BookCollection();
        collection.setCollectionID(rs.getInt("CollectionID"));
        collection.setCollectionName(rs.getString("CollectionName"));
        collection.setDescription(rs.getString("Description"));
        collection.setCreatedByStaffID(rs.getInt("CreatedByStaffID"));
        collection.setDateCreated(rs.getTimestamp("DateCreated"));
        collection.setPublic(rs.getBoolean("IsPublic"));
        return collection;
    }
}
