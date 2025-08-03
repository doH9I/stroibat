package com.construction.crm.controller;

import com.construction.crm.entity.Project;
import com.construction.crm.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        try {
            Project createdProject = projectService.createProject(project);
            return ResponseEntity.ok(createdProject);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        try {
            Project updatedProject = projectService.updateProject(id, projectDetails);
            return ResponseEntity.ok(updatedProject);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable String status) {
        try {
            Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(projectService.getProjectsByStatus(projectStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getProjectsByType(@PathVariable String type) {
        try {
            Project.ProjectType projectType = Project.ProjectType.valueOf(type.toUpperCase());
            return ResponseEntity.ok(projectService.getProjectsByType(projectType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/client/{clientName}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getProjectsByClient(@PathVariable String clientName) {
        return ResponseEntity.ok(projectService.getProjectsByClient(clientName));
    }
    
    @GetMapping("/address/{address}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getProjectsByAddress(@PathVariable String address) {
        return ResponseEntity.ok(projectService.getProjectsByAddress(address));
    }
    
    @GetMapping("/manager/{projectManager}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Project>> getProjectsByManager(@PathVariable String projectManager) {
        return ResponseEntity.ok(projectService.getProjectsByManager(projectManager));
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getProjectsByDateRange(
            @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(projectService.getProjectsByDateRange(startDate, endDate));
    }
    
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Project>> getOverdueProjects() {
        return ResponseEntity.ok(projectService.getOverdueProjects());
    }
    
    @GetMapping("/contract-value-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Project>> getProjectsByContractValueRange(
            @RequestParam BigDecimal minValue, @RequestParam BigDecimal maxValue) {
        return ResponseEntity.ok(projectService.getProjectsByContractValueRange(minValue, maxValue));
    }
    
    @GetMapping("/assigned-user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Project>> getProjectsByAssignedUser(@PathVariable Long userId) {
        return ResponseEntity.ok(projectService.getProjectsByAssignedUser(userId));
    }
    

    
    @PostMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> assignProjectToUser(@PathVariable Long id, @PathVariable Long userId) {
        try {
            projectService.assignProjectToUser(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> updateProjectStatus(@PathVariable Long id, @PathVariable String status) {
        try {
            Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());
            projectService.updateProjectStatus(id, projectStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/progress/{progressPercentage}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> updateProjectProgress(@PathVariable Long id, @PathVariable int progressPercentage) {
        try {
            projectService.updateProjectProgress(id, progressPercentage);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/stats/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Object[]>> getProjectStatsByStatus() {
        return ResponseEntity.ok(projectService.getProjectStatsByStatus());
    }
    
    @GetMapping("/stats/type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<Object[]>> getProjectStatsByType() {
        return ResponseEntity.ok(projectService.getProjectStatsByType());
    }
    
    @GetMapping("/stats/total-completed-value")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalCompletedProjectValue() {
        return ResponseEntity.ok(projectService.getTotalCompletedProjectValue());
    }
    
    @GetMapping("/stats/average-value")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getAverageProjectValue() {
        return ResponseEntity.ok(projectService.getAverageProjectValue());
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getActiveProjects() {
        return ResponseEntity.ok(projectService.getActiveProjects());
    }
    
    @GetMapping("/planning")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getPlanningProjects() {
        return ResponseEntity.ok(projectService.getPlanningProjects());
    }
    
    @GetMapping("/completed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getCompletedProjects() {
        return ResponseEntity.ok(projectService.getCompletedProjects());
    }
    
    @GetMapping("/on-hold")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getOnHoldProjects() {
        return ResponseEntity.ok(projectService.getOnHoldProjects());
    }
    
    @GetMapping("/cancelled")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<List<Project>> getCancelledProjects() {
        return ResponseEntity.ok(projectService.getCancelledProjects());
    }
    
    @PutMapping("/{id}/costs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> updateProjectCosts(@PathVariable Long id, @RequestBody BigDecimal actualCost) {
        try {
            projectService.updateProjectCosts(id, actualCost);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/profit")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getProjectProfit(@PathVariable Long id) {
        try {
            BigDecimal profit = projectService.getProjectProfit(id);
            return ResponseEntity.ok(profit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/progress-percentage")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('ENGINEER')")
    public ResponseEntity<Integer> getProjectProgressPercentage(@PathVariable Long id) {
        try {
            int progress = projectService.getProjectProgressPercentage(id);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}