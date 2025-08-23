package com.example.demo.controller;

import com.example.demo.dto.request.RecordRequest;
import com.example.demo.dto.request.ScheduleRequest;
import com.example.demo.model.InvestmentRecord;
import com.example.demo.model.Schedule;
import com.example.demo.service.SportsService;
import com.example.demo.service.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * REST Controller for Sports Investment Management
 * Provides endpoints for managing schedules, investment records, analytics, and exports
 */
@RestController
@RequestMapping("/api/sports")
@CrossOrigin(origins = "*")
@Validated
public class SportsController {
    
    @Autowired
    private SportsService sportsService;
    
    @Autowired
    private ScheduleService scheduleService;
    
    // ==================== SCHEDULE OPERATIONS ====================
    
    @PostMapping("/schedules")
    public ResponseEntity<?> addSchedule(@Valid @RequestBody ScheduleRequest request, 
                                        Authentication authentication) {
        try {
            String username = authentication.getName();
            Schedule schedule = sportsService.addSchedule(request, username);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse<>("Schedule created successfully", schedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create schedule: " + e.getMessage()));
        }
    }
    
    @GetMapping("/schedules")
    public ResponseEntity<?> getSchedules(Authentication authentication) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // ✅ UPDATED: Handle both authenticated and anonymous users
            if (auth == null || 
                !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken ||
                "anonymous".equals(auth.getName()) ||
                "anonymousUser".equals(auth.getName())) {
                
                // Handle anonymous users - return all schedules for testing
                List<Schedule> allSchedules = scheduleService.getAllSchedules();
                
                return ResponseEntity.ok(Map.of(
                    "schedules", allSchedules,
                    "success", true,
                    "message", "Public access - showing all schedules",
                    "user", "anonymous",
                    "timestamp", System.currentTimeMillis()
                ));
                
            } else {
                // Handle authenticated users
                String username = auth.getName();
                List<Schedule> userSchedules = scheduleService.getSchedulesByUser(username);
                
                return ResponseEntity.ok(Map.of(
                    "schedules", userSchedules,
                    "success", true,
                    "user", username,
                    "timestamp", System.currentTimeMillis()
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to get schedules: " + e.getMessage()));
        }
    }
    
    @GetMapping("/schedules/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable String id, Authentication authentication) {
        try {
            String username = authentication.getName();
            Schedule schedule = sportsService.getScheduleById(id, username);
            return ResponseEntity.ok(schedule);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Schedule not found or access denied"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve schedule: " + e.getMessage()));
        }
    }
    
