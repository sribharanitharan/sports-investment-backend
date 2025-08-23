package com.example.demo.service;

import com.example.demo.dto.request.RecordRequest;
import com.example.demo.dto.request.ScheduleRequest;
import com.example.demo.model.InvestmentRecord;
import com.example.demo.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface SportsService {

    // Schedule Operations
    Schedule addSchedule(ScheduleRequest request, String username);
    List<Schedule> getUserSchedules(String username);
    List<Schedule> getUserSchedules(String username, String sportType, LocalDate start, LocalDate end);
    Schedule updateSchedule(String id, ScheduleRequest request, String username);
    void deleteSchedule(String id, String username);
    Schedule getScheduleById(String id, String username);

    // Investment Record Operations
    InvestmentRecord addRecord(RecordRequest request, String username);
    List<InvestmentRecord> getUserRecords(String username, String sportType, LocalDate start, LocalDate end);
    InvestmentRecord updateRecord(String id, RecordRequest request, String username);
    void deleteRecord(String id, String username);
    InvestmentRecord getRecordById(String id, String username);
    Double getTotalEstimatedProfit(String username);
    Double getTotalInvestment(String username);
    List<Schedule> getUpcomingSchedules(String username);
}
