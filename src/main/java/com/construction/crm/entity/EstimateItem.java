package com.construction.crm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Entity
@Table(name = "estimate_items")
public class EstimateItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotNull
    @Positive
    private BigDecimal quantity;
    
    private String unit;
    
    @NotNull
    @Positive
    private BigDecimal unitPrice;
    
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    private ItemType type;
    
    private String category;
    
    private String subcategory;
    
    private String supplier;
    
    private String materialCode;
    
    private String specifications;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_id")
    private Estimate estimate;
    
    public enum ItemType {
        MATERIAL,
        LABOR,
        EQUIPMENT,
        SUBCONTRACTOR,
        OVERHEAD,
        PROFIT
    }
    
    // Constructors
    public EstimateItem() {}
    
    public EstimateItem(String name, BigDecimal quantity, BigDecimal unitPrice, ItemType type) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.type = type;
        calculateTotal();
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
    
    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        calculateTotal();
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotal();
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public ItemType getType() {
        return type;
    }
    
    public void setType(ItemType type) {
        this.type = type;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getSubcategory() {
        return subcategory;
    }
    
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }
    
    public String getSupplier() {
        return supplier;
    }
    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    
    public String getMaterialCode() {
        return materialCode;
    }
    
    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }
    
    public String getSpecifications() {
        return specifications;
    }
    
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }
    
    public Estimate getEstimate() {
        return estimate;
    }
    
    public void setEstimate(Estimate estimate) {
        this.estimate = estimate;
    }
    
    // Utility methods
    public void calculateTotal() {
        if (quantity != null && unitPrice != null) {
            this.totalAmount = quantity.multiply(unitPrice);
        }
    }
    
    public String getDisplayName() {
        return name + (description != null ? " - " + description : "");
    }
}