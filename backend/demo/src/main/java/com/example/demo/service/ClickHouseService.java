package com.example.demo.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ClickHouseService {
    public List<String> getTables(Connection connection) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }
    public Connection getConnection(String host, int port, String database, String user, String token) throws SQLException {
        try {
            Class.forName("com.clickhouse.jdbc.ClickHouseDriver");
            // Use HTTP protocol with port 8123
            String url = String.format("jdbc:clickhouse:http://%s:%d/%s", 
                host, 
                8123, // Always use HTTP port 8123 regardless of input
                database);     
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", token);
            properties.setProperty("ssl", "false");
            properties.setProperty("connectTimeout", "10000");
            properties.setProperty("socketTimeout", "10000");      
            System.out.println("Attempting connection to: " + url); // Debug log
            return DriverManager.getConnection(url, properties);
        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage()); // Debug log
            throw new SQLException("ClickHouse connection failed: " + e.getMessage(), e);
        }
    }


    public boolean testConnection(String host, int port, String database, String user, String token) {
        try (Connection conn = getConnection(host, port, database, user, token)) {
            // Try a simple query to verify the connection
            try (Statement stmt = conn.createStatement()) {
                stmt.executeQuery("SELECT 1");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    public void insertFileMetadata(Connection connection, String filename, String status, long rowCount) throws SQLException {
        String sql = "INSERT INTO file_metadata (id, filename, upload_timestamp, status, row_count) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setString(2, filename);
            stmt.setObject(3, LocalDateTime.now());
            stmt.setString(4, status);
            stmt.setLong(5, rowCount);
            stmt.executeUpdate();
        }
    }

    public List<Map<String, Object>> getPreviewData(Connection connection, String tableName, int limit) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s LIMIT %d", tableName, limit);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new java.util.HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }
        }
        return results;
    }
}
