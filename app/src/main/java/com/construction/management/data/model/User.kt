package com.construction.management.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import java.time.LocalDateTime

@Entity(tableName = "users")
@Parcelize
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastLoginAt: LocalDateTime? = null,
    val organizationId: Long? = null,
    val profileImageUrl: String? = null
) : Parcelable

enum class UserRole(val displayName: String, val permissions: List<Permission>) {
    ADMIN("Администратор", Permission.values().toList()),
    GENERAL_DIRECTOR("Генеральный директор", listOf(
        Permission.VIEW_PROJECTS,
        Permission.EDIT_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_ANALYTICS,
        Permission.VIEW_USERS,
        Permission.EDIT_USERS,
        Permission.VIEW_ORGANIZATIONS,
        Permission.EDIT_ORGANIZATIONS,
        Permission.VIEW_ESTIMATES,
        Permission.EDIT_ESTIMATES,
        Permission.EXPORT_REPORTS
    )),
    CONSTRUCTION_DIRECTOR("Директор по строительству", listOf(
        Permission.VIEW_PROJECTS,
        Permission.EDIT_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_ANALYTICS,
        Permission.VIEW_USERS,
        Permission.EDIT_USERS,
        Permission.VIEW_ORGANIZATIONS,
        Permission.VIEW_ESTIMATES,
        Permission.EDIT_ESTIMATES,
        Permission.EXPORT_REPORTS
    )),
    FINANCIAL_DIRECTOR("Финансовый директор", listOf(
        Permission.VIEW_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_ANALYTICS,
        Permission.VIEW_ESTIMATES,
        Permission.EDIT_ESTIMATES,
        Permission.EXPORT_REPORTS
    )),
    PROJECT_MANAGER("Руководитель проекта", listOf(
        Permission.VIEW_PROJECTS,
        Permission.EDIT_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_ESTIMATES,
        Permission.EDIT_ESTIMATES,
        Permission.EXPORT_REPORTS
    )),
    SITE_MANAGER("Начальник участка", listOf(
        Permission.VIEW_PROJECTS,
        Permission.EDIT_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_ESTIMATES,
        Permission.EXPORT_REPORTS
    )),
    WORK_PRODUCER("Производитель работ", listOf(
        Permission.VIEW_PROJECTS,
        Permission.EDIT_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_ESTIMATES,
        Permission.EXPORT_REPORTS
    )),
    FOREMAN("Бригадир", listOf(
        Permission.VIEW_PROJECTS,
        Permission.EDIT_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_ESTIMATES
    )),
    MASTER("Мастер", listOf(
        Permission.VIEW_PROJECTS,
        Permission.EDIT_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.VIEW_ESTIMATES
    )),
    ESTIMATOR("Сметчик", listOf(
        Permission.VIEW_PROJECTS,
        Permission.VIEW_ESTIMATES,
        Permission.EDIT_ESTIMATES,
        Permission.EXPORT_REPORTS
    )),
    PTO_ENGINEER("Сотрудник ПТО", listOf(
        Permission.VIEW_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.EDIT_REPORTS,
        Permission.EXPORT_REPORTS
    )),
    SAFETY_OFFICER("Ответственный по ТБ", listOf(
        Permission.VIEW_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.EDIT_REPORTS
    )),
    WORKER("Рабочий", listOf(
        Permission.VIEW_PROJECTS,
        Permission.VIEW_REPORTS,
        Permission.EDIT_REPORTS
    ))
}

enum class Permission {
    VIEW_PROJECTS,
    EDIT_PROJECTS,
    DELETE_PROJECTS,
    VIEW_REPORTS,
    EDIT_REPORTS,
    DELETE_REPORTS,
    VIEW_ANALYTICS,
    VIEW_USERS,
    EDIT_USERS,
    DELETE_USERS,
    VIEW_ORGANIZATIONS,
    EDIT_ORGANIZATIONS,
    DELETE_ORGANIZATIONS,
    VIEW_ESTIMATES,
    EDIT_ESTIMATES,
    DELETE_ESTIMATES,
    EXPORT_REPORTS,
    VIEW_MATERIALS,
    EDIT_MATERIALS,
    VIEW_TIME_SHEETS,
    EDIT_TIME_SHEETS,
    VIEW_DEFECTS,
    EDIT_DEFECTS,
    VIEW_PHOTOS,
    EDIT_PHOTOS
}