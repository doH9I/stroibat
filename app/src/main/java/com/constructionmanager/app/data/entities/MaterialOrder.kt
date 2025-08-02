package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.util.Date
import java.math.BigDecimal

@Entity(
    tableName = "material_orders",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["id"],
            childColumns = ["project_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Organization::class,
            parentColumns = ["id"],
            childColumns = ["supplier_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class MaterialOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "project_id")
    val projectId: Long,
    
    @ColumnInfo(name = "order_number")
    val orderNumber: String,
    
    @ColumnInfo(name = "supplier_id")
    val supplierId: Long,
    
    @ColumnInfo(name = "status")
    val status: OrderStatus,
    
    @ColumnInfo(name = "total_amount")
    val totalAmount: BigDecimal,
    
    @ColumnInfo(name = "delivery_date")
    val deliveryDate: Date?,
    
    @ColumnInfo(name = "delivery_address")
    val deliveryAddress: String?,
    
    @ColumnInfo(name = "notes")
    val notes: String?,
    
    @ColumnInfo(name = "created_by")
    val createdBy: Long,
    
    @ColumnInfo(name = "approved_by")
    val approvedBy: Long? = null,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Date = Date(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)

enum class OrderStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    ORDERED,
    DELIVERED,
    CANCELLED
}