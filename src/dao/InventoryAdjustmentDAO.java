package dao;

import java.sql.*;
import model.InventoryAdjustment;
import utils.DBConnection;

public class InventoryAdjustmentDAO {

    public void addAdjustment(InventoryAdjustment adj) throws SQLException {
        String sql = "INSERT INTO InventoryAdjustment (copy_id, adjustment_type, reason, adjustment_date, user) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adj.getCopyId());
            stmt.setString(2, adj.getAdjustmentType());
            stmt.setString(3, adj.getReason());
            stmt.setDate(4, adj.getAdjustmentDate());
            stmt.setString(5, adj.getUser());
            stmt.executeUpdate();
        }
    }
}
