package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.DataTransferService;

@RestController
@RequestMapping("/api/transfer")
@CrossOrigin(origins = "http://localhost:3000")
public class DataTransferController {

    @Autowired
    private DataTransferService dataTransferService;

    @GetMapping("")
    public ResponseEntity<Map<String, String>> rootEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "running");
        response.put("message", "ClickHouse File Ingestor API");
        response.put("endpoints", "/test-connection, /file-to-clickhouse, /preview");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/file-to-clickhouse")
    public ResponseEntity<?> uploadFileToClickHouse(
            @RequestParam("file") MultipartFile file,
            @RequestParam("tableName") String tableName,
            @RequestParam("selectedColumns") List<String> selectedColumns,
            @RequestParam("host") String host,
            @RequestParam("port") int port,
            @RequestParam("database") String database,
            @RequestParam("user") String user,
            @RequestParam("token") String token) {
        try {
            Map<String, Object> result = dataTransferService.transferFileToClickHouse(
                file, tableName, selectedColumns, host, port, database, user, token);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/preview")
    public ResponseEntity<?> previewData(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> connectionDetails = (Map<String, Object>) request.get("connectionDetails");
            String tableName = (String) request.get("tableName");
            
            // Extract connection details
            String host = (String) connectionDetails.get("host");
            int port = ((Number) connectionDetails.get("port")).intValue();
            String database = (String) connectionDetails.get("database");
            String user = (String) connectionDetails.get("user");
            String token = (String) connectionDetails.get("token");
            
            List<Map<String, Object>> previewData = dataTransferService.getPreviewData(
                tableName,
                host,
                port,
                database,
                user,
                token,
                100  // limit
            );
            
            return ResponseEntity.ok(Map.of("data", previewData));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test-connection")
    public ResponseEntity<?> testConnection(
            @RequestParam(required = true) String host,
            @RequestParam(required = true) int port,
            @RequestParam(required = true) String database,
            @RequestParam(required = true) String user,
            @RequestParam(required = true) String token) {
        try {
            boolean isConnected = dataTransferService.testConnection(host, port, database, user, token);
            Map<String, Object> result = new HashMap<>();
            result.put("status", isConnected ? "success" : "failed");
            result.put("message", isConnected ? "Connection successful" : "Connection failed: Please check your ClickHouse server and credentials");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Connection error: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}

