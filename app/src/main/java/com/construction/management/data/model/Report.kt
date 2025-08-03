package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "reports")
@Parcelize
data class Report(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val title: String,
    val description: String,
    val type: ReportType,
    val status: ReportStatus,
    val createdBy: Long,
    val approvedBy: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val approvedAt: LocalDateTime? = null,
    val data: String? = null // JSON data for report content
) : Parcelable

enum class ReportType(val displayName: String) {
    DAILY("Ежедневный отчет"),
    WEEKLY("Недельный отчет"),
    MONTHLY("Месячный отчет"),
    PROGRESS("Отчет о прогрессе"),
    QUALITY("Отчет о качестве"),
    SAFETY("Отчет по безопасности"),
    MATERIAL("Отчет по материалам"),
    TIME("Отчет по времени"),
    FINANCIAL("Финансовый отчет"),
    CUSTOM("Пользовательский отчет")
}

enum class ReportStatus(val displayName: String) {
    DRAFT("Черновик"),
    PENDING_APPROVAL("На согласовании"),
    APPROVED("Утвержден"),
    REJECTED("Отклонен"),
    PUBLISHED("Опубликован")
}