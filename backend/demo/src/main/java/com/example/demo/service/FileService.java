package com.example.demo.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    
    public List<String> getFileHeaders(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            return new ArrayList<>(csvParser.getHeaderNames());
        }
    }

    public List<Map<String, String>> previewData(MultipartFile file, int limit) throws IOException {
        List<Map<String, String>> preview = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            
            for (CSVRecord record : csvParser) {
                if (preview.size() >= limit) break;
                
                Map<String, String> row = new HashMap<>();
                csvParser.getHeaderNames().forEach(header -> 
                    row.put(header, record.get(header))
                );
                preview.add(row);
            }
        }
        return preview;
    }

    public long processFile(MultipartFile file) throws IOException {
        long rowCount = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            
            for (CSVRecord record : csvParser) {
                rowCount++;
            }
        }
        return rowCount;
    }

    public Map<String, String> inferColumnTypes(MultipartFile file, int sampleSize) throws IOException {
        Map<String, String> columnTypes = new HashMap<>();
        List<Map<String, String>> sampleData = previewData(file, sampleSize);
        
        if (!sampleData.isEmpty()) {
            Map<String, String> firstRow = sampleData.get(0);
            
            for (String header : firstRow.keySet()) {
                boolean isNumeric = true;
                boolean isDateTime = true;
                
                for (Map<String, String> row : sampleData) {
                    String value = row.get(header);
                    if (value != null && !value.trim().isEmpty()) {
                        if (!isNumeric(value)) isNumeric = false;
                        if (!isDateTime(value)) isDateTime = false;
                    }
                }
                
                if (isDateTime) {
                    columnTypes.put(header, "DateTime");
                } else if (isNumeric) {
                    columnTypes.put(header, "Float64");
                } else {
                    columnTypes.put(header, "String");
                }
            }
        }
        
        return columnTypes;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDateTime(String str) {
        try {
            java.time.LocalDateTime.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}