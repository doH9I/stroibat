package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date
import java.math.BigDecimal

@Entity(
    tableName = "estimates",
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
data class Estimate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "project_id")
    val projectId: Long,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "version")
    val version: Int = 1,
    
    @ColumnInfo(name = "total_amount")
    val totalAmount: BigDecimal,
    
    @ColumnInfo(name = "vat_rate")
    val vatRate: BigDecimal,
    
    @ColumnInfo(name = "vat_amount")
    val vatAmount: BigDecimal,
    
    @ColumnInfo(name = "total_with_vat")
    val totalWithVat: BigDecimal,
    
    @ColumnInfo(name = "overhead_rate")
    val overheadRate: BigDecimal,
    
    @ColumnInfo(name = "contingency_rate")
    val contingencyRate: BigDecimal,
    
    @ColumnInfo(name = "status")
    val status: EstimateStatus,
    
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

enum class EstimateStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    ARCHIVED
}