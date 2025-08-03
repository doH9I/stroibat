package com.construction.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private RoleName name;
    
    private String description;
    
    public enum RoleName {
        ROLE_ADMIN,
        ROLE_MANAGER,
        ROLE_ENGINEER,
        ROLE_WORKER,
        ROLE_ACCOUNTANT,
        ROLE_CLIENT
    }
    
    // Constructors
    public Role() {}
    
    public Role(RoleName name) {
        this.name = name;
    }
    
    public Role(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public RoleName getName() {
        return name;
    }
    
    public void setName(RoleName name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}