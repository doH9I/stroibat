package com.construction.crm.repository;

import com.construction.crm.entity.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    
    List<Estimate> findByStatus(Estimate.EstimateStatus status);
    
    List<Estimate> findByProjectId(Long projectId);
    
    List<Estimate> findByPreparedBy(String preparedBy);
    
    List<Estimate> findByApprovedBy(String approvedBy);
    
    List<Estimate> findByEstimateDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Estimate> findByApprovalDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT e FROM Estimate e WHERE e.totalAmount >= :minAmount AND e.totalAmount <= :maxAmount")
    List<Estimate> findByAmountRange(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT e.status, COUNT(e) FROM Estimate e GROUP BY e.status")
    List<Object[]> countByStatus();
    
    @Query("SELECT AVG(e.totalAmount) FROM Estimate e WHERE e.status = 'APPROVED'")
    BigDecimal getAverageApprovedAmount();
    
    @Query("SELECT SUM(e.totalAmount) FROM Estimate e WHERE e.status = 'APPROVED' AND e.estimateDate >= :startDate")
    BigDecimal getTotalApprovedAmountSince(@Param("startDate") LocalDate startDate);
    
    @Query("SELECT e FROM Estimate e WHERE e.project.id = :projectId ORDER BY e.estimateDate DESC")
    List<Estimate> findLatestByProject(@Param("projectId") Long projectId);
    
    @Query("SELECT e FROM Estimate e WHERE e.status = 'DRAFT' AND e.estimateDate < :date")
    List<Estimate> findExpiredDrafts(@Param("date") LocalDate date);
}