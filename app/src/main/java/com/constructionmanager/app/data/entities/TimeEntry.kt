package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "time_entries",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TimeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: Long,
    
    @ColumnInfo(name = "project_id")
    val projectId: Long,
    
    @ColumnInfo(name = "work_date")
    val workDate: Date,
    
    @ColumnInfo(name = "start_time")
    val startTime: Date,
    
    @ColumnInfo(name = "end_time")
    val endTime: Date?,
    
    @ColumnInfo(name = "break_duration")
    val breakDuration: Int = 0, // minutes
    
    @ColumnInfo(name = "work_hours")
    val workHours: Float,
    
    @ColumnInfo(name = "overtime_hours")
    val overtimeHours: Float = 0f,
    
    @ColumnInfo(name = "work_description")
    val workDescription: String?,
    
    @ColumnInfo(name = "status")
    val status: TimeEntryStatus,
    
    @ColumnInfo(name = "approved_by")
    val approvedBy: Long? = null,
    
    @ColumnInfo(name = "approved_at")
    val approvedAt: Date? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date()
)

enum class TimeEntryStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED
}