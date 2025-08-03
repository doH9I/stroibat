package com.construction.crm.repository;

import com.construction.crm.entity.TimeSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheet, Long> {
    
    List<TimeSheet> findByUserId(Long userId);
    
    List<TimeSheet> findByProjectId(Long projectId);
    
    List<TimeSheet> findByWorkDate(LocalDate workDate);
    
    List<TimeSheet> findByWorkDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<TimeSheet> findByWorkType(TimeSheet.WorkType workType);
    
    List<TimeSheet> findByUserIdAndWorkDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    List<TimeSheet> findByProjectIdAndWorkDateBetween(Long projectId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT ts FROM TimeSheet ts WHERE ts.user.id = :userId AND ts.workDate >= :startDate AND ts.workDate <= :endDate")
    List<TimeSheet> findUserTimeSheetsInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(ts.hoursWorked) FROM TimeSheet ts WHERE ts.user.id = :userId AND ts.workDate >= :startDate AND ts.workDate <= :endDate")
    BigDecimal getTotalHoursForUserInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(ts.overtimeHours) FROM TimeSheet ts WHERE ts.user.id = :userId AND ts.workDate >= :startDate AND ts.workDate <= :endDate")
    BigDecimal getTotalOvertimeForUserInPeriod(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(ts.hoursWorked) FROM TimeSheet ts WHERE ts.project.id = :projectId AND ts.workDate >= :startDate AND ts.workDate <= :endDate")
    BigDecimal getTotalHoursForProjectInPeriod(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT ts.workType, COUNT(ts) FROM TimeSheet ts WHERE ts.user.id = :userId AND ts.workDate >= :startDate AND ts.workDate <= :endDate GROUP BY ts.workType")
    List<Object[]> getWorkTypeStatsForUser(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT ts.user.id, SUM(ts.hoursWorked) FROM TimeSheet ts WHERE ts.project.id = :projectId AND ts.workDate >= :startDate AND ts.workDate <= :endDate GROUP BY ts.user.id")
    List<Object[]> getHoursByUserForProject(@Param("projectId") Long projectId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT AVG(ts.hoursWorked) FROM TimeSheet ts WHERE ts.user.id = :userId AND ts.workDate >= :startDate AND ts.workDate <= :endDate")
    BigDecimal getAverageHoursPerDayForUser(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}