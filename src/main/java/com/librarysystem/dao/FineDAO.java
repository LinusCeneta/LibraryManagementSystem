package com.librarysystem.dao;

import com.librarysystem.model.Fine;
import com.librarysystem.util.DBConnectionUtil;
// Assuming User model exists for populating member details
import com.librarysystem.model.User;


import java.math.BigDecimal;
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

public class FineDAO {

    private UserDAO userDAO; // For fetching member details if needed

    public FineDAO() {
        this.userDAO = new UserDAO();
    }

    public FineDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Fine createFine(Fine fine) throws SQLException {
        String sql = "INSERT INTO Fines (LoanID, MemberID, FineAmount, AmountPaid, DateLevied, DatePaid, FineStatus, Reason, Notes, ProcessedByStaffID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (fine.getLoanID() != null) {
                pstmt.setLong(1, fine.getLoanID());
            } else {
                pstmt.setNull(1, Types.BIGINT);
            }
            pstmt.setInt(2, fine.getMemberID());
            pstmt.setBigDecimal(3, fine.getFineAmount());
            pstmt.setBigDecimal(4, fine.getAmountPaid() != null ? fine.getAmountPaid() : BigDecimal.ZERO);
            pstmt.setTimestamp(5, fine.getDateLevied() != null ? fine.getDateLevied() : new Timestamp(System.currentTimeMillis()));

            if (fine.getDatePaid() != null) {
                pstmt.setTimestamp(6, fine.getDatePaid());
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }
            pstmt.setString(7, fine.getFineStatus() != null ? fine.getFineStatus() : "UNPAID");
            pstmt.setString(8, fine.getReason());
            pstmt.setString(9, fine.getNotes());

