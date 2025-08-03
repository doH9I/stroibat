package com.construction.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "time_sheets")
public class TimeSheet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    private LocalDate workDate;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private BigDecimal hoursWorked;
    
    private BigDecimal overtimeHours;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private WorkType workType = WorkType.REGULAR;
    
    private String location;
    
    private String weatherConditions;
    
    private String notes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private ProjectTask task;
    
    public enum WorkType {
        REGULAR,
        OVERTIME,
        WEEKEND,
        HOLIDAY,
        SICK_LEAVE,
        VACATION,
        BUSINESS_TRIP
    }
    
    // Constructors
    public TimeSheet() {}
    
    public TimeSheet(LocalDate workDate, User user) {
        this.workDate = workDate;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        calculateHours();
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        calculateHours();
    }
    
    public BigDecimal getHoursWorked() {
        return hoursWorked;
    }
    
    public void setHoursWorked(BigDecimal hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
    
    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }
    
    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public WorkType getWorkType() {
        return workType;
    }
    
    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getWeatherConditions() {
        return weatherConditions;
    }
    
    public void setWeatherConditions(String weatherConditions) {
        this.weatherConditions = weatherConditions;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public ProjectTask getTask() {
        return task;
    }
    
    public void setTask(ProjectTask task) {
        this.task = task;
    }
    
    // Utility methods
    public void calculateHours() {
        if (startTime != null && endTime != null) {
            long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
            this.hoursWorked = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            
            // Calculate overtime (assuming 8 hours is regular work day)
            if (hoursWorked.compareTo(BigDecimal.valueOf(8)) > 0) {
                this.overtimeHours = hoursWorked.subtract(BigDecimal.valueOf(8));
            } else {
                this.overtimeHours = BigDecimal.ZERO;
            }
        }
    }
    
    public String getWorkPeriod() {
        if (startTime != null && endTime != null) {
            return startTime.toString() + " - " + endTime.toString();
        }
        return "";
    }
    
    public boolean isOvertime() {
        return overtimeHours != null && overtimeHours.compareTo(BigDecimal.ZERO) > 0;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}