package dao;

import model.Loan;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private Connection connection;

    public LoanDAO() throws SQLException {
        connection = DBConnection.getConnection();
    }

    public void addLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO Loan (member_id, copy_id, issue_date, due_date, staff_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, loan.getMemberId());
            ps.setInt(2, loan.getCopyId());
            ps.setDate(3, loan.getIssueDate());
            ps.setDate(4, loan.getDueDate());
            ps.setInt(5, loan.getStaffId());
            ps.setString(6, loan.getStatus());
            ps.executeUpdate();
        }
    }

    public void updateLoan(Loan loan) throws SQLException {
        String sql = "UPDATE Loan SET return_date = ?, status = ? WHERE loan_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, loan.getReturnDate());
            ps.setString(2, loan.getStatus());
            ps.setInt(3, loan.getLoanId());
            ps.executeUpdate();
        }
    }

    public Loan getLoanById(int loanId) throws SQLException {
        String sql = "SELECT * FROM Loan WHERE loan_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setMemberId(rs.getInt("member_id"));
                loan.setCopyId(rs.getInt("copy_id"));
                loan.setIssueDate(rs.getDate("issue_date"));
                loan.setDueDate(rs.getDate("due_date"));
                loan.setReturnDate(rs.getDate("return_date"));
                loan.setStaffId(rs.getInt("staff_id"));
                loan.setStatus(rs.getString("status"));
                return loan;
            }
        }
        return null;
    }

    public List<Loan> getLoansByMember(int memberId) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loan WHERE member_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Loan loan = new Loan();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setMemberId(rs.getInt("member_id"));
                loan.setCopyId(rs.getInt("copy_id"));
                loan.setIssueDate(rs.getDate("issue_date"));
                loan.setDueDate(rs.getDate("due_date"));
                loan.setReturnDate(rs.getDate("return_date"));
                loan.setStaffId(rs.getInt("staff_id"));
                loan.setStatus(rs.getString("status"));
                loans.add(loan);
            }
        }
        return loans;
    }

    public void issueLoan(String memberId, String copyId, String staffId) throws SQLException {
        String insertLoanSQL = "INSERT INTO Loan (member_id, copy_id, staff_id, issue_date, due_date, status) "
                + "VALUES (?, ?, ?, CURRENT_DATE, CURRENT_DATE + 14, 'Active')";
        try (PreparedStatement pstmt = connection.prepareStatement(insertLoanSQL)) {
            pstmt.setString(1, memberId);
            pstmt.setString(2, copyId);
            pstmt.setString(3, staffId);
            pstmt.executeUpdate();
        }

        String updateCopySQL = "UPDATE Copy SET status = 'Checked Out' WHERE copy_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateCopySQL)) {
            pstmt.setString(1, copyId);
            pstmt.executeUpdate();
        }
    }

    public void returnLoan(String copyId, String staffId) throws SQLException {
        // Mark loan as returned (set return_date and status)
        String updateLoanSQL = "UPDATE Loan SET return_date = CURRENT_DATE, status = 'Returned' "
                + "WHERE copy_id = ? AND status = 'Active'";
        try (PreparedStatement pstmt = connection.prepareStatement(updateLoanSQL)) {
            pstmt.setString(1, copyId);
            pstmt.executeUpdate();
        }

        // Update copy status to Available
        String updateCopySQL = "UPDATE Copy SET status = 'Available' WHERE copy_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateCopySQL)) {
            pstmt.setString(1, copyId);
            pstmt.executeUpdate();
        }
    }

    public void renewLoan(String loanId) throws SQLException {
        // Extend due_date by 14 days
        String renewSQL = "UPDATE Loan SET due_date = due_date + 14 WHERE loan_id = ? AND status = 'Active'";
        try (PreparedStatement pstmt = connection.prepareStatement(renewSQL)) {
            pstmt.setString(1, loanId);
            pstmt.executeUpdate();
        }
    }

    public void processReturn(String copyId) throws SQLException {
        // This is usually similar to returnLoan() without staffId
        String updateLoanSQL = "UPDATE Loan SET return_date = CURRENT_DATE, status = 'Returned' "
                + "WHERE copy_id = ? AND status = 'Active'";
        try (PreparedStatement pstmt = connection.prepareStatement(updateLoanSQL)) {
            pstmt.setString(1, copyId);
            pstmt.executeUpdate();
        }

        String updateCopySQL = "UPDATE Copy SET status = 'Available' WHERE copy_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateCopySQL)) {
            pstmt.setString(1, copyId);
            pstmt.executeUpdate();
        }
    }
}
