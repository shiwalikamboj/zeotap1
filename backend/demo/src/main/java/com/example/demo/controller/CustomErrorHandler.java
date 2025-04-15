package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorHandler implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<?> handleError() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Invalid endpoint. Available endpoints: /api/transfer/test-connection, /api/transfer/file-to-clickhouse, /api/transfer/preview/{tableName}");
        
        return ResponseEntity.status(404).body(response);
    }
}
