package servlet;

import utils.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class AdHocReportServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String whereClause = request.getParameter("whereClause");
        String export = request.getParameter("export");

        if (whereClause == null) {
            whereClause = "";
        }

        // Basic validation: disallow semicolons or DROP, DELETE, UPDATE for safety
        String lower = whereClause.toLowerCase();
        if (lower.contains(";") || lower.contains("drop") || lower.contains("delete") || lower.contains("update") || lower.contains("--")) {
            request.setAttribute("error", "Unsafe query criteria detected.");
            request.getRequestDispatcher("/adhocReport.jsp").forward(request, response);
            return;
        }

        String baseQuery = "SELECT * FROM items ";  // Example base table

        String sql = baseQuery;
        if (!whereClause.trim().isEmpty()) {
            sql += " WHERE " + whereClause;
        }
        sql += " LIMIT 100"; // Safety limit

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columns; i++) {
                    row.put(md.getColumnLabel(i), rs.getObject(i));
                }
                rows.add(row);
            }

            request.setAttribute("columns", getColumnLabels(rs));
            request.setAttribute("rows", rows);

            if ("csv".equalsIgnoreCase(export)) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"adhoc_report.csv\"");

                StringBuilder csv = new StringBuilder();

                // Headers
                List<String> cols = getColumnLabels(rs);
                csv.append(String.join(",", cols)).append("\n");

                // Rows
                for (Map<String, Object> row : rows) {
                    for (int i = 0; i < cols.size(); i++) {
                        Object val = row.get(cols.get(i));
                        csv.append(val != null ? val.toString() : "");
                        if (i < cols.size() - 1) csv.append(",");
                    }
                    csv.append("\n");
                }

                response.getWriter().write(csv.toString());
            } else {
                request.getRequestDispatcher("/adhocReport.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "SQL error: " + e.getMessage());
            request.getRequestDispatcher("/adhocReport.jsp").forward(request, response);
        }
    }

    private List<String> getColumnLabels(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        List<String> columns = new ArrayList<>();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            columns.add(md.getColumnLabel(i));
        }
        return columns;
    }
}
