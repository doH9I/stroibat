package com.constructionmanager.app.data.repository

import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.dao.ProjectDao
import com.constructionmanager.app.data.entities.Project
import com.constructionmanager.app.data.entities.ProjectStatus
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao
) {
    
    fun getAllProjects(): Flow<List<Project>> = projectDao.getAllProjects()
    
    suspend fun getProjectById(id: Long): Project? = projectDao.getProjectById(id)
    
    fun getProjectsByManager(managerId: Long): Flow<List<Project>> = 
        projectDao.getProjectsByManager(managerId)
    
    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>> = 
        projectDao.getProjectsByStatus(status)
    
    fun getProjectsByClient(clientId: Long): Flow<List<Project>> = 
        projectDao.getProjectsByClient(clientId)
    
    suspend fun insertProject(project: Project): Long = projectDao.insertProject(project)
    
    suspend fun updateProject(project: Project) = projectDao.updateProject(project)
    
    suspend fun deleteProject(project: Project) = projectDao.deleteProject(project)
    
    suspend fun getProjectsCount(): Int = projectDao.getProjectsCount()
    
    suspend fun getProjectsCountByStatus(status: ProjectStatus): Int = 
        projectDao.getProjectsCountByStatus(status)
    
    suspend fun getTotalBudgetByStatus(status: ProjectStatus): BigDecimal? = 
        projectDao.getTotalBudgetByStatus(status)
    
    fun searchProjects(query: String): Flow<List<Project>> = 
        projectDao.searchProjects(query)
    
    // Дополнительные методы для дашборда
    suspend fun getRecentProjects(limit: Int = 5): List<Project> {
        // Здесь можно добавить логику для получения последних проектов
        return getAllProjects().let { flow ->
            // Простая реализация - возвращаем первые проекты
            // В реальном приложении здесь была бы более сложная логика
            emptyList() // TODO: Implement proper recent projects logic
        }
    }
}