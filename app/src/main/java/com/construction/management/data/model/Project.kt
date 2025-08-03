package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "projects")
@Parcelize
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime? = null,
    val budget: Double,
    val status: ProjectStatus,
    val managerId: Long,
    val organizationId: Long,
    val clientId: Long? = null,
    val contractorId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) : Parcelable

enum class ProjectStatus(val displayName: String) {
    PLANNING("Планирование"),
    IN_PROGRESS("В работе"),
    ON_HOLD("Приостановлен"),
    COMPLETED("Завершен"),
    CANCELLED("Отменен")
}