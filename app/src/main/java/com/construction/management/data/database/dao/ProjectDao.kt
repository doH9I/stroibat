package com.construction.management.data.database.dao

import androidx.room.*
import com.construction.management.data.model.Project
import com.construction.management.data.model.ProjectStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ProjectDao {
    
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: Long): Project?
    
    @Query("SELECT * FROM projects WHERE organizationId = :organizationId")
    fun getProjectsByOrganization(organizationId: Long): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE managerId = :managerId")
    fun getProjectsByManager(managerId: Long): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE status = :status")
    fun getProjectsByStatus(status: ProjectStatus): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE status IN (:statuses)")
    fun getProjectsByStatuses(statuses: List<ProjectStatus>): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE startDate >= :startDate")
    fun getProjectsFromDate(startDate: LocalDateTime): Flow<List<Project>>
    
    @Query("SELECT * FROM projects WHERE endDate <= :endDate")
    fun getProjectsUntilDate(endDate: LocalDateTime): Flow<List<Project>>
    
    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectsCount(): Int
    
    @Query("SELECT COUNT(*) FROM projects WHERE status = :status")
    suspend fun getProjectsCountByStatus(status: ProjectStatus): Int
    
    @Query("SELECT SUM(budget) FROM projects WHERE status = :status")
    suspend fun getTotalBudgetByStatus(status: ProjectStatus): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project): Long
    
    @Update
    suspend fun updateProject(project: Project)
    
    @Query("UPDATE projects SET status = :status WHERE id = :projectId")
    suspend fun updateProjectStatus(projectId: Long, status: ProjectStatus)
    
    @Query("UPDATE projects SET updatedAt = :updatedAt WHERE id = :projectId")
    suspend fun updateProjectTimestamp(projectId: Long, updatedAt: LocalDateTime)
    
    @Delete
    suspend fun deleteProject(project: Project)
    
    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Long)
}