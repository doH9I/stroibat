package com.constructionmanager.app.data.repository

import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.dao.IssueDao
import com.constructionmanager.app.data.entities.Issue
import com.constructionmanager.app.data.entities.IssueStatus
import com.constructionmanager.app.data.entities.IssuePriority
import com.constructionmanager.app.data.entities.IssueCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssueRepository @Inject constructor(
    private val issueDao: IssueDao
) {
    
    fun getAllIssues(): Flow<List<Issue>> = issueDao.getAllIssues()
    
    suspend fun getIssueById(id: Long): Issue? = issueDao.getIssueById(id)
    
    fun getIssuesByProject(projectId: Long): Flow<List<Issue>> = 
        issueDao.getIssuesByProject(projectId)
    
    fun getIssuesByStatus(status: IssueStatus): Flow<List<Issue>> = 
        issueDao.getIssuesByStatus(status)
    
    fun getIssuesByPriority(priority: IssuePriority): Flow<List<Issue>> = 
        issueDao.getIssuesByPriority(priority)
    
    fun getIssuesByCategory(category: IssueCategory): Flow<List<Issue>> = 
        issueDao.getIssuesByCategory(category)
    
    fun getIssuesAssignedToUser(userId: Long): Flow<List<Issue>> = 
        issueDao.getIssuesAssignedToUser(userId)
    
    fun getIssuesReportedByUser(userId: Long): Flow<List<Issue>> = 
        issueDao.getIssuesReportedByUser(userId)
    
    suspend fun insertIssue(issue: Issue): Long = issueDao.insertIssue(issue)
    
    suspend fun updateIssue(issue: Issue) = issueDao.updateIssue(issue)
    
    suspend fun deleteIssue(issue: Issue) = issueDao.deleteIssue(issue)
    
    suspend fun getIssuesCountByStatus(status: IssueStatus): Int = 
        issueDao.getIssuesCountByStatus(status)
    
    suspend fun getOpenIssuesCountByPriority(priority: IssuePriority): Int = 
        issueDao.getOpenIssuesCountByPriority(priority)
    
    // Дополнительные методы для дашборда
    suspend fun getOpenIssuesCount(): Int {
        return getIssuesCountByStatus(IssueStatus.OPEN) + 
               getIssuesCountByStatus(IssueStatus.IN_PROGRESS)
    }
}