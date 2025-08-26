package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    @GetMapping("/api/debug")
    public Map<String, String> debugEnv() {
        Map<String, String> env = new HashMap<>();
        env.put("MONGODB_URI", System.getenv("MONGODB_URI"));
        env.put("SPRING_DATA_MONGODB_URI", System.getenv("SPRING_DATA_MONGODB_URI"));
        env.put("PORT", System.getenv("PORT"));
        env.put("JWT_SECRET", System.getenv("JWT_SECRET") != null ? "SET" : "NOT SET");
        return env;
    }
}
