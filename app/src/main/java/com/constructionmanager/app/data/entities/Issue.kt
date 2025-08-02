package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date

@Entity(
    tableName = "issues",
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
            childColumns = ["reported_by"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["assigned_to"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Issue(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "project_id")
    val projectId: Long,
    
    @ColumnInfo(name = "title")
    val title: String,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "category")
    val category: IssueCategory,
    
    @ColumnInfo(name = "priority")
    val priority: IssuePriority,
    
    @ColumnInfo(name = "status")
    val status: IssueStatus,
    
    @ColumnInfo(name = "location")
    val location: String?,
    
    @ColumnInfo(name = "work_volume")
    val workVolume: String?,
    
    @ColumnInfo(name = "deadline")
    val deadline: Date?,
    
    @ColumnInfo(name = "reported_by")
    val reportedBy: Long,
    
    @ColumnInfo(name = "assigned_to")
    val assignedTo: Long? = null,
    
    @ColumnInfo(name = "resolved_by")
    val resolvedBy: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date(),
    
    @ColumnInfo(name = "resolved_at")
    val resolvedAt: Date? = null,
    
    @ColumnInfo(name = "resolution_notes")
    val resolutionNotes: String? = null
)

enum class IssueCategory {
    QUALITY_DEFECT,
    SAFETY_VIOLATION,
    MATERIAL_ISSUE,
    EQUIPMENT_PROBLEM,
    DESIGN_ISSUE,
    OTHER
}

enum class IssuePriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class IssueStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    REJECTED
}