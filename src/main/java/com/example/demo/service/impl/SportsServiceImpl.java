package com.example.demo.service.impl;

import com.example.demo.dto.request.RecordRequest;
import com.example.demo.dto.request.ScheduleRequest;
import com.example.demo.model.InvestmentRecord;
import com.example.demo.model.Schedule;
import com.example.demo.model.User;
import com.example.demo.repository.InvestmentRecordRepository;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SportsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SportsServiceImpl implements SportsService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private InvestmentRecordRepository investmentRecordRepository;

    @Autowired
    private UserRepository userRepository;

    // ================== HELPER METHODS ==================

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // ================== SCHEDULE OPERATIONS ==================

    @Override
    public Schedule addSchedule(ScheduleRequest request, String username) {
        try {
            User user = findUserByUsername(username);
            Schedule schedule = new Schedule();
            schedule.setSportType(request.getSportType());
            schedule.setMatchName(request.getMatchName());
            schedule.setTeamA(request.getTeamA());
            schedule.setTeamB(request.getTeamB());
            schedule.setMatchDate(request.getMatchDate());
            schedule.setUserId(user.getId());
            schedule.setCreatedDate(LocalDate.now());
            
            Schedule savedSchedule = scheduleRepository.save(schedule);
            System.out.println("✅ Schedule added: " + savedSchedule.getMatchName() + " (ID: " + savedSchedule.getId() + ")");
            return savedSchedule;
        } catch (Exception e) {
            System.err.println("❌ Failed to add schedule: " + e.getMessage());
            throw new RuntimeException("Failed to add schedule: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Schedule> getUserSchedules(String username) {
        try {
            User user = findUserByUsername(username);
            List<Schedule> schedules = scheduleRepository.findByUserIdOrderByMatchDateDesc(user.getId());
            System.out.println("✅ Retrieved " + schedules.size() + " schedules for user: " + username);
            return schedules;
        } catch (Exception e) {
            System.err.println("❌ Failed to get user schedules: " + e.getMessage());
            throw new RuntimeException("Failed to get user schedules: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Schedule> getUserSchedules(String username, String sportType, LocalDate start, LocalDate end) {
        try {
            User user = findUserByUsername(username);
            String userId = user.getId();
            List<Schedule> schedules;
            
            if (start != null && end != null) {
                schedules = scheduleRepository.findByUserIdAndMatchDateBetween(userId, start, end);
                System.out.println("✅ Retrieved " + schedules.size() + " schedules between " + start + " and " + end);
            } else if (sportType != null && !"ALL".equals(sportType)) {
                schedules = scheduleRepository.findByUserIdAndSportType(userId, sportType);
                System.out.println("✅ Retrieved " + schedules.size() + " " + sportType + " schedules");
            } else {
                schedules = scheduleRepository.findByUserIdOrderByMatchDateDesc(userId);
                System.out.println("✅ Retrieved " + schedules.size() + " total schedules");
            }
            
            return schedules;
        } catch (Exception e) {
            System.err.println("❌ Failed to get filtered schedules: " + e.getMessage());
            throw new RuntimeException("Failed to get filtered schedules: " + e.getMessage(), e);
        }
    }

    @Override
    public Schedule updateSchedule(String id, ScheduleRequest request, String username) {
        try {
            User user = findUserByUsername(username);
            Schedule schedule = scheduleRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Schedule not found or access denied"));
            
            // Store old values for logging
            String oldMatchName = schedule.getMatchName();
            
            // Update fields
            schedule.setSportType(request.getSportType());
            schedule.setMatchName(request.getMatchName());
            schedule.setTeamA(request.getTeamA());
            schedule.setTeamB(request.getTeamB());
            schedule.setMatchDate(request.getMatchDate());
            
            Schedule updatedSchedule = scheduleRepository.save(schedule);
            System.out.println("✅ Schedule updated: '" + oldMatchName + "' -> '" + updatedSchedule.getMatchName() + "'");
            return updatedSchedule;
        } catch (Exception e) {
            System.err.println("❌ Failed to update schedule: " + e.getMessage());
            throw new RuntimeException("Failed to update schedule: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSchedule(String id, String username) {
        try {
            User user = findUserByUsername(username);
            Schedule schedule = scheduleRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Schedule not found or access denied"));
            
            String matchName = schedule.getMatchName();
            scheduleRepository.delete(schedule);
            System.out.println("✅ Schedule deleted: " + matchName + " (ID: " + id + ")");
        } catch (Exception e) {
            System.err.println("❌ Failed to delete schedule: " + e.getMessage());
            throw new RuntimeException("Failed to delete schedule: " + e.getMessage(), e);
        }
    }

    @Override
    public Schedule getScheduleById(String id, String username) {
        try {
            User user = findUserByUsername(username);
            Schedule schedule = scheduleRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Schedule not found or access denied"));
            System.out.println("✅ Retrieved schedule: " + schedule.getMatchName());
            return schedule;
        } catch (Exception e) {
            System.err.println("❌ Failed to get schedule: " + e.getMessage());
            throw new RuntimeException("Failed to get schedule: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Schedule> getUpcomingSchedules(String username) {
        try {
            User user = findUserByUsername(username);
            LocalDate today = LocalDate.now();
            List<Schedule> upcomingSchedules = scheduleRepository.findByUserIdAndMatchDateAfterOrderByMatchDateAsc(user.getId(), today);
            System.out.println("✅ Retrieved " + upcomingSchedules.size() + " upcoming schedules");
            return upcomingSchedules;
        } catch (Exception e) {
            System.err.println("❌ Failed to get upcoming schedules: " + e.getMessage());
            throw new RuntimeException("Failed to get upcoming schedules: " + e.getMessage(), e);
        }
    }

    // ================== INVESTMENT RECORD OPERATIONS ==================

    @Override
    public InvestmentRecord addRecord(RecordRequest request, String username) {
        try {
            User user = findUserByUsername(username);
            InvestmentRecord record = new InvestmentRecord();
            record.setSportType(request.getSportType());
            record.setMatchName(request.getMatchName());
            record.setTeamA(request.getTeamA());
            record.setTeamB(request.getTeamB());
            record.setWinnerOrDraw(request.getWinnerOrDraw());
            record.setAmountInvested(request.getAmountInvested());
            record.setRatio(request.getRatio());
            record.setEntryDate(request.getEntryDate());
            record.setUserId(user.getId());
            record.setCreatedDate(LocalDateTime.now());
            
            // Calculate estimated profit
            if (request.getAmountInvested() != null && request.getRatio() != null) {
                record.setEstimatedProfit(request.getAmountInvested() * request.getRatio());
            }
            
            InvestmentRecord savedRecord = investmentRecordRepository.save(record);
            System.out.println("✅ Investment record added: " + savedRecord.getMatchName() + 
                " (₹" + savedRecord.getAmountInvested() + " @ " + savedRecord.getRatio() + "x)");
            return savedRecord;
        } catch (Exception e) {
            System.err.println("❌ Failed to add investment record: " + e.getMessage());
            throw new RuntimeException("Failed to add investment record: " + e.getMessage(), e);
        }
    }

    @Override
    public List<InvestmentRecord> getUserRecords(String username, String sportType, LocalDate start, LocalDate end) {
        try {
            User user = findUserByUsername(username);
            String userId = user.getId();
            List<InvestmentRecord> records;
            
            if (start != null && end != null) {
                records = investmentRecordRepository.findByUserIdAndEntryDateBetween(userId, start, end);
                System.out.println("✅ Retrieved " + records.size() + " investment records between " + start + " and " + end);
            } else if (sportType != null && !"ALL".equals(sportType)) {
                records = investmentRecordRepository.findByUserIdAndSportType(userId, sportType);
                System.out.println("✅ Retrieved " + records.size() + " " + sportType + " investment records");
            } else {
                records = investmentRecordRepository.findByUserIdOrderByEntryDateDesc(userId);
                System.out.println("✅ Retrieved " + records.size() + " total investment records");
            }
            
            return records;
        } catch (Exception e) {
            System.err.println("❌ Failed to get user records: " + e.getMessage());
            throw new RuntimeException("Failed to get user records: " + e.getMessage(), e);
        }
    }

    @Override
    public InvestmentRecord updateRecord(String id, RecordRequest request, String username) {
        try {
            User user = findUserByUsername(username);
            String userId = user.getId();
            InvestmentRecord record = investmentRecordRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Investment record not found or access denied"));
            
            // Store old values for logging
            String oldMatchName = record.getMatchName();
            Double oldAmount = record.getAmountInvested();
            
            // Update fields
            record.setSportType(request.getSportType());
            record.setMatchName(request.getMatchName());
            record.setTeamA(request.getTeamA());
            record.setTeamB(request.getTeamB());
            record.setWinnerOrDraw(request.getWinnerOrDraw());
            record.setAmountInvested(request.getAmountInvested());
            record.setRatio(request.getRatio());
            record.setEntryDate(request.getEntryDate());
            
            // Recalculate estimated profit
            if (request.getAmountInvested() != null && request.getRatio() != null) {
                record.setEstimatedProfit(request.getAmountInvested() * request.getRatio());
            }
            
            InvestmentRecord updatedRecord = investmentRecordRepository.save(record);
            System.out.println("✅ Investment record updated: '" + oldMatchName + "' (₹" + oldAmount + 
                ") -> '" + updatedRecord.getMatchName() + "' (₹" + updatedRecord.getAmountInvested() + ")");
            return updatedRecord;
        } catch (Exception e) {
            System.err.println("❌ Failed to update investment record: " + e.getMessage());
            throw new RuntimeException("Failed to update investment record: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteRecord(String id, String username) {
        try {
            User user = findUserByUsername(username);
            String userId = user.getId();
            InvestmentRecord record = investmentRecordRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Investment record not found or access denied"));
            
            String matchName = record.getMatchName();
            Double amount = record.getAmountInvested();
            investmentRecordRepository.delete(record);
            System.out.println("✅ Investment record deleted: " + matchName + " (₹" + amount + ")");
        } catch (Exception e) {
            System.err.println("❌ Failed to delete investment record: " + e.getMessage());
            throw new RuntimeException("Failed to delete investment record: " + e.getMessage(), e);
        }
    }

    @Override
    public InvestmentRecord getRecordById(String id, String username) {
        try {
            User user = findUserByUsername(username);
            String userId = user.getId();
            InvestmentRecord record = investmentRecordRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Investment record not found or access denied"));
            System.out.println("✅ Retrieved investment record: " + record.getMatchName());
            return record;
        } catch (Exception e) {
            System.err.println("❌ Failed to get investment record: " + e.getMessage());
            throw new RuntimeException("Failed to get investment record: " + e.getMessage(), e);
        }
    }

    // ================== ANALYTICS OPERATIONS ==================

    @Override
    public Double getTotalInvestment(String username) {
        try {
            User user = findUserByUsername(username);
            String userId = user.getId();
            Double totalInvestment = investmentRecordRepository.findByUserIdOrderByEntryDateDesc(userId).stream()
                .mapToDouble(record -> record.getAmountInvested() != null ? record.getAmountInvested() : 0.0)
                .sum();
            System.out.println("✅ Total investment calculated for " + username + ": ₹" + totalInvestment);
            return totalInvestment;
        } catch (Exception e) {
            System.err.println("❌ Failed to calculate total investment: " + e.getMessage());
            throw new RuntimeException("Failed to calculate total investment: " + e.getMessage(), e);
        }
    }

    @Override
    public Double getTotalEstimatedProfit(String username) {
        try {
            User user = findUserByUsername(username);
            String userId = user.getId();
            Double totalProfit = investmentRecordRepository.findByUserIdOrderByEntryDateDesc(userId).stream()
                .mapToDouble(record -> record.getEstimatedProfit() != null ? record.getEstimatedProfit() : 0.0)
                .sum();
            System.out.println("✅ Total estimated profit calculated for " + username + ": ₹" + totalProfit);
            return totalProfit;
        } catch (Exception e) {
            System.err.println("❌ Failed to calculate total estimated profit: " + e.getMessage());
            throw new RuntimeException("Failed to calculate total estimated profit: " + e.getMessage(), e);
        }
    }

    // ================== UTILITY METHODS ==================

    /**
     * Get count of schedules for a user
     */
    public Long getScheduleCount(String username) {
        try {
            User user = findUserByUsername(username);
            return scheduleRepository.countByUserId(user.getId());
        } catch (Exception e) {
            System.err.println("❌ Failed to get schedule count: " + e.getMessage());
            throw new RuntimeException("Failed to get schedule count: " + e.getMessage(), e);
        }
    }

    /**
     * Get count of investment records for a user
     */
    public Long getRecordCount(String username) {
        try {
            User user = findUserByUsername(username);
            return investmentRecordRepository.countByUserId(user.getId());
        } catch (Exception e) {
            System.err.println("❌ Failed to get record count: " + e.getMessage());
            throw new RuntimeException("Failed to get record count: " + e.getMessage(), e);
        }
    }
}
