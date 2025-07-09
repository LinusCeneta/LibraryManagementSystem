package com.librarysystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// Imports for Tomcat Connection Pool (Context and DataSource)
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnectionUtil {

    // Configuration for direct Derby connection (fallback or testing)
    private static final String DB_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static final String DB_CONNECTION_URL = "jdbc:derby:libraryDB;create=true"; // Embedded mode
    // For Derby Network Server: "jdbc:derby://localhost:1527/libraryDB;create=true";
    private static final String DB_USER = ""; // Not typically required for embedded Derby
    private static final String DB_PASSWORD = ""; // Not typically required for embedded Derby

    private static DataSource dataSource = null;

    static {
        // Initialize Tomcat JDBC DataSource (Preferred Method)
        try {
            Context initContext = new InitialContext();
            // Adjust "java:/comp/env/jdbc/LibraryDB" to match your context.xml <Resource> name
            Context envContext = (Context) initContext.lookup("java:comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/LibraryDB");
            System.out.println("DataSource initialized successfully via JNDI.");
        } catch (NamingException e) {
            System.err.println("NamingException for DataSource: " + e.getMessage());
            System.err.println("Falling back to direct JDBC connection for testing/dev.");
            // Fallback: Load driver for direct connection if DataSource fails (e.g., outside Tomcat)
            try {
                Class.forName(DB_DRIVER);
            } catch (ClassNotFoundException cnfe) {
                System.err.println("JDBC Driver not found: " + cnfe.getMessage());
                throw new RuntimeException("Failed to load JDBC driver and JNDI lookup failed.", cnfe);
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource != null) {
            // System.out.println("Attempting to get connection from DataSource...");
            Connection conn = dataSource.getConnection();
            // System.out.println("Connection obtained from DataSource: " + (conn != null));
            return conn;
        } else {
            // Fallback to direct DriverManager connection (useful for testing outside Tomcat)
            // System.out.println("Attempting to get direct JDBC connection...");
            Connection conn = DriverManager.getConnection(DB_CONNECTION_URL, DB_USER, DB_PASSWORD);
            // System.out.println("Direct JDBC connection obtained: " + (conn != null));
            return conn;
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void closeStatement(java.sql.Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
    }

    public static void closeResultSet(java.sql.ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing result set: " + e.getMessage());
            }
        }
    }

    public static void closeQuietly(Connection connection, java.sql.Statement statement, java.sql.ResultSet resultSet) {
        closeResultSet(resultSet);
        closeStatement(statement);
        closeConnection(connection);
    }
     public static void closeQuietly(Connection connection, java.sql.Statement statement) {
        closeStatement(statement);
        closeConnection(connection);
    }

    // Main method for testing direct connection (run this class directly)
    public static void main(String[] args) {
        System.out.println("Attempting to establish a test database connection...");
        try (Connection conn = DBConnectionUtil.getConnection()) {
            if (conn != null) {
                System.out.println("Successfully connected to the database!");
                System.out.println("Database Product Name: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("Database Product Version: " + conn.getMetaData().getDatabaseProductVersion());

                // Example: Check if a table exists (optional)
                // try (java.sql.ResultSet rs = conn.getMetaData().getTables(null, null, "SUPPLIERS", null)) {
                //     if (rs.next()) {
                //         System.out.println("Table 'SUPPLIERS' exists.");
                //     } else {
                //         System.out.println("Table 'SUPPLIERS' does not exist. Consider running schema creation.");
                //     }
                // }

            } else {
                System.err.println("Failed to connect to the database.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception during test connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
