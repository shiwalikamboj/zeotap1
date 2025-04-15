package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.demo.service.FileService;
import java.util.*;
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Autowired
    private FileService fileService;
    @PostMapping("/preview")
    public ResponseEntity<?> previewFile(@RequestParam("file") MultipartFile file) {
        try {
            List<String> headers = fileService.getFileHeaders(file);
            List<Map<String, String>> preview = fileService.previewData(file, 5);
            
            Map<String, Object> response = new HashMap<>();
            response.put("headers", headers);
            response.put("preview", preview);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("File processing failed: " + e.getMessage());
        }
    }
}
