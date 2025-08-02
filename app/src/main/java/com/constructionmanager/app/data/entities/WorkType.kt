package com.constructionmanager.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.math.BigDecimal

@Entity(tableName = "work_types")
data class WorkType(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "category")
    val category: WorkCategory,
    
    @ColumnInfo(name = "unit")
    val unit: String,
    
    @ColumnInfo(name = "base_price")
    val basePrice: BigDecimal,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)

enum class WorkCategory {
    EARTHWORKS,
    FOUNDATION,
    WALLS,
    ROOFING,
    FINISHING,
    ELECTRICAL,
    PLUMBING,
    HVAC,
    OTHER
}