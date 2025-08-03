package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime
import java.time.LocalDate

@Entity(tableName = "time_sheets")
@Parcelize
data class TimeSheet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val userId: Long,
    val date: LocalDate,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val breakStartTime: LocalDateTime? = null,
    val breakEndTime: LocalDateTime? = null,
    val totalHours: Double = 0.0,
    val overtimeHours: Double = 0.0,
    val status: TimeSheetStatus,
    val notes: String? = null,
    val approvedBy: Long? = null,
    val approvedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) : Parcelable

enum class TimeSheetStatus(val displayName: String) {
    DRAFT("Черновик"),
    SUBMITTED("Отправлен"),
    APPROVED("Утвержден"),
    REJECTED("Отклонен")
}