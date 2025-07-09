package dao;

import java.sql.*;
import java.util.*;

public class AdHocReportDAO {
    private Connection connection;

    public AdHocReportDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Map<String, Object>> runCustomQuery(String baseQuery, List<Object> parameters) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(baseQuery)) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;
    }
}
