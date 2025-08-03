package com.construction.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estimates")
public class Estimate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    private LocalDate estimateDate;
    
    @Enumerated(EnumType.STRING)
    private EstimateStatus status = EstimateStatus.DRAFT;
    
    private BigDecimal totalAmount;
    
    private BigDecimal taxRate = new BigDecimal("20.0"); // 20% VAT
    
    private BigDecimal taxAmount;
    
    private BigDecimal totalWithTax;
    
    private String preparedBy;
    
    private String approvedBy;
    
    private LocalDate approvalDate;
    
    private String notes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EstimateItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EstimateDocument> documents = new ArrayList<>();
    
    public enum EstimateStatus {
        DRAFT,
        SUBMITTED,
        APPROVED,
        REJECTED,
        EXPIRED
    }
    
    // Constructors
    public Estimate() {}
    
    public Estimate(String name, Project project) {
        this.name = name;
        this.project = project;
        this.estimateDate = LocalDate.now();
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
    
    public LocalDate getEstimateDate() {
        return estimateDate;
    }
    
    public void setEstimateDate(LocalDate estimateDate) {
        this.estimateDate = estimateDate;
    }
    
    public EstimateStatus getStatus() {
        return status;
    }
    
    public void setStatus(EstimateStatus status) {
        this.status = status;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        calculateTaxAndTotal();
    }
    
    public BigDecimal getTaxRate() {
        return taxRate;
    }
    
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
        calculateTaxAndTotal();
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public BigDecimal getTotalWithTax() {
        return totalWithTax;
    }
    
    public void setTotalWithTax(BigDecimal totalWithTax) {
        this.totalWithTax = totalWithTax;
    }
    
    public String getPreparedBy() {
        return preparedBy;
    }
    
    public void setPreparedBy(String preparedBy) {
        this.preparedBy = preparedBy;
    }
    
    public String getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDate getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
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
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public List<EstimateItem> getItems() {
        return items;
    }
    
    public void setItems(List<EstimateItem> items) {
        this.items = items;
    }
    
    public List<EstimateDocument> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<EstimateDocument> documents) {
        this.documents = documents;
    }
    
    // Utility methods
    public void calculateTaxAndTotal() {
        if (totalAmount != null && taxRate != null) {
            this.taxAmount = totalAmount.multiply(taxRate).divide(new BigDecimal("100"));
            this.totalWithTax = totalAmount.add(taxAmount);
        }
    }
    
    public void addItem(EstimateItem item) {
        items.add(item);
        item.setEstimate(this);
        recalculateTotal();
    }
    
    public void removeItem(EstimateItem item) {
        items.remove(item);
        item.setEstimate(null);
        recalculateTotal();
    }
    
    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(EstimateItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        calculateTaxAndTotal();
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