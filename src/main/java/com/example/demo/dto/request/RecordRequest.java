package com.example.demo.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RecordRequest {
    @NotBlank
    private String matchName;
    
    @NotBlank
    private String sportType;
    
    @NotBlank
    private String teamA;
    
    @NotBlank
    private String teamB;
    
    @NotBlank
    private String winnerOrDraw;
    
    @NotNull
    @Positive
    private Double amountInvested;
    
    @NotNull
    @Positive
    private Double ratio;
    
    @NotNull
    private LocalDate entryDate;
    
    // Constructors
    public RecordRequest() {}
    
    // Getters and Setters
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
    
    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }
}
