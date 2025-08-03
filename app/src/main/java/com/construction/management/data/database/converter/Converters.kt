package com.construction.management.data.database.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.construction.management.data.model.*

class LocalDateTimeConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}

class UserRoleConverter {
    @TypeConverter
    fun fromUserRole(value: UserRole): String {
        return value.name
    }

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }
}

class ProjectStatusConverter {
    @TypeConverter
    fun fromProjectStatus(value: ProjectStatus): String {
        return value.name
    }

    @TypeConverter
    fun toProjectStatus(value: String): ProjectStatus {
        return ProjectStatus.valueOf(value)
    }
}

class EstimateStatusConverter {
    @TypeConverter
    fun fromEstimateStatus(value: EstimateStatus): String {
        return value.name
    }

    @TypeConverter
    fun toEstimateStatus(value: String): EstimateStatus {
        return EstimateStatus.valueOf(value)
    }
}

class OrganizationTypeConverter {
    @TypeConverter
    fun fromOrganizationType(value: OrganizationType): String {
        return value.name
    }

    @TypeConverter
    fun toOrganizationType(value: String): OrganizationType {
        return OrganizationType.valueOf(value)
    }
}

class WorkCategoryTypeConverter {
    @TypeConverter
    fun fromWorkCategoryType(value: WorkCategoryType): String {
        return value.name
    }

    @TypeConverter
    fun toWorkCategoryType(value: String): WorkCategoryType {
        return WorkCategoryType.valueOf(value)
    }
}

class PermissionConverter {
    @TypeConverter
    fun fromPermissionList(value: List<Permission>): String {
        return value.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toPermissionList(value: String): List<Permission> {
        return if (value.isEmpty()) emptyList() else value.split(",").map { Permission.valueOf(it) }
    }
}