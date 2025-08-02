package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.Report
import com.constructionmanager.app.data.entities.ReportType
import com.constructionmanager.app.data.entities.ReportStatus
import java.util.Date

@Dao
interface ReportDao {
    
    @Query("SELECT * FROM reports ORDER BY work_date DESC")
    fun getAllReports(): Flow<List<Report>>
    
    @Query("SELECT * FROM reports WHERE id = :id")
    suspend fun getReportById(id: Long): Report?
    
    @Query("SELECT * FROM reports WHERE project_id = :projectId ORDER BY work_date DESC")
    fun getReportsByProject(projectId: Long): Flow<List<Report>>
    
    @Query("SELECT * FROM reports WHERE created_by = :userId ORDER BY work_date DESC")
    fun getReportsByUser(userId: Long): Flow<List<Report>>
    
    @Query("SELECT * FROM reports WHERE type = :type ORDER BY work_date DESC")
    fun getReportsByType(type: ReportType): Flow<List<Report>>
    
    @Query("SELECT * FROM reports WHERE status = :status ORDER BY work_date DESC")
    fun getReportsByStatus(status: ReportStatus): Flow<List<Report>>
    
    @Query("SELECT * FROM reports WHERE work_date BETWEEN :startDate AND :endDate ORDER BY work_date DESC")
    fun getReportsByDateRange(startDate: Date, endDate: Date): Flow<List<Report>>
    
    @Insert
    suspend fun insertReport(report: Report): Long
    
    @Update
    suspend fun updateReport(report: Report)
    
    @Delete
    suspend fun deleteReport(report: Report)
    
    @Query("SELECT COUNT(*) FROM reports WHERE status = :status")
    suspend fun getReportsCountByStatus(status: ReportStatus): Int
}