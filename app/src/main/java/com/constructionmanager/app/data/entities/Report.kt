package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "reports",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "project_id")
    val projectId: Long,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "type")
    val type: ReportType,
    
    @ColumnInfo(name = "content")
    val content: String,
    
    @ColumnInfo(name = "work_date")
    val workDate: Date,
    
    @ColumnInfo(name = "weather")
    val weather: String?,
    
    @ColumnInfo(name = "temperature")
    val temperature: Int?,
    
    @ColumnInfo(name = "workers_count")
    val workersCount: Int?,
    
    @ColumnInfo(name = "equipment_used")
    val equipmentUsed: String?,
    
    @ColumnInfo(name = "materials_used")
    val materialsUsed: String?,
    
    @ColumnInfo(name = "work_progress")
    val workProgress: String?,
    
    @ColumnInfo(name = "issues")
    val issues: String?,
    
    @ColumnInfo(name = "status")
    val status: ReportStatus,
    
    @ColumnInfo(name = "created_by")
    val createdBy: Long,
    
    @ColumnInfo(name = "approved_by")
    val approvedBy: Long? = null,
    
    @ColumnInfo(name = "approved_at")
    val approvedAt: Date? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)

enum class ReportType {
    DAILY,
    WEEKLY,
    MONTHLY,
    MILESTONE,
    INCIDENT
}

enum class ReportStatus {
    DRAFT,
    SUBMITTED,
    APPROVED,
    REJECTED
}