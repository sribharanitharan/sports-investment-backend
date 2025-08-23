package com.example.demo.controller;

import com.example.demo.dto.response.AnalyticsResponse;
import com.example.demo.service.AnalyticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsResponse> getDashboardData(Authentication authentication) {
        String username = authentication.getName();
        AnalyticsResponse analytics = analyticsService.getDashboardAnalytics(username);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/monthly")
    public ResponseEntity<AnalyticsResponse> getMonthlyAnalytics(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        
        String username = authentication.getName();
        AnalyticsResponse analytics = analyticsService.getMonthlyAnalytics(username, year, month);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/sport-wise")
    public ResponseEntity<AnalyticsResponse> getSportWiseAnalytics(
            @RequestParam String sportType,
            Authentication authentication) {
        
        String username = authentication.getName();
        AnalyticsResponse analytics = analyticsService.getSportWiseAnalytics(username, sportType);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/profit-loss")
    public ResponseEntity<AnalyticsResponse> getProfitLossAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication authentication) {
        
        String username = authentication.getName();
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        
        AnalyticsResponse analytics = analyticsService.getProfitLossAnalytics(username, start, end);
        return ResponseEntity.ok(analytics);
    }
}
