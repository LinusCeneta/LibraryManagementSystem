package com.librarysystem.dao;

import com.librarysystem.model.Hold;
import com.librarysystem.util.DBConnectionUtil;
// Assuming User and Book models for populating details
import com.librarysystem.model.User;
import com.librarysystem.model.Book;


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

public class HoldDAO {

    private UserDAO userDAO; // For fetching Member details
    private BookDAO bookDAO; // For fetching Book details (placeholder, needs actual BookDAO)

    public HoldDAO() {
        this.userDAO = new UserDAO();
        this.bookDAO = new BookDAO(); // Replace with actual BookDAO instance
    }

    public HoldDAO(UserDAO userDAO, BookDAO bookDAO) {
        this.userDAO = userDAO;
        this.bookDAO = bookDAO;
    }

    public Hold createHold(Hold hold) throws SQLException {
        String sql = "INSERT INTO Holds (BookID, MemberID, RequestDate, Status, ExpiryDate, FulfilledByCopyID, PickupLocationID, QueuePosition, NotificationSentDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, hold.getBookID());
            pstmt.setInt(2, hold.getMemberID());
            pstmt.setTimestamp(3, hold.getRequestDate() != null ? hold.getRequestDate() : new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, hold.getStatus() != null ? hold.getStatus() : "PENDING");

            if (hold.getExpiryDate() != null) pstmt.setTimestamp(5, hold.getExpiryDate()); else pstmt.setNull(5, Types.TIMESTAMP);
            if (hold.getFulfilledByCopyID() != null) pstmt.setInt(6, hold.getFulfilledByCopyID()); else pstmt.setNull(6, Types.INTEGER);
            if (hold.getPickupLocationID() != null) pstmt.setInt(7, hold.getPickupLocationID()); else pstmt.setNull(7, Types.INTEGER);
            if (hold.getQueuePosition() != null) pstmt.setInt(8, hold.getQueuePosition()); else pstmt.setNull(8, Types.INTEGER);
            if (hold.getNotificationSentDate() != null) pstmt.setTimestamp(9, hold.getNotificationSentDate()); else pstmt.setNull(9, Types.TIMESTAMP);

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    hold.setHoldID(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating hold failed, no ID obtained.");
                }
            }
            return hold;
        }
    }

    public Optional<Hold> getHoldById(long holdId) throws SQLException {
        String sql = "SELECT * FROM Holds WHERE HoldID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, holdId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToHoldWithDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Hold> findHoldsByMemberId(int memberId) throws SQLException {
        List<Hold> holds = new ArrayList<>();
        String sql = "SELECT h.*, b.Title as BookTitle " + // Include BookTitle
                     "FROM Holds h " +
                     "JOIN Books b ON h.BookID = b.BookID " +
                     "WHERE h.MemberID = ? ORDER BY h.RequestDate ASC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Hold hold = mapRowToHold(rs);
                    Book book = new Book();
                    book.setBookID(hold.getBookID());
                    book.setTitle(rs.getString("BookTitle"));
                    hold.setBook(book);
                    holds.add(hold);
                }
            }
        }
        return holds;
    }

    public List<Hold> findHoldsByBookId(int bookId) throws SQLException {
        List<Hold> holds = new ArrayList<>();
        // Order by RequestDate to get the queue order
        String sql = "SELECT * FROM Holds WHERE BookID = ? AND Status IN ('PENDING', 'READY_FOR_PICKUP') ORDER BY RequestDate ASC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    holds.add(mapRowToHoldWithDetails(rs));
                }
            }
        }
        return holds;
    }

    public Optional<Hold> findNextPendingHoldForBook(int bookId) throws SQLException {
        String sql = "SELECT * FROM Holds WHERE BookID = ? AND Status = 'PENDING' ORDER BY RequestDate ASC LIMIT 1"; // Derby specific LIMIT
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToHoldWithDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    public boolean updateHoldStatus(long holdId, String newStatus) throws SQLException {
        String sql = "UPDATE Holds SET Status = ? WHERE HoldID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setLong(2, holdId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateHoldForPickup(long holdId, int copyId, Timestamp expiryDate, Timestamp notificationDate) throws SQLException {
        String sql = "UPDATE Holds SET Status = 'READY_FOR_PICKUP', FulfilledByCopyID = ?, ExpiryDate = ?, NotificationSentDate = ? WHERE HoldID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, copyId);
            pstmt.setTimestamp(2, expiryDate);
            pstmt.setTimestamp(3, notificationDate != null ? notificationDate : new Timestamp(System.currentTimeMillis()));
            pstmt.setLong(4, holdId);
            return pstmt.executeUpdate() > 0;
        }
    }


    public boolean deleteHold(long holdId) throws SQLException {
        String sql = "DELETE FROM Holds WHERE HoldID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, holdId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public int deleteExpiredHolds() throws SQLException {
        // Deletes holds that are 'READY_FOR_PICKUP' but past their expiry date,
        // or 'PENDING' holds that are very old (e.g. > 90 days, define policy)
        String sqlReady = "UPDATE Holds SET Status = 'EXPIRED' WHERE Status = 'READY_FOR_PICKUP' AND ExpiryDate < ?";
        // String sqlPendingOld = "UPDATE Holds SET Status = 'CANCELLED' WHERE Status = 'PENDING' AND RequestDate < ?"; // Example for old pending

        int updatedCount = 0;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmtReady = conn.prepareStatement(sqlReady)) {
            pstmtReady.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            updatedCount = pstmtReady.executeUpdate();

            // Potentially add logic for old pending holds here if needed
        }
        return updatedCount;
    }


    private Hold mapRowToHold(ResultSet rs) throws SQLException {
        Hold hold = new Hold();
        hold.setHoldID(rs.getLong("HoldID"));
        hold.setBookID(rs.getInt("BookID"));
        hold.setMemberID(rs.getInt("MemberID"));
        hold.setRequestDate(rs.getTimestamp("RequestDate"));
        hold.setStatus(rs.getString("Status"));
        hold.setExpiryDate(rs.getTimestamp("ExpiryDate"));
        hold.setFulfilledByCopyID(rs.getObject("FulfilledByCopyID", Integer.class));
        hold.setPickupLocationID(rs.getObject("PickupLocationID", Integer.class));
        hold.setQueuePosition(rs.getObject("QueuePosition", Integer.class));
        hold.setNotificationSentDate(rs.getTimestamp("NotificationSentDate"));
        return hold;
    }

    private Hold mapRowToHoldWithDetails(ResultSet rs) throws SQLException {
        Hold hold = mapRowToHold(rs);
        // Populate Member details
        if (userDAO != null) {
            userDAO.findUserById(hold.getMemberID()).ifPresent(hold::setMember);
        }
        // Populate Book details (requires actual BookDAO)
        if (bookDAO != null) {
             bookDAO.getBookById(hold.getBookID()).ifPresent(hold::setBook);
        }
        // FulfilledByCopy details could also be populated if CopyDAO is available and needed
        return hold;
    }
}

// Dummy BookDAO for compilation. Replace with actual implementation.
class BookDAO {
    public Optional<Book> getBookById(int bookId) throws SQLException {
        // Dummy implementation
        System.out.println("BookDAO: Fetching book by ID (dummy) " + bookId);
        // return Optional.of(new Book()); // Return a dummy book to avoid NPE in HoldDAO if used
        return Optional.empty();
    }
}
