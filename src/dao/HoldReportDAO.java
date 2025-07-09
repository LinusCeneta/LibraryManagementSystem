package dao;

import model.HoldReport;
import java.sql.*;
import java.util.*;

public class HoldReportDAO {
    private Connection connection;

    public HoldReportDAO(Connection connection) {
        this.connection = connection;
    }

    public List<HoldReport> getHoldStats() throws SQLException {
        List<HoldReport> reports = new ArrayList<>();
        String sql = "SELECT title, COUNT(*) AS hold_queue, AVG(DATEDIFF(available_date, hold_request_date)) AS avg_wait_days " +
                     "FROM holds " +
                     "GROUP BY title " +
                     "ORDER BY hold_queue DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                HoldReport report = new HoldReport();
                report.setTitle(rs.getString("title"));
                report.setHoldQueue(rs.getInt("hold_queue"));
                report.setAvgWaitDays(rs.getDouble("avg_wait_days"));
                reports.add(report);
            }
        }
        return reports;
    }
}
