package com.example.demo.dto.response;

import java.util.Map;
import java.util.List;

public class AnalyticsResponse {
    private Double totalInvestment;
    private Double totalProfit;
    private Double totalLoss;
    private Double netProfit;
    private Integer totalBets;
    private Integer winningBets;
    private Integer losingBets;
    private Double winPercentage;
    private Double averageBetAmount;
    private Double bestProfit;
    private Double worstLoss;
    
    // Sport-wise breakdown
    private Map<String, SportStats> sportWiseStats;
    
    // Monthly data for charts
    private List<MonthlyData> monthlyData;
    
    // Recent activities
    private List<RecentActivity> recentActivities;
    
    // Constructors
    public AnalyticsResponse() {}
    
    // Inner classes for structured data
    public static class SportStats {
        private Double investment;
        private Double profit;
        private Integer totalBets;
        private Integer winningBets;
        private Double winPercentage;
        
        // Constructors, Getters, Setters
        public SportStats() {}
        
        public SportStats(Double investment, Double profit, Integer totalBets, Integer winningBets) {
            this.investment = investment;
            this.profit = profit;
            this.totalBets = totalBets;
            this.winningBets = winningBets;
            this.winPercentage = totalBets > 0 ? (winningBets.doubleValue() / totalBets) * 100 : 0.0;
        }
        
        // Getters and Setters
        public Double getInvestment() { return investment; }
        public void setInvestment(Double investment) { this.investment = investment; }
        
        public Double getProfit() { return profit; }
        public void setProfit(Double profit) { this.profit = profit; }
        
        public Integer getTotalBets() { return totalBets; }
        public void setTotalBets(Integer totalBets) { this.totalBets = totalBets; }
        
        public Integer getWinningBets() { return winningBets; }
        public void setWinningBets(Integer winningBets) { this.winningBets = winningBets; }
        
        public Double getWinPercentage() { return winPercentage; }
        public void setWinPercentage(Double winPercentage) { this.winPercentage = winPercentage; }
    }
    
    public static class MonthlyData {
        private String month;
        private Double investment;
        private Double profit;
        private Integer totalBets;
        
        // Constructors, Getters, Setters
        public MonthlyData() {}
        
        public MonthlyData(String month, Double investment, Double profit, Integer totalBets) {
            this.month = month;
            this.investment = investment;
            this.profit = profit;
            this.totalBets = totalBets;
        }
        
        // Getters and Setters
        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        
        public Double getInvestment() { return investment; }
        public void setInvestment(Double investment) { this.investment = investment; }
        
        public Double getProfit() { return profit; }
        public void setProfit(Double profit) { this.profit = profit; }
        
        public Integer getTotalBets() { return totalBets; }
        public void setTotalBets(Integer totalBets) { this.totalBets = totalBets; }
    }
    
    public static class RecentActivity {
        private String matchName;
        private String sportType;
        private Double amount;
        private Double profit;
        private String date;
        private String result;
        
        // Constructors, Getters, Setters
        public RecentActivity() {}
        
        public RecentActivity(String matchName, String sportType, Double amount, Double profit, String date, String result) {
            this.matchName = matchName;
            this.sportType = sportType;
            this.amount = amount;
            this.profit = profit;
            this.date = date;
            this.result = result;
        }
        
        // Getters and Setters
        public String getMatchName() { return matchName; }
        public void setMatchName(String matchName) { this.matchName = matchName; }
        
        public String getSportType() { return sportType; }
        public void setSportType(String sportType) { this.sportType = sportType; }
        
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        
        public Double getProfit() { return profit; }
        public void setProfit(Double profit) { this.profit = profit; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
    }
    
    // Main class Getters and Setters
    public Double getTotalInvestment() { return totalInvestment; }
    public void setTotalInvestment(Double totalInvestment) { this.totalInvestment = totalInvestment; }
    
    public Double getTotalProfit() { return totalProfit; }
    public void setTotalProfit(Double totalProfit) { this.totalProfit = totalProfit; }
    
    public Double getTotalLoss() { return totalLoss; }
    public void setTotalLoss(Double totalLoss) { this.totalLoss = totalLoss; }
    
    public Double getNetProfit() { return netProfit; }
    public void setNetProfit(Double netProfit) { this.netProfit = netProfit; }
    
    public Integer getTotalBets() { return totalBets; }
    public void setTotalBets(Integer totalBets) { this.totalBets = totalBets; }
    
    public Integer getWinningBets() { return winningBets; }
    public void setWinningBets(Integer winningBets) { this.winningBets = winningBets; }
    
    public Integer getLosingBets() { return losingBets; }
    public void setLosingBets(Integer losingBets) { this.losingBets = losingBets; }
    
    public Double getWinPercentage() { return winPercentage; }
    public void setWinPercentage(Double winPercentage) { this.winPercentage = winPercentage; }
    
    public Double getAverageBetAmount() { return averageBetAmount; }
    public void setAverageBetAmount(Double averageBetAmount) { this.averageBetAmount = averageBetAmount; }
    
    public Double getBestProfit() { return bestProfit; }
    public void setBestProfit(Double bestProfit) { this.bestProfit = bestProfit; }
    
    public Double getWorstLoss() { return worstLoss; }
    public void setWorstLoss(Double worstLoss) { this.worstLoss = worstLoss; }
    
    public Map<String, SportStats> getSportWiseStats() { return sportWiseStats; }
    public void setSportWiseStats(Map<String, SportStats> sportWiseStats) { this.sportWiseStats = sportWiseStats; }
    
    public List<MonthlyData> getMonthlyData() { return monthlyData; }
    public void setMonthlyData(List<MonthlyData> monthlyData) { this.monthlyData = monthlyData; }
    
    public List<RecentActivity> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<RecentActivity> recentActivities) { this.recentActivities = recentActivities; }
}
