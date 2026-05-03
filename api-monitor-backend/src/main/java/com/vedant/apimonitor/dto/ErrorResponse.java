package com.vedant.apimonitor.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {


    private int status;
    private String message;   // actual error message
    private String code;      // error code
    private LocalDateTime timestamp;
    private Map<String, String> errors; // for validation

    // Constructor (basic)
    public ErrorResponse(int status, String message, String code, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
    }

    // Constructor (with validation errors)
    public ErrorResponse(int status, String message, String code,
                         LocalDateTime timestamp, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    // Getters & Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}
