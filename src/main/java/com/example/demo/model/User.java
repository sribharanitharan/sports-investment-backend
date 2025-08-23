package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    private String passwordHash;
    
    @CreatedDate
    private LocalDateTime createdDate;
    
    // Constructors
    public User() {}
    
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public void setSportType(String sportType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSportType'");
    }

    public void setTeamA(String teamA) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setTeamA'");
    }

    public void setTeamB(String teamB) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setTeamB'");
    }

    public void setWinnerOrDraw(String winnerOrDraw) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setWinnerOrDraw'");
    }

    public void setAmountInvested(Double amountInvested) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAmountInvested'");
    }

    public void setRatio(Double ratio) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRatio'");
    }

    public void setEntryDate(LocalDate entryDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEntryDate'");
    }

    public void setEstimatedProfit(double d) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEstimatedProfit'");
    }

    public void setMatchDate(LocalDate matchDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setMatchDate'");
    }
}
