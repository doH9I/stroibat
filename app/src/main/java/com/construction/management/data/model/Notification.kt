package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "notifications")
@Parcelize
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: NotificationType,
    val priority: NotificationPriority,
    val isRead: Boolean = false,
    val isSent: Boolean = false,
    val scheduledAt: LocalDateTime? = null,
    val sentAt: LocalDateTime? = null,
    val readAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val data: String? = null // JSON data for additional info
) : Parcelable

enum class NotificationType(val displayName: String) {
    PROJECT_UPDATE("Обновление проекта"),
    TASK_ASSIGNMENT("Назначение задачи"),
    DEADLINE_REMINDER("Напоминание о дедлайне"),
    DEFECT_REPORT("Отчет о дефекте"),
    MATERIAL_ORDER("Заказ материалов"),
    REPORT_APPROVAL("Согласование отчета"),
    SAFETY_ALERT("Предупреждение по безопасности"),
    SYSTEM_UPDATE("Обновление системы"),
    CUSTOM("Пользовательское уведомление")
}

enum class NotificationPriority(val displayName: String) {
    LOW("Низкий"),
    NORMAL("Обычный"),
    HIGH("Высокий"),
    URGENT("Срочный")
}