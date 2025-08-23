package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "schedules")
public class Schedule {
    @Id
    private String id;
    
    private String userId;  // ✅ This field exists - referencing User by ID
    private String sportType; // CRICKET, KABADDI
    private String matchName;
    private String teamA;
    private String teamB;
    private LocalDate matchDate;
    
    @CreatedDate
    private LocalDate createdDate;
    
    // Constructors
    public Schedule() {}
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }
    
    public String getMatchName() { return matchName; }
    public void setMatchName(String matchName) { this.matchName = matchName; }
    
    public String getTeamA() { return teamA; }
    public void setTeamA(String teamA) { this.teamA = teamA; }
    
    public String getTeamB() { return teamB; }
    public void setTeamB(String teamB) { this.teamB = teamB; }
    
    public LocalDate getMatchDate() { return matchDate; }
    public void setMatchDate(LocalDate matchDate) { this.matchDate = matchDate; }
    
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    
    // ✅ REMOVED: The problematic setUser method that was causing confusion
    // public void setUser(User user) { ... } // REMOVED
}
