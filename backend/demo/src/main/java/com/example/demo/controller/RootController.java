package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class RootController {

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "online");
        response.put("service", "ClickHouse File Ingestor API");
        response.put("version", "1.0.0");
        response.put("documentation", new HashMap<String, String>() {{
            put("test_connection", "/api/transfer/test-connection - Test your ClickHouse connection");
            put("file_upload", "/api/transfer/file-to-clickhouse - Upload and ingest files to ClickHouse");
            put("preview", "/api/transfer/preview/{tableName} - Preview table contents");
        }});
        return ResponseEntity.ok(response);
    }
}
