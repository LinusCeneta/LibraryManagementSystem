package dao;

import model.Fine;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {
    private Connection connection;

    public FineDAO() throws SQLException {
        connection = DBConnection.getConnection();
    }

    public void addFine(Fine fine) throws SQLException {
        String sql = "INSERT INTO Fine (member_id, loan_id, amount, date_levied, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, fine.getMemberId());
            ps.setInt(2, fine.getLoanId());
            ps.setBigDecimal(3, fine.getAmount());
            ps.setDate(4, fine.getDateLevied());
            ps.setString(5, fine.getStatus());
            ps.executeUpdate();
        }
    }

    public void updateFineStatus(int fineId, String status) throws SQLException {
        String sql = "UPDATE Fine SET status = ? WHERE fine_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, fineId);
            ps.executeUpdate();
        }
    }

    public List<Fine> getFinesByMember(int memberId) throws SQLException {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM Fine WHERE member_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Fine fine = new Fine();
                fine.setFineId(rs.getInt("fine_id"));
                fine.setMemberId(rs.getInt("member_id"));
                fine.setLoanId(rs.getInt("loan_id"));
                fine.setAmount(rs.getBigDecimal("amount"));
                fine.setDateLevied(rs.getDate("date_levied"));
                fine.setStatus(rs.getString("status"));
                fines.add(fine);
            }
        }
        return fines;
    }

	public void recordFine(String memberId, String loanId, double amount) {
		// TODO Auto-generated method stub
		
	}
}
