package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "defects")
@Parcelize
data class Defect(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val title: String,
    val description: String,
    val category: DefectCategory,
    val severity: DefectSeverity,
    val status: DefectStatus,
    val location: String,
    val volume: String? = null,
    val photoIds: String? = null, // JSON array of photo IDs
    val reportedBy: Long,
    val assignedTo: Long? = null,
    val reportedAt: LocalDateTime = LocalDateTime.now(),
    val dueDate: LocalDateTime? = null,
    val fixedAt: LocalDateTime? = null,
    val fixedBy: Long? = null,
    val notes: String? = null
) : Parcelable

enum class DefectCategory(val displayName: String) {
    STRUCTURAL("Конструктивные"),
    FINISHING("Отделочные"),
    ELECTRICAL("Электрические"),
    PLUMBING("Сантехнические"),
    HVAC("Вентиляция и кондиционирование"),
    SAFETY("Безопасность"),
    QUALITY("Качество"),
    OTHER("Прочие")
}

enum class DefectSeverity(val displayName: String) {
    LOW("Низкая"),
    MEDIUM("Средняя"),
    HIGH("Высокая"),
    CRITICAL("Критическая")
}

enum class DefectStatus(val displayName: String) {
    OPEN("Открыт"),
    IN_PROGRESS("В работе"),
    FIXED("Исправлен"),
    VERIFIED("Проверен"),
    CLOSED("Закрыт"),
    REOPENED("Переоткрыт")
}