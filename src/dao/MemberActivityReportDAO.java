package dao;

import model.MemberActivityReport;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class MemberActivityReportDAO {
    private Connection conn;

    public MemberActivityReportDAO(Connection conn) {
        this.conn = conn;
    }

    public List<MemberActivityReport> getFineRevenue(Date startDate, Date endDate) throws SQLException {
        List<MemberActivityReport> reports = new ArrayList<>();

        String sql = "SELECT m.member_id, m.name, SUM(f.amount) AS total_fines, AVG(f.amount) AS average_fine " +
                     "FROM Member m JOIN Fine f ON m.member_id = f.member_id " +
                     "WHERE f.payment_date BETWEEN ? AND ? " +
                     "GROUP BY m.member_id, m.name";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(startDate.getTime()));
            ps.setDate(2, new java.sql.Date(endDate.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MemberActivityReport report = new MemberActivityReport();
                    report.setMemberId(rs.getString("member_id"));
                    report.setMemberName(rs.getString("name"));
                    report.setTotalFines(rs.getDouble("total_fines"));
                    report.setAverageFine(rs.getDouble("average_fine"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    public List<MemberActivityReport> getActiveInactiveMembers() throws SQLException {
        List<MemberActivityReport> reports = new ArrayList<>();

        // SQL assumes checkout_date in Checkouts table and member_id in Member table.
        String sql = 
            "SELECT 'Active' AS status, COUNT(DISTINCT m.member_id) AS member_count " +
            "FROM Member m JOIN Checkout c ON m.member_id = c.member_id " +
            "WHERE c.checkout_date >= CURRENT_DATE - INTERVAL '6' MONTH " +
            "UNION ALL " +
            "SELECT 'Inactive' AS status, COUNT(m.member_id) AS member_count " +
            "FROM Member m " +
            "WHERE m.member_id NOT IN (" +
            "  SELECT DISTINCT member_id FROM Checkout WHERE checkout_date >= CURRENT_DATE - INTERVAL '6' MONTH" +
            ")";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MemberActivityReport report = new MemberActivityReport();
                // Use memberName field to store "Active" or "Inactive" status label for this report
                report.setMemberName(rs.getString("status"));
                // We set memberId to null (not applicable here)
                report.setMemberId(null);
                report.setTotalFines(rs.getDouble("member_count"));
                // averageFine not applicable here, set to 0 or -1 to indicate N/A
                report.setAverageFine(0);
                reports.add(report);
            }
        }
        return reports;
    }
}
