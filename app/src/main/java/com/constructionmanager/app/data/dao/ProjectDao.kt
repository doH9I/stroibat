package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.Project
import com.constructionmanager.app.data.entities.ProjectStatus
import java.math.BigDecimal

@Dao
interface ProjectDao {
    
    @Query("SELECT * FROM projects ORDER BY created_at DESC")
    fun getAllProjects(): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): Project?
    
    @Query("SELECT * FROM projects WHERE manager_id = :managerId ORDER BY created_at DESC")
    fun getProjectsByManager(managerId: Long): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE status = :status ORDER BY created_at DESC")
    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE client_id = :clientId ORDER BY created_at DESC")
    fun getProjectsByClient(clientId: Long): Flow<List<Project>>
    
    @Insert
    suspend fun insertProject(project: Project): Long
    
    @Update
    suspend fun updateProject(project: Project)
    
    @Delete
    suspend fun deleteProject(project: Project)
    
    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectsCount(): Int
    
    @Query("SELECT COUNT(*) FROM projects WHERE status = :status")
    suspend fun getProjectsCountByStatus(status: ProjectStatus): Int
    
    @Query("SELECT SUM(budget) FROM projects WHERE status = :status")
    suspend fun getTotalBudgetByStatus(status: ProjectStatus): BigDecimal?
    
    @Query("SELECT * FROM projects WHERE name LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%'")
    fun searchProjects(query: String): Flow<List<Project>>
}