package dao;

import model.HoldReservation;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoldDAO {
    private Connection connection;

    public HoldDAO() throws SQLException {
        connection = DBConnection.getConnection();
    }

    public void addHold(HoldReservation hold) throws SQLException {
        String sql = "INSERT INTO HoldReservation (member_id, copy_id, request_date, status, pickup_expiry_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, hold.getMemberId());
            ps.setInt(2, hold.getCopyId());
            ps.setDate(3, hold.getRequestDate());
            ps.setString(4, hold.getStatus());
            ps.setDate(5, hold.getPickupExpiryDate());
            ps.executeUpdate();
        }
    }

    public void updateHoldStatus(int holdId, String status) throws SQLException {
        String sql = "UPDATE HoldReservation SET status = ? WHERE hold_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, holdId);
            ps.executeUpdate();
        }
    }

    public List<HoldReservation> getHoldsByMember(int memberId) throws SQLException {
        List<HoldReservation> holds = new ArrayList<>();
        String sql = "SELECT * FROM HoldReservation WHERE member_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoldReservation hold = new HoldReservation();
                hold.setHoldId(rs.getInt("hold_id"));
                hold.setMemberId(rs.getInt("member_id"));
                hold.setCopyId(rs.getInt("copy_id"));
                hold.setRequestDate(rs.getDate("request_date"));
                hold.setStatus(rs.getString("status"));
                hold.setPickupExpiryDate(rs.getDate("pickup_expiry_date"));
                holds.add(hold);
            }
        }
        return holds;
    }

	public void placeHold(String memberId, String copyId) {
		// TODO Auto-generated method stub
		
	}
}
