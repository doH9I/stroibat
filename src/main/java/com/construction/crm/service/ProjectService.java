package com.construction.crm.service;

import com.construction.crm.entity.Project;
import com.construction.crm.entity.User;
import com.construction.crm.repository.ProjectRepository;
import com.construction.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }
    
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
    
    public Project createProject(Project project) {
        // Validate project data
        if (project.getStartDate() == null) {
            throw new RuntimeException("Project start date is required");
        }
        
        // Set default status if not provided
        if (project.getStatus() == null) {
            project.setStatus(Project.ProjectStatus.PLANNING);
        }
        
        return projectRepository.save(project);
    }
    
    public Project updateProject(Long id, Project projectDetails) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Update basic fields
        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setAddress(projectDetails.getAddress());
        project.setStartDate(projectDetails.getStartDate());
        project.setPlannedEndDate(projectDetails.getPlannedEndDate());
        project.setActualEndDate(projectDetails.getActualEndDate());
        project.setStatus(projectDetails.getStatus());
        project.setType(projectDetails.getType());
        project.setBudget(projectDetails.getBudget());
        project.setActualCost(projectDetails.getActualCost());
        project.setClientName(projectDetails.getClientName());
        project.setClientPhone(projectDetails.getClientPhone());
        project.setClientEmail(projectDetails.getClientEmail());
        project.setContractNumber(projectDetails.getContractNumber());
        project.setContractDate(projectDetails.getContractDate());
        project.setContractValue(projectDetails.getContractValue());
        project.setProjectManager(projectDetails.getProjectManager());
        project.setArchitect(projectDetails.getArchitect());
        project.setEngineer(projectDetails.getEngineer());
        
        return projectRepository.save(project);
    }
    
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }
    
    public List<Project> getProjectsByStatus(Project.ProjectStatus status) {
        return projectRepository.findByStatus(status);
    }
    
    public List<Project> getProjectsByType(Project.ProjectType type) {
        return projectRepository.findByType(type);
    }
    
    public List<Project> getProjectsByClient(String clientName) {
        return projectRepository.findByClientNameContainingIgnoreCase(clientName);
    }
    
    public List<Project> getProjectsByAddress(String address) {
        return projectRepository.findByAddressContainingIgnoreCase(address);
    }
    
    public List<Project> getProjectsByManager(String projectManager) {
        return projectRepository.findByProjectManager(projectManager);
    }
    
    public List<Project> getProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        return projectRepository.findByStartDateBetween(startDate, endDate);
    }
    
    public List<Project> getOverdueProjects() {
        return projectRepository.findOverdueProjects(LocalDate.now());
    }
    
    public List<Project> getProjectsByContractValueRange(BigDecimal minValue, BigDecimal maxValue) {
        return projectRepository.findByContractValueRange(minValue, maxValue);
    }
    
    public List<Project> getProjectsByAssignedUser(Long userId) {
        return projectRepository.findByAssignedUserId(userId);
    }
    

    
    public void assignProjectToUser(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        project.setAssignedTo(user);
        projectRepository.save(project);
    }
    
    public void updateProjectStatus(Long projectId, Project.ProjectStatus status) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        project.setStatus(status);
        
        // If project is completed, set actual end date
        if (status == Project.ProjectStatus.COMPLETED && project.getActualEndDate() == null) {
            project.setActualEndDate(LocalDate.now());
        }
        
        projectRepository.save(project);
    }
    
    public void updateProjectProgress(Long projectId, int progressPercentage) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        // Update status based on progress
        if (progressPercentage >= 100) {
            project.setStatus(Project.ProjectStatus.COMPLETED);
            project.setActualEndDate(LocalDate.now());
        } else if (progressPercentage > 0) {
            project.setStatus(Project.ProjectStatus.IN_PROGRESS);
        }
        
        projectRepository.save(project);
    }
    
    public List<Object[]> getProjectStatsByStatus() {
        return projectRepository.countByStatus();
    }
    
    public List<Object[]> getProjectStatsByType() {
        return projectRepository.countByType();
    }
    
    public BigDecimal getTotalCompletedProjectValue() {
        return projectRepository.getTotalCompletedValue();
    }
    
    public BigDecimal getAverageProjectValue() {
        return projectRepository.getAverageProjectValue();
    }
    
    public List<Project> getActiveProjects() {
        return projectRepository.findByStatus(Project.ProjectStatus.IN_PROGRESS);
    }
    
    public List<Project> getPlanningProjects() {
        return projectRepository.findByStatus(Project.ProjectStatus.PLANNING);
    }
    
    public List<Project> getCompletedProjects() {
        return projectRepository.findByStatus(Project.ProjectStatus.COMPLETED);
    }
    
    public List<Project> getOnHoldProjects() {
        return projectRepository.findByStatus(Project.ProjectStatus.ON_HOLD);
    }
    
    public List<Project> getCancelledProjects() {
        return projectRepository.findByStatus(Project.ProjectStatus.CANCELLED);
    }
    
    public void updateProjectCosts(Long projectId, BigDecimal actualCost) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        project.setActualCost(actualCost);
        projectRepository.save(project);
    }
    
    public BigDecimal getProjectProfit(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return project.getProfit();
    }
    
    public int getProjectProgressPercentage(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        return project.getProgressPercentage();
    }
}