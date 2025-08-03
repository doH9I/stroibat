package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "estimates")
@Parcelize
data class Estimate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val name: String,
    val description: String,
    val totalAmount: Double,
    val totalAmountWithVAT: Double,
    val vatRate: Double = 20.0,
    val contingencyRate: Double = 5.0,
    val status: EstimateStatus,
    val createdBy: Long,
    val approvedBy: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val approvedAt: LocalDateTime? = null
) : Parcelable

@Entity(tableName = "estimate_items")
@Parcelize
data class EstimateItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val estimateId: Long,
    val category: WorkCategory,
    val name: String,
    val description: String,
    val unit: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double,
    val sortOrder: Int
) : Parcelable

@Entity(tableName = "work_categories")
@Parcelize
data class WorkCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val parentId: Long? = null
) : Parcelable

enum class EstimateStatus(val displayName: String) {
    DRAFT("Черновик"),
    PENDING_APPROVAL("На согласовании"),
    APPROVED("Утверждена"),
    REJECTED("Отклонена")
}

enum class WorkCategoryType(val displayName: String) {
    EARTHWORKS("Земляные работы"),
    FOUNDATION("Фундамент"),
    WALLS("Стены"),
    ROOF("Кровля"),
    FINISHING("Отделка"),
    ELECTRICAL("Электрика"),
    PLUMBING("Сантехника"),
    HVAC("Вентиляция и кондиционирование"),
    LANDSCAPING("Благоустройство"),
    OTHER("Прочие работы")
}