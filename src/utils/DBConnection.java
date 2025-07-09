package utils;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());
    private static final String URL = "jdbc:derby://localhost:1527/library;create=true";
    private static final String USER = "yak";
    private static final String PASSWORD = "app";

    static {
        initializeDriver();
    }

    private static void initializeDriver() {
        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
            logger.info("✅ Derby Client JDBC Driver registered successfully");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Failed to register Derby Client Driver", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false); // Enable transaction control
            logger.info("✅ Database connection established");
            return conn;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "❌ Failed to establish database connection", e);
            throw e;
        }
    }

    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
            logger.info("✅ Database resources closed successfully");
        } catch (SQLException e) {
            logger.log(Level.WARNING, "❌ Error closing database resources", e);
        }
    }
}


