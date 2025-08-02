package com.constructionmanager.app.data.converters

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.util.Date
import com.constructionmanager.app.data.entities.*

class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun fromProjectStatus(status: ProjectStatus): String {
        return status.name
    }

    @TypeConverter
    fun toProjectStatus(status: String): ProjectStatus {
        return ProjectStatus.valueOf(status)
    }

    @TypeConverter
    fun fromOrganizationType(type: OrganizationType): String {
        return type.name
    }

    @TypeConverter
    fun toOrganizationType(type: String): OrganizationType {
        return OrganizationType.valueOf(type)
    }

    @TypeConverter
    fun fromWorkCategory(category: WorkCategory): String {
        return category.name
    }

    @TypeConverter
    fun toWorkCategory(category: String): WorkCategory {
        return WorkCategory.valueOf(category)
    }

    @TypeConverter
    fun fromEstimateStatus(status: EstimateStatus): String {
        return status.name
    }

    @TypeConverter
    fun toEstimateStatus(status: String): EstimateStatus {
        return EstimateStatus.valueOf(status)
    }

    @TypeConverter
    fun fromReportType(type: ReportType): String {
        return type.name
    }

    @TypeConverter
    fun toReportType(type: String): ReportType {
        return ReportType.valueOf(type)
    }

    @TypeConverter
    fun fromReportStatus(status: ReportStatus): String {
        return status.name
    }

    @TypeConverter
    fun toReportStatus(status: String): ReportStatus {
        return ReportStatus.valueOf(status)
    }

    @TypeConverter
    fun fromIssueCategory(category: IssueCategory): String {
        return category.name
    }

    @TypeConverter
    fun toIssueCategory(category: String): IssueCategory {
        return IssueCategory.valueOf(category)
    }

    @TypeConverter
    fun fromIssuePriority(priority: IssuePriority): String {
        return priority.name
    }

    @TypeConverter
    fun toIssuePriority(priority: String): IssuePriority {
        return IssuePriority.valueOf(priority)
    }

    @TypeConverter
    fun fromIssueStatus(status: IssueStatus): String {
        return status.name
    }

    @TypeConverter
    fun toIssueStatus(status: String): IssueStatus {
        return IssueStatus.valueOf(status)
    }

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String {
        return status.name
    }

    @TypeConverter
    fun toOrderStatus(status: String): OrderStatus {
        return OrderStatus.valueOf(status)
    }

    @TypeConverter
    fun fromTimeEntryStatus(status: TimeEntryStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTimeEntryStatus(status: String): TimeEntryStatus {
        return TimeEntryStatus.valueOf(status)
    }
}