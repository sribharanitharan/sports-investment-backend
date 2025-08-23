package com.example.demo.dto.response;

public class AuthResponse {
    private String token;
    private String username;
    private String message;
    
    // Default constructor
    public AuthResponse() {}
    
    // Constructor for successful authentication (with token)
    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
        this.message = null;
    }
    
    // Constructor for error messages
    public AuthResponse(String message) {
        this.token = null;
        this.username = null;
        this.message = message;
    }
    
    // Constructor with all fields
    public AuthResponse(String token, String username, String message) {
        this.token = token;
        this.username = username;
        this.message = message;
    }
    
    // Getters and Setters
    public String getToken() { 
        return token; 
    }
    
    public void setToken(String token) { 
        this.token = token; 
    }
    
    public String getUsername() { 
        return username; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String message) { 
        this.message = message; 
    }
    
    // Helper methods
    public boolean isSuccess() {
        return token != null && username != null;
    }
    
    public boolean hasError() {
        return message != null && token == null;
    }
    
    // Debug method
    @Override
    public String toString() {
        return "AuthResponse{" +
                "token=" + (token != null ? "[TOKEN_PRESENT]" : "null") +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
