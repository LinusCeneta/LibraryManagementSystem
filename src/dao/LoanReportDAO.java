package dao;

import java.sql.*;
import java.util.Date;
import java.util.*;
import model.CirculationReport;

public class LoanReportDAO {

    private Connection connection;

    public LoanReportDAO(Connection connection) {
        this.connection = connection;
    }

    public List<CirculationReport> getCheckoutCounts(String branch, Date startDate, Date endDate) throws SQLException {
        List<CirculationReport> reports = new ArrayList<>();
        String sql = "SELECT DATE(loan_date) AS loan_date, COUNT(*) AS checkout_count " +
                     "FROM loan " +
                     "WHERE loan_date BETWEEN ? AND ? " +
                     (branch != null && !branch.isEmpty() ? "AND branch = ? " : "") +
                     "GROUP BY DATE(loan_date) " +
                     "ORDER BY loan_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            if (branch != null && !branch.isEmpty()) {
                stmt.setString(3, branch);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CirculationReport report = new CirculationReport();
                report.setLoanDate(rs.getDate("loan_date"));
                report.setCheckoutCount(rs.getInt("checkout_count"));
                reports.add(report);
            }
        }
        return reports;
    }
}