    // ✅ REMOVED: Conflicting PUT mapping - using records endpoint instead
    
    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable String id, Authentication authentication) {
        try {
            String username = authentication.getName();
            sportsService.deleteSchedule(id, username);
            return ResponseEntity.ok(new SuccessResponse("Schedule deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Schedule not found or access denied"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete schedule: " + e.getMessage()));
        }
    }
    
    // ==================== INVESTMENT RECORD OPERATIONS ====================
    
    @PostMapping("/records")
    public ResponseEntity<?> addRecord(@Valid @RequestBody RecordRequest request,
                                      Authentication authentication) {
        try {
            String username = authentication.getName();
            InvestmentRecord record = sportsService.addRecord(request, username);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponse<>("Investment record created successfully", record));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create investment record: " + e.getMessage()));
        }
    }
    
    @GetMapping("/records")
    public ResponseEntity<?> getRecords(
            @RequestParam(required = false) String sportType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            
            LocalDate start = null;
            LocalDate end = null;
            
            if (startDate != null && !startDate.trim().isEmpty()) {
                start = LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = LocalDate.parse(endDate);
            }
            
            List<InvestmentRecord> records = sportsService.getUserRecords(username, sportType, start, end);
            return ResponseEntity.ok(records);
            
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid date format. Use YYYY-MM-DD format."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to fetch records: " + e.getMessage()));
        }
    }
    
    @GetMapping("/records/{id}")
    public ResponseEntity<?> getRecordById(@PathVariable String id, Authentication authentication) {
        try {
            String username = authentication.getName();
            InvestmentRecord record = sportsService.getRecordById(id, username);
            return ResponseEntity.ok(record);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Investment record not found or access denied"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to retrieve record: " + e.getMessage()));
        }
    }
    
    @PutMapping("/records/{id}")
    public ResponseEntity<?> updateRecord(@PathVariable String id, 
                                        @Valid @RequestBody RecordRequest request, 
                                        Authentication authentication) {
        try {
            String username = authentication.getName();
            InvestmentRecord updatedRecord = sportsService.updateRecord(id, request, username);
            return ResponseEntity.ok(new SuccessResponse<>("Investment record updated successfully", updatedRecord));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Investment record not found or access denied"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to update investment record: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/records/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable String id, Authentication authentication) {
        try {
            String username = authentication.getName();
            sportsService.deleteRecord(id, username);
            return ResponseEntity.ok(new SuccessResponse("Investment record deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Investment record not found or access denied"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to delete investment record: " + e.getMessage()));
        }
    }
    
    // ==================== EXPORT/DOWNLOAD ENDPOINTS ====================
    
    @GetMapping("/export/monthly")
    public ResponseEntity<?> exportDataByMonth(
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            
            List<InvestmentRecord> records = sportsService.getUserRecords(username, null, startDate, endDate);
            List<Schedule> schedules = sportsService.getUserSchedules(username, null, startDate, endDate);
            
            String csvContent = generateCsvContent(records, schedules);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                String.format("sports_data_%04d_%02d.csv", year, month));
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to export monthly data: " + e.getMessage()));
        }
    }
    
    @GetMapping("/export/overall")
    public ResponseEntity<?> exportOverallData(Authentication authentication) {
        try {
            String username = authentication.getName();
            
            List<InvestmentRecord> records = sportsService.getUserRecords(username, null, null, null);
            List<Schedule> schedules = sportsService.getUserSchedules(username);
            
            String csvContent = generateCsvContent(records, schedules);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "sports_data_overall.csv");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to export overall data: " + e.getMessage()));
        }
    }
    
    @GetMapping("/export/by-sport")
    public ResponseEntity<?> exportDataBySport(
            @RequestParam @NotBlank String sportType,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            
            List<InvestmentRecord> records = sportsService.getUserRecords(username, sportType, null, null);
            List<Schedule> schedules = sportsService.getUserSchedules(username, sportType, null, null);
            
            String csvContent = generateCsvContent(records, schedules);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                String.format("sports_data_%s.csv", sportType.toLowerCase()));
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to export sport data: " + e.getMessage()));
        }
    }
    
    // ==================== FILTERED OPERATIONS ====================
    
    @GetMapping("/schedules/filtered")
    public ResponseEntity<?> getFilteredSchedules(
            @RequestParam(required = false) String sportType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication authentication) {
        
        try {
            String username = authentication.getName();
            
            LocalDate start = null;
            LocalDate end = null;
            
            if (startDate != null && !startDate.trim().isEmpty()) {
                start = LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = LocalDate.parse(endDate);
            }
            
            List<Schedule> schedules = sportsService.getUserSchedules(username, sportType, start, end);
            return ResponseEntity.ok(schedules);
            
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Invalid date format. Use YYYY-MM-DD format."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to fetch filtered schedules: " + e.getMessage()));
        }
    }
    
    // ==================== UTILITY ENDPOINTS ====================
    
    @GetMapping("/schedules/upcoming")
    public ResponseEntity<?> getUpcomingSchedules(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<Schedule> upcomingSchedules = sportsService.getUpcomingSchedules(username);
            return ResponseEntity.ok(upcomingSchedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to fetch upcoming schedules: " + e.getMessage()));
        }
    }
    
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<?> getDashboardStats(Authentication authentication) {
        try {
            String username = authentication.getName();
            
            Double totalInvestment = sportsService.getTotalInvestment(username);
            Double totalProfit = sportsService.getTotalEstimatedProfit(username);
            List<InvestmentRecord> allRecords = sportsService.getUserRecords(username, null, null, null);
            List<Schedule> allSchedules = sportsService.getUserSchedules(username);
            
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("totalInvestment", totalInvestment);
            dashboard.put("totalProfit", totalProfit);
            dashboard.put("netProfit", totalProfit - totalInvestment);
            dashboard.put("totalRecords", allRecords.size());
            dashboard.put("totalSchedules", allSchedules.size());
            dashboard.put("winRate", calculateWinRate(allRecords));
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to fetch dashboard stats: " + e.getMessage()));
        }
    }
    
    @GetMapping("/analytics/investment-total")
    public ResponseEntity<?> getTotalInvestment(Authentication authentication) {
        try {
            String username = authentication.getName();
            Double totalInvestment = sportsService.getTotalInvestment(username);
            return ResponseEntity.ok(new AnalyticsResponse("Total Investment", totalInvestment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to calculate total investment: " + e.getMessage()));
        }
    }
    
    @GetMapping("/analytics/profit-total")
    public ResponseEntity<?> getTotalProfit(Authentication authentication) {
        try {
            String username = authentication.getName();
            Double totalProfit = sportsService.getTotalEstimatedProfit(username);
            return ResponseEntity.ok(new AnalyticsResponse("Total Estimated Profit", totalProfit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to calculate total profit: " + e.getMessage()));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis(),
            "service", "Sports Investment API"
        ));
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    private String generateCsvContent(List<InvestmentRecord> records, List<Schedule> schedules) {
        StringBuilder csv = new StringBuilder();
        
        csv.append("Type,Match Name,Sport,Team A,Team B,Amount (₹),Ratio,Est. Profit (₹),Actual Profit (₹),Date,Status\n");
        
        for (InvestmentRecord record : records) {
            csv.append("Investment,")
               .append(escapeForCsv(record.getMatchName())).append(",")
               .append(record.getSportType()).append(",")
               .append(escapeForCsv(record.getTeamA())).append(",")
               .append(escapeForCsv(record.getTeamB())).append(",")
               .append(record.getAmountInvested() != null ? record.getAmountInvested() : 0).append(",")
               .append(record.getRatio() != null ? record.getRatio() : 0).append(",")
               .append(record.getEstimatedProfit() != null ? record.getEstimatedProfit() : 0).append(",")
               .append(calculateActualProfit(record)).append(",")
               .append(record.getEntryDate() != null ? record.getEntryDate() : "").append(",")
               .append("Completed\n");
        }
        
        for (Schedule schedule : schedules) {
            csv.append("Schedule,")
               .append(escapeForCsv(schedule.getMatchName())).append(",")
               .append(schedule.getSportType()).append(",")
               .append(escapeForCsv(schedule.getTeamA())).append(",")
               .append(escapeForCsv(schedule.getTeamB())).append(",")
               .append(",,,,")
               .append(schedule.getMatchDate() != null ? schedule.getMatchDate() : "").append(",")
               .append(isUpcomingMatch(schedule) ? "Upcoming" : "Past")
               .append("\n");
        }
        
        return csv.toString();
    }
    
    private String escapeForCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    private double calculateActualProfit(InvestmentRecord record) {
        if (record.getEstimatedProfit() != null && record.getAmountInvested() != null) {
            return record.getEstimatedProfit() - record.getAmountInvested();
        }
        return 0.0;
    }
    
    private boolean isUpcomingMatch(Schedule schedule) {
        if (schedule.getMatchDate() == null) return false;
        return LocalDate.now().isBefore(schedule.getMatchDate());
    }
    
    private double calculateWinRate(List<InvestmentRecord> records) {
        if (records.isEmpty()) return 0.0;
        
        long wins = records.stream()
            .mapToLong(record -> 
                (record.getEstimatedProfit() != null && record.getAmountInvested() != null && 
                 record.getEstimatedProfit() > record.getAmountInvested()) ? 1 : 0)
            .sum();
            
        return (double) wins / records.size() * 100;
    }
    
    // ==================== RESPONSE CLASSES ====================
    
    public static class ErrorResponse {
        private String message;
        private boolean success = false;
        private long timestamp = System.currentTimeMillis();
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() { return message; }
        public boolean isSuccess() { return success; }
        public long getTimestamp() { return timestamp; }
        public void setMessage(String message) { this.message = message; }
        public void setSuccess(boolean success) { this.success = success; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    public static class SuccessResponse<T> {
        private String message;
        private T data;
        private boolean success = true;
        private long timestamp = System.currentTimeMillis();
        
        public SuccessResponse(String message) {
            this.message = message;
        }
        
        public SuccessResponse(String message, T data) {
            this.message = message;
            this.data = data;
        }
        
        public String getMessage() { return message; }
        public T getData() { return data; }
        public boolean isSuccess() { return success; }
        public long getTimestamp() { return timestamp; }
        public void setMessage(String message) { this.message = message; }
        public void setData(T data) { this.data = data; }
        public void setSuccess(boolean success) { this.success = success; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
    
    public static class AnalyticsResponse {
        private String label;
        private Double value;
        private boolean success = true;
        private long timestamp = System.currentTimeMillis();
        
        public AnalyticsResponse(String label, Double value) {
            this.label = label;
            this.value = value;
        }
        
        public String getLabel() { return label; }
        public Double getValue() { return value; }
        public boolean isSuccess() { return success; }
        public long getTimestamp() { return timestamp; }
        public void setLabel(String label) { this.label = label; }
        public void setValue(Double value) { this.value = value; }
        public void setSuccess(boolean success) { this.success = success; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
