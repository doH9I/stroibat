package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import java.math.BigDecimal

@Entity(
    tableName = "estimate_items",
    foreignKeys = [
        ForeignKey(
            entity = Estimate::class,
            parentColumns = ["id"],
            childColumns = ["estimate_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkType::class,
            parentColumns = ["id"],
            childColumns = ["work_type_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class EstimateItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "estimate_id")
    val estimateId: Long,
    
    @ColumnInfo(name = "work_type_id")
    val workTypeId: Long,
    
    @ColumnInfo(name = "description")
    val description: String,
    
    @ColumnInfo(name = "quantity")
    val quantity: BigDecimal,
    
    @ColumnInfo(name = "unit_price")
    val unitPrice: BigDecimal,
    
    @ColumnInfo(name = "total_price")
    val totalPrice: BigDecimal,
    
    @ColumnInfo(name = "order_index")
    val orderIndex: Int
)