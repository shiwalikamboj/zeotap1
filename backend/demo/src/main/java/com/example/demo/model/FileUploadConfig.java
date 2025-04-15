package com.example.demo.model;

import java.util.List;

public class FileUploadConfig {
    private String delimiter;
    private List<String> selectedColumns;
    private String targetTable;

    // Getters
    public String getDelimiter() {
        return delimiter;
    }

    public List<String> getSelectedColumns() {
        return selectedColumns;
    }

    public String getTargetTable() {
        return targetTable;
    }

    // Setters
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setSelectedColumns(List<String> selectedColumns) {
        this.selectedColumns = selectedColumns;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }
}
