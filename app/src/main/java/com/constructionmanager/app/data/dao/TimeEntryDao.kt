package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.TimeEntry
import com.constructionmanager.app.data.entities.TimeEntryStatus
import java.util.Date

@Dao
interface TimeEntryDao {
    
    @Query("SELECT * FROM time_entries ORDER BY work_date DESC")
    fun getAllTimeEntries(): Flow<List<TimeEntry>>
    
    @Query("SELECT * FROM time_entries WHERE id = :id")
    suspend fun getTimeEntryById(id: Long): TimeEntry?
    
    @Query("SELECT * FROM time_entries WHERE user_id = :userId ORDER BY work_date DESC")
    fun getTimeEntriesByUser(userId: Long): Flow<List<TimeEntry>>
    
    @Query("SELECT * FROM time_entries WHERE project_id = :projectId ORDER BY work_date DESC")
    fun getTimeEntriesByProject(projectId: Long): Flow<List<TimeEntry>>
    
    @Query("SELECT * FROM time_entries WHERE status = :status ORDER BY work_date DESC")
    fun getTimeEntriesByStatus(status: TimeEntryStatus): Flow<List<TimeEntry>>
    
    @Query("SELECT * FROM time_entries WHERE work_date BETWEEN :startDate AND :endDate ORDER BY work_date DESC")
    fun getTimeEntriesByDateRange(startDate: Date, endDate: Date): Flow<List<TimeEntry>>
    
    @Query("SELECT * FROM time_entries WHERE user_id = :userId AND work_date BETWEEN :startDate AND :endDate ORDER BY work_date DESC")
    fun getTimeEntriesByUserAndDateRange(userId: Long, startDate: Date, endDate: Date): Flow<List<TimeEntry>>
    
    @Insert
    suspend fun insertTimeEntry(timeEntry: TimeEntry): Long
    
    @Update
    suspend fun updateTimeEntry(timeEntry: TimeEntry)
    
    @Delete
    suspend fun deleteTimeEntry(timeEntry: TimeEntry)
    
    @Query("SELECT SUM(work_hours) FROM time_entries WHERE user_id = :userId AND work_date BETWEEN :startDate AND :endDate AND status = 'APPROVED'")
    suspend fun getTotalWorkedHoursByUser(userId: Long, startDate: Date, endDate: Date): Float?
    
    @Query("SELECT SUM(overtime_hours) FROM time_entries WHERE user_id = :userId AND work_date BETWEEN :startDate AND :endDate AND status = 'APPROVED'")
    suspend fun getTotalOvertimeHoursByUser(userId: Long, startDate: Date, endDate: Date): Float?
}