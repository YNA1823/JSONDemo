package com.demo.jsondemo.utils;

import com.demo.jsondemo.config.ConfigManager;
import com.demo.jsondemo.config.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseUtils {
    private static final Logger logger = LogManager.getLogger(DatabaseUtils.class);
    private static Connection connection;

    public static void connect() {
        DatabaseConfig config = ConfigManager.getInstance().getDatabaseConfig();
        if (!config.isEnabled()) {
            logger.info("Database is disabled in config");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    config.getUrl(),
                    config.getUsername(),
                    config.getPassword()
            );
            logger.info("Database connected successfully");
            initTables();
        } catch (ClassNotFoundException e) {
            logger.error("MySQL Driver not found", e);
            throw new RuntimeException("MySQL Driver not found", e);
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    public static void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database disconnected");
            } catch (SQLException e) {
                logger.error("Failed to close database connection", e);
            }
        }
    }

    private static void initTables() {
        String createTestDataTable = "CREATE TABLE IF NOT EXISTS test_data (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "test_case VARCHAR(100) NOT NULL, " +
                "endpoint VARCHAR(200), " +
                "method VARCHAR(10), " +
                "expected_status INT, " +
                "description VARCHAR(500), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String createTestResultsTable = "CREATE TABLE IF NOT EXISTS test_results (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "test_name VARCHAR(200) NOT NULL, " +
                "test_class VARCHAR(200), " +
                "status VARCHAR(20) NOT NULL, " +
                "duration_ms BIGINT, " +
                "error_message TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTestDataTable);
            stmt.execute(createTestResultsTable);
            logger.info("Database tables initialized");
        } catch (SQLException e) {
            logger.error("Failed to initialize tables", e);
        }
    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    results.add(row);
                }
            }
        } catch (SQLException e) {
            logger.error("Query execution failed: " + sql, e);
            throw new RuntimeException("Query failed", e);
        }

        return results;
    }

    public static int executeUpdate(String sql, Object... params) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            setParameters(pstmt, params);
            int rows = pstmt.executeUpdate();
            logger.debug("Executed update: {}, affected rows: {}", sql, rows);
            return rows;
        } catch (SQLException e) {
            logger.error("Update execution failed: " + sql, e);
            throw new RuntimeException("Update failed", e);
        }
    }

    public static long insertAndGetId(String sql, Object... params) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(pstmt, params);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Insert execution failed: " + sql, e);
            throw new RuntimeException("Insert failed", e);
        }
        return -1;
    }

    private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    public static void insertTestResult(String testName, String testClass, String status, long durationMs, String errorMessage) {
        if (!isConnected()) return;

        String sql = "INSERT INTO test_results (test_name, test_class, status, duration_ms, error_message) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(sql, testName, testClass, status, durationMs, errorMessage);
        logger.info("Test result saved: {} - {}", testName, status);
    }

    public static List<Map<String, Object>> getTestData(String testCase) {
        if (!isConnected()) return new ArrayList<>();

        String sql = "SELECT * FROM test_data WHERE test_case = ?";
        return executeQuery(sql, testCase);
    }

    public static void insertTestData(String testCase, String endpoint, String method, int expectedStatus, String description) {
        if (!isConnected()) return;

        String sql = "INSERT INTO test_data (test_case, endpoint, method, expected_status, description) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(sql, testCase, endpoint, method, expectedStatus, description);
    }

    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
