package com.construction.crm.repository;

import com.construction.crm.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByStatus(Project.ProjectStatus status);
    
    List<Project> findByType(Project.ProjectType type);
    
    List<Project> findByClientNameContainingIgnoreCase(String clientName);
    
    List<Project> findByAddressContainingIgnoreCase(String address);
    
    List<Project> findByProjectManager(String projectManager);
    
    List<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Project> findByPlannedEndDateBefore(LocalDate date);
    
    @Query("SELECT p FROM Project p WHERE p.status = 'IN_PROGRESS' AND p.plannedEndDate < :today")
    List<Project> findOverdueProjects(@Param("today") LocalDate today);
    
    @Query("SELECT p FROM Project p WHERE p.contractValue >= :minValue AND p.contractValue <= :maxValue")
    List<Project> findByContractValueRange(@Param("minValue") BigDecimal minValue, @Param("maxValue") BigDecimal maxValue);
    
    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countByStatus();
    
    @Query("SELECT p.type, COUNT(p) FROM Project p GROUP BY p.type")
    List<Object[]> countByType();
    
    @Query("SELECT SUM(p.contractValue) FROM Project p WHERE p.status = 'COMPLETED'")
    BigDecimal getTotalCompletedValue();
    
    @Query("SELECT AVG(p.contractValue) FROM Project p WHERE p.status = 'COMPLETED'")
    BigDecimal getAverageProjectValue();
    
    @Query("SELECT p FROM Project p WHERE p.assignedTo.id = :userId")
    List<Project> findByAssignedUserId(@Param("userId") Long userId);
    

}