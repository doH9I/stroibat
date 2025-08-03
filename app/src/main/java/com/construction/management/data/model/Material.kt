package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "materials")
@Parcelize
data class Material(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val name: String,
    val description: String? = null,
    val category: MaterialCategory,
    val unit: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double,
    val supplierId: Long? = null,
    val supplierName: String? = null,
    val orderNumber: String? = null,
    val status: MaterialStatus,
    val orderedAt: LocalDateTime? = null,
    val deliveredAt: LocalDateTime? = null,
    val orderedBy: Long,
    val notes: String? = null
) : Parcelable

@Entity(tableName = "material_categories")
@Parcelize
data class MaterialCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val parentId: Long? = null
) : Parcelable

enum class MaterialStatus(val displayName: String) {
    PLANNED("Запланирован"),
    ORDERED("Заказан"),
    IN_TRANSIT("В пути"),
    DELIVERED("Доставлен"),
    INSTALLED("Установлен"),
    RETURNED("Возвращен")
}

enum class MaterialCategoryType(val displayName: String) {
    CONCRETE("Бетон"),
    REINFORCEMENT("Арматура"),
    BRICKS("Кирпич"),
    BLOCKS("Блоки"),
    INSULATION("Утеплитель"),
    ROOFING("Кровельные материалы"),
    FINISHING("Отделочные материалы"),
    ELECTRICAL("Электротехнические"),
    PLUMBING("Сантехнические"),
    TOOLS("Инструменты"),
    EQUIPMENT("Оборудование"),
    OTHER("Прочие")
}