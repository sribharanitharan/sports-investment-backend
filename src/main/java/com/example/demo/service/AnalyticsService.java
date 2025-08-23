package com.example.demo.service;

import com.example.demo.dto.response.AnalyticsResponse;
import com.example.demo.dto.response.AnalyticsResponse.SportStats;
import com.example.demo.dto.response.AnalyticsResponse.MonthlyData;
import com.example.demo.dto.response.AnalyticsResponse.RecentActivity;
import com.example.demo.model.InvestmentRecord;
import com.example.demo.model.User;
import com.example.demo.repository.InvestmentRecordRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    @Autowired
    private InvestmentRecordRepository recordRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public AnalyticsResponse getDashboardAnalytics(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<InvestmentRecord> records = recordRepository.findByUserId(user.getId());
        
        return calculateAnalytics(records);
    }
    
    public AnalyticsResponse getMonthlyAnalytics(String username, int year, int month) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        List<InvestmentRecord> records = recordRepository.findByUserIdAndDateRange(
                user.getId(), startDate, endDate);
        
        return calculateAnalytics(records);
    }
    
    public AnalyticsResponse getSportWiseAnalytics(String username, String sportType) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<InvestmentRecord> records = recordRepository.findByUserIdAndSportType(
                user.getId(), sportType);
        
        return calculateAnalytics(records);
    }
    
    public AnalyticsResponse getProfitLossAnalytics(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<InvestmentRecord> records;
        if (startDate != null && endDate != null) {
            records = recordRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);
        } else {
            records = recordRepository.findByUserId(user.getId());
        }
        
        return calculateAnalytics(records);
    }
    
    private AnalyticsResponse calculateAnalytics(List<InvestmentRecord> records) {
        AnalyticsResponse analytics = new AnalyticsResponse();
        
        if (records.isEmpty()) {
            return analytics;
        }
        
        // Basic calculations
        double totalInvestment = records.stream()
                .mapToDouble(InvestmentRecord::getAmountInvested)
                .sum();
        
        double totalProfit = records.stream()
                .mapToDouble(record -> Math.max(0, record.getEstimatedProfit()))
                .sum();
        
        double totalLoss = records.stream()
                .mapToDouble(record -> Math.min(0, record.getEstimatedProfit()))
                .sum();
        
        double netProfit = totalProfit + totalLoss;
        
        int totalBets = records.size();
        
        int winningBets = (int) records.stream()
                .filter(record -> record.getEstimatedProfit() > 0)
                .count();
        
        int losingBets = totalBets - winningBets;
        
        double winPercentage = totalBets > 0 ? (winningBets * 100.0 / totalBets) : 0.0;
        
        double averageBetAmount = totalInvestment / totalBets;
        
        double bestProfit = records.stream()
                .mapToDouble(InvestmentRecord::getEstimatedProfit)
                .max()
                .orElse(0.0);
        
        double worstLoss = records.stream()
                .mapToDouble(InvestmentRecord::getEstimatedProfit)
                .min()
                .orElse(0.0);
        
        // Set basic analytics
        analytics.setTotalInvestment(totalInvestment);
        analytics.setTotalProfit(totalProfit);
        analytics.setTotalLoss(Math.abs(totalLoss));
        analytics.setNetProfit(netProfit);
        analytics.setTotalBets(totalBets);
        analytics.setWinningBets(winningBets);
        analytics.setLosingBets(losingBets);
        analytics.setWinPercentage(winPercentage);
        analytics.setAverageBetAmount(averageBetAmount);
        analytics.setBestProfit(bestProfit);
        analytics.setWorstLoss(worstLoss);
        
        // Sport-wise statistics
        Map<String, SportStats> sportWiseStats = calculateSportWiseStats(records);
        analytics.setSportWiseStats(sportWiseStats);
        
        // Monthly data for charts
        List<MonthlyData> monthlyData = calculateMonthlyData(records);
        analytics.setMonthlyData(monthlyData);
        
        // Recent activities
        List<RecentActivity> recentActivities = getRecentActivities(records);
        analytics.setRecentActivities(recentActivities);
        
        return analytics;
    }
    
    private Map<String, SportStats> calculateSportWiseStats(List<InvestmentRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(InvestmentRecord::getSportType))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            List<InvestmentRecord> sportRecords = entry.getValue();
                            double investment = sportRecords.stream()
                                    .mapToDouble(InvestmentRecord::getAmountInvested)
                                    .sum();
                            double profit = sportRecords.stream()
                                    .mapToDouble(InvestmentRecord::getEstimatedProfit)
                                    .sum();
                            int totalBets = sportRecords.size();
                            int winningBets = (int) sportRecords.stream()
                                    .filter(record -> record.getEstimatedProfit() > 0)
                                    .count();
                            
                            return new SportStats(investment, profit, totalBets, winningBets);
                        }
                ));
    }
    
    private List<MonthlyData> calculateMonthlyData(List<InvestmentRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getEntryDate().format(DateTimeFormatter.ofPattern("yyyy-MM"))
                ))
                .entrySet().stream()
                .map(entry -> {
                    List<InvestmentRecord> monthRecords = entry.getValue();
                    double investment = monthRecords.stream()
                            .mapToDouble(InvestmentRecord::getAmountInvested)
                            .sum();
                    double profit = monthRecords.stream()
                            .mapToDouble(InvestmentRecord::getEstimatedProfit)
                            .sum();
                    int totalBets = monthRecords.size();
                    
                    return new MonthlyData(entry.getKey(), investment, profit, totalBets);
                })
                .sorted(Comparator.comparing(MonthlyData::getMonth))
                .collect(Collectors.toList());
    }
    
    private List<RecentActivity> getRecentActivities(List<InvestmentRecord> records) {
        return records.stream()
                .sorted(Comparator.comparing(InvestmentRecord::getCreatedDate).reversed())
                .limit(10)
                .map(record -> new RecentActivity(
                        record.getMatchName(),
                        record.getSportType(),
                        record.getAmountInvested(),
                        record.getEstimatedProfit(),
                        record.getEntryDate().toString(),
                        record.getWinnerOrDraw()
                ))
                .collect(Collectors.toList());
    }
}