            if (fine.getProcessedByStaffID() != null) {
                pstmt.setInt(10, fine.getProcessedByStaffID());
            } else {
                pstmt.setNull(10, Types.INTEGER);
            }

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fine.setFineID(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating fine failed, no ID obtained.");
                }
            }
            return fine;
        }
    }

    public Optional<Fine> getFineById(long fineId) throws SQLException {
        String sql = "SELECT * FROM Fines WHERE FineID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, fineId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToFineWithDetails(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Fine> findFinesByMemberId(int memberId) throws SQLException {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM Fines WHERE MemberID = ? ORDER BY DateLevied DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapRowToFineWithDetails(rs));
                }
            }
        }
        return fines;
    }

    public List<Fine> findFinesByMemberIdAndStatus(int memberId, String status) throws SQLException {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM Fines WHERE MemberID = ? AND FineStatus = ? ORDER BY DateLevied DESC";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setString(2, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapRowToFineWithDetails(rs));
                }
            }
        }
        return fines;
    }

    public BigDecimal getTotalUnpaidFinesForMember(int memberId) throws SQLException {
        String sql = "SELECT SUM(FineAmount - AmountPaid) FROM Fines WHERE MemberID = ? AND FineStatus IN ('UNPAID', 'PARTIALLY_PAID')";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }


    public boolean updateFine(Fine fine) throws SQLException {
        // Typically used to update payment status, amount paid, notes
        String sql = "UPDATE Fines SET LoanID = ?, MemberID = ?, FineAmount = ?, AmountPaid = ?, DateLevied = ?, DatePaid = ?, FineStatus = ?, Reason = ?, Notes = ?, ProcessedByStaffID = ? WHERE FineID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (fine.getLoanID() != null) pstmt.setLong(1, fine.getLoanID()); else pstmt.setNull(1, Types.BIGINT);
            pstmt.setInt(2, fine.getMemberID());
            pstmt.setBigDecimal(3, fine.getFineAmount());
            pstmt.setBigDecimal(4, fine.getAmountPaid());
            pstmt.setTimestamp(5, fine.getDateLevied());
            if (fine.getDatePaid() != null) pstmt.setTimestamp(6, fine.getDatePaid()); else pstmt.setNull(6, Types.TIMESTAMP);
            pstmt.setString(7, fine.getFineStatus());
            pstmt.setString(8, fine.getReason());
            pstmt.setString(9, fine.getNotes());
            if (fine.getProcessedByStaffID() != null) pstmt.setInt(10, fine.getProcessedByStaffID()); else pstmt.setNull(10, Types.INTEGER);
            pstmt.setLong(11, fine.getFineID());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean recordFinePayment(long fineId, BigDecimal paymentAmount, int staffId) throws SQLException {
        Optional<Fine> fineOpt = getFineById(fineId);
        if (!fineOpt.isPresent()) {
            throw new SQLException("Fine not found with ID: " + fineId);
        }
        Fine fine = fineOpt.get();

        BigDecimal newAmountPaid = fine.getAmountPaid().add(paymentAmount);
        String newStatus = fine.getFineStatus();
        Timestamp datePaid = fine.getDatePaid();

        if (newAmountPaid.compareTo(fine.getFineAmount()) >= 0) {
            newStatus = "PAID";
            datePaid = new Timestamp(System.currentTimeMillis());
            newAmountPaid = fine.getFineAmount(); // Ensure amount paid doesn't exceed fine amount
        } else if (newAmountPaid.compareTo(BigDecimal.ZERO) > 0) {
            newStatus = "PARTIALLY_PAID";
        }

        String sql = "UPDATE Fines SET AmountPaid = ?, FineStatus = ?, DatePaid = ?, ProcessedByStaffID = ? WHERE FineID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newAmountPaid);
            pstmt.setString(2, newStatus);
            if (datePaid != null) pstmt.setTimestamp(3, datePaid); else pstmt.setNull(3, Types.TIMESTAMP);
            pstmt.setInt(4, staffId);
            pstmt.setLong(5, fineId);

            boolean updated = pstmt.executeUpdate() > 0;
            // TODO: After successful payment, update User's CurrentFineBalance. This should ideally be in a service layer with transaction.
            return updated;
        }
    }

    public boolean waiveFine(long fineId, int staffId, String reasonNotes) throws SQLException {
        String sql = "UPDATE Fines SET FineStatus = 'WAIVED', AmountPaid = FineAmount, DatePaid = ?, ProcessedByStaffID = ?, Notes = ? WHERE FineID = ?";
         try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, staffId);
            pstmt.setString(3, reasonNotes);
            pstmt.setLong(4, fineId);

            boolean updated = pstmt.executeUpdate() > 0;
            // TODO: After successful waiver, update User's CurrentFineBalance. Service layer.
            return updated;
        }
    }


    // Deleting fines might not be common; usually waived.
    public boolean deleteFine(long fineId) throws SQLException {
        String sql = "DELETE FROM Fines WHERE FineID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, fineId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Fine mapRowToFine(ResultSet rs) throws SQLException {
        Fine fine = new Fine();
        fine.setFineID(rs.getLong("FineID"));
        fine.setLoanID(rs.getObject("LoanID", Long.class)); // Handle NULL
        fine.setMemberID(rs.getInt("MemberID"));
        fine.setFineAmount(rs.getBigDecimal("FineAmount"));
        fine.setAmountPaid(rs.getBigDecimal("AmountPaid"));
        fine.setDateLevied(rs.getTimestamp("DateLevied"));
        fine.setDatePaid(rs.getTimestamp("DatePaid"));
        fine.setFineStatus(rs.getString("FineStatus"));
        fine.setReason(rs.getString("Reason"));
        fine.setNotes(rs.getString("Notes"));
        fine.setProcessedByStaffID(rs.getObject("ProcessedByStaffID", Integer.class));
        return fine;
    }

    private Fine mapRowToFineWithDetails(ResultSet rs) throws SQLException {
        Fine fine = mapRowToFine(rs);
        // Optionally populate User (Member) details
        if (userDAO != null) {
            userDAO.findUserById(fine.getMemberID()).ifPresent(fine::setMember);
            if (fine.getProcessedByStaffID() != null) {
                userDAO.findUserById(fine.getProcessedByStaffID()).ifPresent(fine::setStaffProcessedBy);
            }
        }
        // Populating Loan details would require LoanDAO and could be complex here.
        // Usually done at service layer if needed.
        return fine;
    }
}
