package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class WelcomeController {
    
    @GetMapping("/")
    public Map<String, Object> welcome() {
        return Map.of(
            "message", "Sports Investment Tracker API is running!",
            "status", "active", 
            "timestamp", LocalDateTime.now().toString(),
            "endpoints", Map.of(
                "health", "/actuator/health",
                "register", "/api/auth/register",
                "login", "/api/auth/login",
                "schedules", "/api/sports/schedules"
            )
        );
    }
}
