package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.Issue
import com.constructionmanager.app.data.entities.IssueStatus
import com.constructionmanager.app.data.entities.IssuePriority
import com.constructionmanager.app.data.entities.IssueCategory

@Dao
interface IssueDao {
    
    @Query("SELECT * FROM issues ORDER BY created_at DESC")
    fun getAllIssues(): Flow<List<Issue>>
    
    @Query("SELECT * FROM issues WHERE id = :id")
    suspend fun getIssueById(id: Long): Issue?
    
    @Query("SELECT * FROM issues WHERE project_id = :projectId ORDER BY created_at DESC")
    fun getIssuesByProject(projectId: Long): Flow<List<Issue>>
    
    @Query("SELECT * FROM issues WHERE status = :status ORDER BY created_at DESC")
    fun getIssuesByStatus(status: IssueStatus): Flow<List<Issue>>
    
    @Query("SELECT * FROM issues WHERE priority = :priority ORDER BY created_at DESC")
    fun getIssuesByPriority(priority: IssuePriority): Flow<List<Issue>>
    
    @Query("SELECT * FROM issues WHERE category = :category ORDER BY created_at DESC")
    fun getIssuesByCategory(category: IssueCategory): Flow<List<Issue>>
    
    @Query("SELECT * FROM issues WHERE assigned_to = :userId ORDER BY created_at DESC")
    fun getIssuesAssignedToUser(userId: Long): Flow<List<Issue>>
    
    @Query("SELECT * FROM issues WHERE reported_by = :userId ORDER BY created_at DESC")
    fun getIssuesReportedByUser(userId: Long): Flow<List<Issue>>
    
    @Insert
    suspend fun insertIssue(issue: Issue): Long
    
    @Update
    suspend fun updateIssue(issue: Issue)
    
    @Delete
    suspend fun deleteIssue(issue: Issue)
    
    @Query("SELECT COUNT(*) FROM issues WHERE status = :status")
    suspend fun getIssuesCountByStatus(status: IssueStatus): Int
    
    @Query("SELECT COUNT(*) FROM issues WHERE priority = :priority AND status != 'CLOSED'")
    suspend fun getOpenIssuesCountByPriority(priority: IssuePriority): Int
}