package com.example.demo.service;

import com.example.demo.dto.request.ScheduleRequest;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Schedule;
import com.example.demo.model.User;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // ✅ NEW: Method to get all schedules (for anonymous/testing access)
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }
    
    // ✅ NEW: Simple method to create schedule without authentication
    public Schedule createSchedule(Schedule schedule) {
        // Set creation timestamp if not provided
        // Only set if your Schedule model has this field
        return scheduleRepository.save(schedule);
    }
    
    // ✅ NEW: Method to update schedule without authentication
    public Schedule updateSchedule(String id, Schedule schedule) {
        Schedule existingSchedule = scheduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        
        // Update fields
        existingSchedule.setSportType(schedule.getSportType());
        existingSchedule.setMatchName(schedule.getMatchName());
        existingSchedule.setTeamA(schedule.getTeamA());
        existingSchedule.setTeamB(schedule.getTeamB());
        existingSchedule.setMatchDate(schedule.getMatchDate());
        
        return scheduleRepository.save(existingSchedule);
    }
    
    // ✅ NEW: Method to delete schedule without authentication
    public void deleteSchedule(String id) {
        Schedule schedule = scheduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        scheduleRepository.delete(schedule);
    }
    
    // ✅ UPDATED: Enhanced method for user-specific schedules with error handling
    public List<Schedule> getSchedulesByUser(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
            
            return scheduleRepository.findByUserId(user.getId());
        } catch (Exception e) {
            // If user not found, return empty list for graceful handling
            return List.of();
        }
    }
    
    // ===== EXISTING USER-AUTHENTICATED METHODS =====
    
    public Schedule createSchedule(ScheduleRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        // Validate that teams are different
        if (request.getTeamA().equalsIgnoreCase(request.getTeamB())) {
            throw new BadRequestException("Team A and Team B cannot be the same");
        }
        
        // Validate that match date is not in the past
        if (request.getMatchDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Match date cannot be in the past");
        }
        
        Schedule schedule = new Schedule();
        schedule.setUserId(user.getId());
        schedule.setSportType(request.getSportType().toUpperCase());
        schedule.setMatchName(request.getMatchName());
        schedule.setTeamA(request.getTeamA());
        schedule.setTeamB(request.getTeamB());
        schedule.setMatchDate(request.getMatchDate());
        // Remove entryDate lines - only add if your Schedule model has this field
        
        return scheduleRepository.save(schedule);
    }
    
    public List<Schedule> getUserSchedulesList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return scheduleRepository.findByUserId(user.getId());
    }
    
    public List<Schedule> getUserSchedulesBySport(String username, String sportType) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return scheduleRepository.findByUserIdAndSportType(user.getId(), sportType.toUpperCase());
    }
    
    public List<Schedule> getUpcomingSchedules(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return scheduleRepository.findUpcomingMatches(user.getId(), LocalDate.now());
    }
    
    public Schedule getScheduleById(String scheduleId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", scheduleId));
        
        // Ensure the schedule belongs to the current user
        if (!schedule.getUserId().equals(user.getId())) {
            throw new BadRequestException("You don't have permission to access this schedule");
        }
        
        return schedule;
    }
    
    public Schedule updateSchedule(String scheduleId, ScheduleRequest request, String username) {
        Schedule schedule = getScheduleById(scheduleId, username);
        
        // Validate that teams are different
        if (request.getTeamA().equalsIgnoreCase(request.getTeamB())) {
            throw new BadRequestException("Team A and Team B cannot be the same");
        }
        
        // Update schedule fields
        schedule.setSportType(request.getSportType().toUpperCase());
        schedule.setMatchName(request.getMatchName());
        schedule.setTeamA(request.getTeamA());
        schedule.setTeamB(request.getTeamB());
        schedule.setMatchDate(request.getMatchDate());
        
        return scheduleRepository.save(schedule);
    }
    
    public void deleteSchedule(String scheduleId, String username) {
        Schedule schedule = getScheduleById(scheduleId, username);
        scheduleRepository.delete(schedule);
    }
    
    public boolean scheduleExists(String matchName, String username, LocalDate matchDate) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
            
            List<Schedule> userSchedules = scheduleRepository.findByUserId(user.getId());
            
            return userSchedules.stream()
                    .anyMatch(schedule -> 
                        schedule.getMatchName().equalsIgnoreCase(matchName) &&
                        schedule.getMatchDate().equals(matchDate)
                    );
        } catch (Exception e) {
            return false;
        }
    }
    
    public List<Schedule> getTodaySchedules(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        LocalDate today = LocalDate.now();
        return scheduleRepository.findByUserId(user.getId())
                .stream()
                .filter(schedule -> schedule.getMatchDate().equals(today))
                .toList();
    }
    
    public List<Schedule> getSchedulesByDateRange(String username, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        return scheduleRepository.findByUserId(user.getId())
                .stream()
                .filter(schedule -> 
                    !schedule.getMatchDate().isBefore(startDate) && 
                    !schedule.getMatchDate().isAfter(endDate)
                )
                .toList();
    }
}
