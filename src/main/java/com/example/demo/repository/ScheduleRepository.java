package com.example.demo.repository;

import com.example.demo.model.Schedule;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    
    // ✅ CORRECT: Find schedules by userId (matches your entity field)
    List<Schedule> findByUserId(String userId);
    
    // ✅ CORRECT: Find schedule by ID and userId (security check)
    Optional<Schedule> findByIdAndUserId(String id, String userId);
    
    // ✅ CORRECT: Sort schedules by match date descending
    List<Schedule> findByUserIdOrderByMatchDateDesc(String userId);
    
    // ✅ CORRECT: Find by userId and sport type
    List<Schedule> findByUserIdAndSportType(String userId, String sportType);
    
    // ✅ CORRECT: Find upcoming matches using method naming
    List<Schedule> findByUserIdAndMatchDateAfterOrderByMatchDateAsc(String userId, LocalDate currentDate);
    
    // ✅ CORRECT: Custom query for upcoming matches
    @Query("{ 'userId': ?0, 'matchDate': { '$gt': ?1 } }")
    List<Schedule> findUpcomingMatches(String userId, LocalDate currentDate);
    
    // ✅ CORRECT: Find by userId and date range
    List<Schedule> findByUserIdAndMatchDateBetween(String userId, LocalDate startDate, LocalDate endDate);
    
    // ✅ CORRECT: Count schedules for a user
    Long countByUserId(String userId);
    
    // ✅ CORRECT: Find recent schedules (last N days)
    @Query("{ 'userId': ?0, 'matchDate': { '$gte': ?1 } }")
    List<Schedule> findRecentSchedules(String userId, LocalDate sinceDate);
}
