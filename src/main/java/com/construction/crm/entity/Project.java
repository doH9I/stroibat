package com.construction.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotBlank
    private String address;
    
    @NotNull
    private LocalDate startDate;
    
    private LocalDate plannedEndDate;
    
    private LocalDate actualEndDate;
    
    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.PLANNING;
    
    @Enumerated(EnumType.STRING)
    private ProjectType type;
    
    private BigDecimal budget;
    
    private BigDecimal actualCost;
    
    private String clientName;
    
    private String clientPhone;
    
    private String clientEmail;
    
    private String contractNumber;
    
    private LocalDate contractDate;
    
    private BigDecimal contractValue;
    
    private String projectManager;
    
    private String architect;
    
    private String engineer;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Estimate> estimates = new ArrayList<>();
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProjectTask> tasks = new HashSet<>();
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectDocument> documents = new ArrayList<>();
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProjectProgress> progressUpdates = new ArrayList<>();
    
    public enum ProjectStatus {
        PLANNING,
        IN_PROGRESS,
        ON_HOLD,
        COMPLETED,
        CANCELLED
    }
    
    public enum ProjectType {
        RESIDENTIAL,
        COMMERCIAL,
        INDUSTRIAL,
        INFRASTRUCTURE,
        RENOVATION
    }
    
    // Constructors
    public Project() {}
    
    public Project(String name, String address, LocalDate startDate) {
        this.name = name;
        this.address = address;
        this.startDate = startDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getPlannedEndDate() {
        return plannedEndDate;
    }
    
    public void setPlannedEndDate(LocalDate plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }
    
    public LocalDate getActualEndDate() {
        return actualEndDate;
    }
    
    public void setActualEndDate(LocalDate actualEndDate) {
        this.actualEndDate = actualEndDate;
    }
    
    public ProjectStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
    
    public ProjectType getType() {
        return type;
    }
    
    public void setType(ProjectType type) {
        this.type = type;
    }
    
    public BigDecimal getBudget() {
        return budget;
    }
    
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
    
    public BigDecimal getActualCost() {
        return actualCost;
    }
    
    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getClientPhone() {
        return clientPhone;
    }
    
    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }
    
    public String getClientEmail() {
        return clientEmail;
    }
    
    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
    
    public String getContractNumber() {
        return contractNumber;
    }
    
    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }
    
    public LocalDate getContractDate() {
        return contractDate;
    }
    
    public void setContractDate(LocalDate contractDate) {
        this.contractDate = contractDate;
    }
    
    public BigDecimal getContractValue() {
        return contractValue;
    }
    
    public void setContractValue(BigDecimal contractValue) {
        this.contractValue = contractValue;
    }
    
    public String getProjectManager() {
        return projectManager;
    }
    
    public void setProjectManager(String projectManager) {
        this.projectManager = projectManager;
    }
    
    public String getArchitect() {
        return architect;
    }
    
    public void setArchitect(String architect) {
        this.architect = architect;
    }
    
    public String getEngineer() {
        return engineer;
    }
    
    public void setEngineer(String engineer) {
        this.engineer = engineer;
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
    
    public User getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public List<Estimate> getEstimates() {
        return estimates;
    }
    
    public void setEstimates(List<Estimate> estimates) {
        this.estimates = estimates;
    }
    
    public Set<ProjectTask> getTasks() {
        return tasks;
    }
    
    public void setTasks(Set<ProjectTask> tasks) {
        this.tasks = tasks;
    }
    
    public List<ProjectDocument> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<ProjectDocument> documents) {
        this.documents = documents;
    }
    
    public List<ProjectProgress> getProgressUpdates() {
        return progressUpdates;
    }
    
    public void setProgressUpdates(List<ProjectProgress> progressUpdates) {
        this.progressUpdates = progressUpdates;
    }
    
    // Utility methods
    public BigDecimal getProfit() {
        if (contractValue != null && actualCost != null) {
            return contractValue.subtract(actualCost);
        }
        return BigDecimal.ZERO;
    }
    
    public int getProgressPercentage() {
        if (actualEndDate != null) {
            return 100;
        }
        if (plannedEndDate != null && startDate != null) {
            LocalDate today = LocalDate.now();
            if (today.isAfter(plannedEndDate)) {
                return 100;
            }
            long totalDays = startDate.until(plannedEndDate).getDays();
            long elapsedDays = startDate.until(today).getDays();
            if (totalDays > 0) {
                return Math.min(100, (int) ((elapsedDays * 100) / totalDays));
            }
        }
        return 0;
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