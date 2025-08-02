package com.constructionmanager.app.data.repository

import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.dao.ReportDao
import com.constructionmanager.app.data.entities.Report
import com.constructionmanager.app.data.entities.ReportType
import com.constructionmanager.app.data.entities.ReportStatus
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {
    
    fun getAllReports(): Flow<List<Report>> = reportDao.getAllReports()
    
    suspend fun getReportById(id: Long): Report? = reportDao.getReportById(id)
    
    fun getReportsByProject(projectId: Long): Flow<List<Report>> = 
        reportDao.getReportsByProject(projectId)
    
    fun getReportsByUser(userId: Long): Flow<List<Report>> = 
        reportDao.getReportsByUser(userId)
    
    fun getReportsByType(type: ReportType): Flow<List<Report>> = 
        reportDao.getReportsByType(type)
    
    fun getReportsByStatus(status: ReportStatus): Flow<List<Report>> = 
        reportDao.getReportsByStatus(status)
    
    fun getReportsByDateRange(startDate: Date, endDate: Date): Flow<List<Report>> = 
        reportDao.getReportsByDateRange(startDate, endDate)
    
    suspend fun insertReport(report: Report): Long = reportDao.insertReport(report)
    
    suspend fun updateReport(report: Report) = reportDao.updateReport(report)
    
    suspend fun deleteReport(report: Report) = reportDao.deleteReport(report)
    
    suspend fun getReportsCountByStatus(status: ReportStatus): Int = 
        reportDao.getReportsCountByStatus(status)
    
    // Дополнительные методы для дашборда
    suspend fun getRecentReports(limit: Int = 5): List<Report> {
        // TODO: Implement proper recent reports logic
        return emptyList()
    }
}