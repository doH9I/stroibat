package com.construction.management.data.repository

import com.construction.management.data.database.dao.ProjectDao
import com.construction.management.data.model.Project
import com.construction.management.data.model.ProjectStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao
) {
    
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()
    
    suspend fun getProjectById(projectId: Long): Project? = projectDao.getProjectById(projectId)
    
    fun getProjectsByOrganization(organizationId: Long): Flow<List<Project>> = projectDao.getProjectsByOrganization(organizationId)
    
    fun getProjectsByManager(managerId: Long): Flow<List<Project>> = projectDao.getProjectsByManager(managerId)
    
    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> = projectDao.getProjectsByStatus(status)
    
    fun getProjectsByStatuses(statuses: List<ProjectStatus>): Flow<List<Project>> = projectDao.getProjectsByStatuses(statuses)
    
    fun getProjectsFromDate(startDate: LocalDateTime): Flow<List<Project>> = projectDao.getProjectsFromDate(startDate)
    
    fun getProjectsUntilDate(endDate: LocalDateTime): Flow<List<Project>> = projectDao.getProjectsUntilDate(endDate)
    
    suspend fun getProjectsCount(): Int = projectDao.getProjectsCount()
    
    suspend fun getProjectsCountByStatus(status: ProjectStatus): Int = projectDao.getProjectsCountByStatus(status)
    
    suspend fun getTotalBudgetByStatus(status: ProjectStatus): Double? = projectDao.getTotalBudgetByStatus(status)
    
    suspend fun insertProject(project: Project): Long = projectDao.insertProject(project)
    
    suspend fun updateProject(project: Project) = projectDao.updateProject(project)
    
    suspend fun updateProjectStatus(projectId: Long, status: ProjectStatus) = projectDao.updateProjectStatus(projectId, status)
    
    suspend fun updateProjectTimestamp(projectId: Long, updatedAt: LocalDateTime) = projectDao.updateProjectTimestamp(projectId, updatedAt)
    
    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)
    
    suspend fun deleteProjectById(projectId: Long) = projectDao.deleteProjectById(projectId)
}