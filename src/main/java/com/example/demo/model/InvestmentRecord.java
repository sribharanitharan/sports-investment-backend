package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "investment_records")
public class InvestmentRecord {
    @Id
    private String id;
    
    private String userId;  // Reference to User by ID (String)
    private String matchName;
    private String sportType;
    private String teamA;
    private String teamB;
    private String winnerOrDraw; // TEAM_A, TEAM_B, DRAW
    private Double amountInvested;
    private Double ratio;
    private Double estimatedProfit;
    private LocalDate entryDate;
    
    @CreatedDate
    private LocalDateTime createdDate;
    
    // Constructors
    public InvestmentRecord() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMatchName() { return matchName; }
    public void setMatchName(String matchName) { this.matchName = matchName; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public String getTeamA() { return teamA; }
    public void setTeamA(String teamA) { this.teamA = teamA; }

    public String getTeamB() { return teamB; }
    public void setTeamB(String teamB) { this.teamB = teamB; }

    public String getWinnerOrDraw() { return winnerOrDraw; }
    public void setWinnerOrDraw(String winnerOrDraw) { this.winnerOrDraw = winnerOrDraw; }

    public Double getAmountInvested() { return amountInvested; }
    public void setAmountInvested(Double amountInvested) { this.amountInvested = amountInvested; }

    public Double getRatio() { return ratio; }
    public void setRatio(Double ratio) { this.ratio = ratio; }

    public Double getEstimatedProfit() { return estimatedProfit; }
    public void setEstimatedProfit(Double estimatedProfit) { this.estimatedProfit = estimatedProfit; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}
