package com.example.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.sql.ResultSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DataTransferService {
    
    @Autowired
    private ClickHouseService clickHouseService;
    

    public boolean testConnection(String host, int port, String database, String user, String token) {
        try (Connection conn = clickHouseService.getConnection(host, port, database, user, token)) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Object> transferFileToClickHouse(
            MultipartFile file, 
            String tableName, 
            List<String> selectedColumns,
            String host,
            int port,
            String database,
            String user,
            String token) throws Exception {
        
        validateFile(file);
        validateTableName(tableName);
        validateColumns(selectedColumns);

        try (Connection conn = clickHouseService.getConnection(host, port, database, user, token)) {
            createTableIfNotExists(conn, tableName, selectedColumns);
            int recordCount = processAndInsertData(conn, file, tableName, selectedColumns);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "File processed successfully");
            result.put("tableName", tableName);
            result.put("recordsProcessed", recordCount);
            return result;
        }
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new IllegalArgumentException("Only CSV files are supported");
        }
    }

    private void validateTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be empty");
        }
        if (!tableName.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException("Invalid table name format");
        }
    }

    private void validateColumns(List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Column list cannot be empty");
        }
        for (String column : columns) {
            if (!column.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
                throw new IllegalArgumentException("Invalid column name: " + column);
            }
        }
    }

    private void createTableIfNotExists(Connection conn, String tableName, List<String> columns) throws SQLException {
        String createTableQuery = generateCreateTableQuery(tableName, columns);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createTableQuery);
        }
    }

    private int processAndInsertData(Connection conn, MultipartFile file, String tableName, List<String> columns) throws IOException, SQLException {
        String insertQuery = generateInsertQuery(tableName, columns);
        int recordCount = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            
            for (CSVRecord record : csvParser) {
                int paramIndex = 1;
                for (String column : columns) {
                    String value = record.get(column);
                    pstmt.setString(paramIndex++, value != null ? value : "");
                }
                pstmt.addBatch();
                recordCount++;
                
                if (recordCount % 1000 == 0) {
                    pstmt.executeBatch();
                }
            }
            if (recordCount % 1000 != 0) {
                pstmt.executeBatch();
            }
        }
        return recordCount;
    }

    private String generateCreateTableQuery(String tableName, List<String> columns) {
        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        
        List<String> columnDefs = new ArrayList<>();
        for (String column : columns) {
            columnDefs.add("`" + column + "` String");
        }
        
        query.append(String.join(", ", columnDefs));
        query.append(") ENGINE = MergeTree() ORDER BY tuple()");
        
        return query.toString();
    }

    private String generateInsertQuery(String tableName, List<String> columns) {
        return "INSERT INTO " + tableName + " (`" + 
               String.join("`, `", columns) + 
               "`) VALUES (" + 
               String.join(", ", Collections.nCopies(columns.size(), "?")) + 
               ")";
    }

    public List<Map<String, Object>> getPreviewData(String tableName, String host, int port, String database, String user, String token, int limit) {
        validateTableName(tableName);
        if (limit <= 0) {
            throw new IllegalArgumentException("Preview limit must be greater than 0");
        }

        try (Connection conn = clickHouseService.getConnection(host, port, database, user, token);
             Statement stmt = conn.createStatement()) {
                
            String query = String.format("SELECT * FROM `%s` LIMIT %d", tableName, limit);
            var rs = stmt.executeQuery(query);
            
            List<Map<String, Object>> rows = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch preview data: " + e.getMessage(), e);
        }
    }

    public List<String> getTableColumns(String tableName, String host, int port, String database, String user, String token) {
        validateTableName(tableName);
        
        try (Connection conn = clickHouseService.getConnection(host, port, database, user, token);
             Statement stmt = conn.createStatement()) {
                
            String query = String.format("DESCRIBE `%s`", tableName);
            var rs = stmt.executeQuery(query);
            
            List<String> columns = new ArrayList<>();
            while (rs.next()) {
                columns.add(rs.getString("name"));
            }
            return columns;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch table columns: " + e.getMessage(), e);
        }
    }
}

