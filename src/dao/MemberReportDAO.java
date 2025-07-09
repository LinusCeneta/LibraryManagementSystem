package dao;

import java.sql.*;
import java.util.*;
import java.util.Date;

import model.MemberReport;

public class MemberReportDAO {
    private Connection connection;

    public MemberReportDAO(Connection connection) {
        this.connection = connection;
    }

    public List<MemberReport> getActiveMembers(Date sinceDate) throws SQLException {
        List<MemberReport> reports = new ArrayList<>();
        String sql = "SELECT m.member_id, m.name, COUNT(l.loan_id) AS checkout_count " +
                     "FROM members m " +
                     "LEFT JOIN loan l ON m.member_id = l.member_id AND l.loan_date >= ? " +
                     "GROUP BY m.member_id, m.name " +
                     "ORDER BY checkout_count DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(sinceDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MemberReport report = new MemberReport();
                report.setMemberId(rs.getInt("member_id"));
                report.setName(rs.getString("name"));
                report.setCheckoutCount(rs.getInt("checkout_count"));
                reports.add(report);
            }
        }
        return reports;
    }
}
