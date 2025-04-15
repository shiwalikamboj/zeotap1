package com.example.demo.controller;

import java.sql.Connection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.ClickHouseConnection;
import com.example.demo.service.ClickHouseService;

@RestController
@RequestMapping("/api/ingestion")
public class DataIngestionController {

    @Autowired
    private ClickHouseService clickHouseService;

    @PostMapping("/connect/clickhouse")
    public ResponseEntity<?> testClickHouseConnection(@RequestBody ClickHouseConnection connection) {
        try {
            Connection conn = clickHouseService.getConnection(
                connection.getHost(),
                connection.getPort(),
                connection.getDatabase(),
                connection.getUser(),
                connection.getJwtToken()
            );
            List<String> tables = clickHouseService.getTables(conn);
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Connection failed: " + e.getMessage());
        }
    }
}
