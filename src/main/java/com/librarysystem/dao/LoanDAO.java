package com.librarysystem.dao;

import com.librarysystem.model.Loan;
import com.librarysystem.util.DBConnectionUtil;
// Assuming User, Copy, Book models exist for populating details
import com.librarysystem.model.User;
import com.librarysystem.model.Copy;
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

public class LoanDAO {
    // DAOs for related entities, if needed for populating Loan object details
    private UserDAO userDAO;
    private CopyDAO copyDAO;

    public LoanDAO() {
        // Initialize if you want to fetch full User/Copy objects within LoanDAO methods
        // For now, assuming they are lightweight or handled by a service layer
        this.userDAO = new UserDAO();
        this.copyDAO = new CopyDAO();
    }

    // Constructor for DI if preferred
    public LoanDAO(UserDAO userDAO, CopyDAO copyDAO) {
        this.userDAO = userDAO;
        this.copyDAO = copyDAO;
    }


    public Loan createLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO Loans (MemberID, CopyID, StaffID, IssueDate, DueDate, LoanStatus, RenewalsDone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, loan.getMemberID());
            pstmt.setInt(2, loan.getCopyID());
            pstmt.setInt(3, loan.getStaffID());
            pstmt.setTimestamp(4, loan.getIssueDate() != null ? loan.getIssueDate() : new Timestamp(System.currentTimeMillis()));
            pstmt.setTimestamp(5, loan.getDueDate());
            pstmt.setString(6, loan.getLoanStatus() != null ? loan.getLoanStatus() : "ACTIVE");
            pstmt.setInt(7, loan.getRenewalsDone());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setLoanID(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating loan failed, no ID obtained.");
                }
            }
            return loan;
        }
    }

    public Optional<Loan> getLoanById(long loanId) throws SQLException {
        String sql = "SELECT * FROM Loans WHERE LoanID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, loanId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToLoan(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Loan> getActiveLoanByCopyId(int copyId) throws SQLException {
        String sql = "SELECT * FROM Loans WHERE CopyID = ? AND LoanStatus = 'ACTIVE'";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, copyId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToLoan(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Loan> findActiveLoansByMemberId(int memberId) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, c.CopyBarcode, b.Title as BookTitle " + // Include CopyBarcode and BookTitle
                     "FROM Loans l " +
                     "JOIN Copies c ON l.CopyID = c.CopyID " +
                     "JOIN Books b ON c.BookID = b.BookID " +
                     "WHERE l.MemberID = ? AND l.LoanStatus = 'ACTIVE' " +
                     "ORDER BY l.DueDate ASC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = mapRowToLoan(rs);
                    // Populate basic copy and book info if available from join
                    Copy copy = new Copy();
                    copy.setCopyID(loan.getCopyID());
                    copy.setCopyBarcode(rs.getString("CopyBarcode"));
                    Book book = new Book(); // Assuming Book model exists
                    book.setTitle(rs.getString("BookTitle"));
                    copy.setBook(book); // Assuming Copy has a setBook method
                    loan.setCopy(copy);
                    loans.add(loan);
                }
            }
        }
        return loans;
    }

    public List<Loan> findLoanHistoryByMemberId(int memberId, int limit, int offset) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, c.CopyBarcode, b.Title as BookTitle " +
                     "FROM Loans l " +
                     "JOIN Copies c ON l.CopyID = c.CopyID " +
                     "JOIN Books b ON c.BookID = b.BookID " +
                     "WHERE l.MemberID = ? " +
                     "ORDER BY l.IssueDate DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"; // Derby pagination
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                     Loan loan = mapRowToLoan(rs);
                    Copy copy = new Copy();
                    copy.setCopyID(loan.getCopyID());
                    copy.setCopyBarcode(rs.getString("CopyBarcode"));
                    Book book = new Book();
                    book.setTitle(rs.getString("BookTitle"));
                    copy.setBook(book);
                    loan.setCopy(copy);
                    loans.add(loan);
                }
            }
        }
        return loans;
    }

    public int countLoansByMemberId(int memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Loans WHERE MemberID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }


    public List<Loan> findOverdueLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        // Loans that are 'ACTIVE' but their DueDate is in the past
        String sql = "SELECT * FROM Loans WHERE LoanStatus = 'ACTIVE' AND DueDate < ? ORDER BY DueDate ASC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapRowToLoanWithDetails(rs, conn)); // Fetch details
                }
            }
        }
        return loans;
    }

    public boolean updateLoan(Loan loan) throws SQLException {
        // Comprehensive update, typically for return date, status, renewals
        String sql = "UPDATE Loans SET MemberID = ?, CopyID = ?, StaffID = ?, IssueDate = ?, DueDate = ?, ReturnDate = ?, LoanStatus = ?, RenewalsDone = ? WHERE LoanID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getMemberID());
            pstmt.setInt(2, loan.getCopyID());
            pstmt.setInt(3, loan.getStaffID());
            pstmt.setTimestamp(4, loan.getIssueDate());
            pstmt.setTimestamp(5, loan.getDueDate());
            if (loan.getReturnDate() != null) {
                pstmt.setTimestamp(6, loan.getReturnDate());
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }
            pstmt.setString(7, loan.getLoanStatus());
            pstmt.setInt(8, loan.getRenewalsDone());
            pstmt.setLong(9, loan.getLoanID());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean markLoanAsReturned(long loanId, Timestamp returnDate) throws SQLException {
        String sql = "UPDATE Loans SET ReturnDate = ?, LoanStatus = 'RETURNED' WHERE LoanID = ? AND LoanStatus = 'ACTIVE'"; // Or OVERDUE
         try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, returnDate != null ? returnDate : new Timestamp(System.currentTimeMillis()));
            pstmt.setLong(2, loanId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean updateLoanStatus(long loanId, String newStatus) throws SQLException {
        String sql = "UPDATE Loans SET LoanStatus = ? WHERE LoanID = ?";
         try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setLong(2, loanId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean renewLoanUpdate(long loanId, Timestamp newDueDate, int renewalsDone) throws SQLException {
        String sql = "UPDATE Loans SET DueDate = ?, RenewalsDone = ? WHERE LoanID = ? AND LoanStatus = 'ACTIVE'";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, newDueDate);
            pstmt.setInt(2, renewalsDone);
            pstmt.setLong(3, loanId);
            return pstmt.executeUpdate() > 0;
        }
    }


    private Loan mapRowToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanID(rs.getLong("LoanID"));
        loan.setMemberID(rs.getInt("MemberID"));
        loan.setCopyID(rs.getInt("CopyID"));
        loan.setStaffID(rs.getInt("StaffID"));
        loan.setIssueDate(rs.getTimestamp("IssueDate"));
        loan.setDueDate(rs.getTimestamp("DueDate"));
        loan.setReturnDate(rs.getTimestamp("ReturnDate"));
        loan.setLoanStatus(rs.getString("LoanStatus"));
        loan.setRenewalsDone(rs.getInt("RenewalsDone"));
        return loan;
    }

    // Helper to map row and fetch related entities (User, Copy)
    // Pass connection to reuse for related DAO calls within the same transaction context if needed
    private Loan mapRowToLoanWithDetails(ResultSet rs, Connection conn) throws SQLException {
        Loan loan = mapRowToLoan(rs);

        // Fetch Member (User) details
        // This creates N+1 if not careful; consider joining in main query for lists
        // or making UserDAO/CopyDAO methods accept an existing connection.
        // For simplicity here, making new calls (can be slow for large lists).
        if (userDAO != null) {
            userDAO.findUserById(loan.getMemberID()).ifPresent(loan::setMember);
            userDAO.findUserById(loan.getStaffID()).ifPresent(loan::setStaff);
        }

        // Fetch Copy details
        if (copyDAO != null) {
            copyDAO.getCopyById(loan.getCopyID()).ifPresent(copy -> {
                // Optionally fetch Book details for the Copy here too if CopyDAO handles it
                loan.setCopy(copy);
            });
        }
        return loan;
    }
     // Dummy Book model for compilation within LoanDAO methods.
    private static class Book {
        private String title;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
}

// Dummy CopyDAO class for compilation. Replace with actual implementation.
// This should be in its own file: com/librarysystem/dao/CopyDAO.java
// Ensure it has getCopyById method and Copy model has setBook.
class CopyDAO {
    public Optional<Copy> getCopyById(int copyId) throws SQLException {
        // Dummy implementation
        System.out.println("CopyDAO: Fetching copy by ID (dummy) " + copyId);
        return Optional.empty();
    }
    public boolean updateCopyStatus(int copyId, String status) throws SQLException { return false;}
    public boolean updateCopyCurrentLoan(int copyId, Long loanId) throws SQLException { return false; }

}
