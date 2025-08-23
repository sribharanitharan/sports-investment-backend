package com.example.demo.repository;

import com.example.demo.model.InvestmentRecord;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRecordRepository extends MongoRepository<InvestmentRecord, String> {

    // ✅ Basic user-based queries by userId
    List<InvestmentRecord> findByUserId(String userId);

    List<InvestmentRecord> findByUserIdAndSportType(String userId, String sportType);

    List<InvestmentRecord> findByUserIdOrderByEntryDateDesc(String userId);

    // ✅ Date range queries
    @Query("{'userId': ?0, 'entryDate': {'$gte': ?1, '$lte': ?2}}")
    List<InvestmentRecord> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate);

    List<InvestmentRecord> findByUserIdAndEntryDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    // ✅ Find by ID and userId (security and ownership checks)
    Optional<InvestmentRecord> findByIdAndUserId(String id, String userId);

    // ✅ Additional query examples (custom logic)
    Long countByUserId(String userId);

    // Remove ALL methods using User object – they do NOT match your entity!
    // List<InvestmentRecord> findByUserOrderByEntryDateDesc(User user);        // ❌ Remove
    // Optional<InvestmentRecord> findByIdAndUser(String id, User user);        // ❌ Remove
}
