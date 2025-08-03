package com.construction.management.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.construction.management.data.model.*
import com.construction.management.data.database.dao.*
import com.construction.management.data.database.converter.*

@Database(
    entities = [
        User::class,
        Project::class,
        Estimate::class,
        EstimateItem::class,
        WorkCategory::class,
        Organization::class,
        Report::class,
        Photo::class,
        Defect::class,
        Material::class,
        TimeSheet::class,
        Notification::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    LocalDateTimeConverter::class,
    UserRoleConverter::class,
    ProjectStatusConverter::class,
    EstimateStatusConverter::class,
    OrganizationTypeConverter::class,
    WorkCategoryTypeConverter::class,
    PermissionConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun estimateDao(): EstimateDao
    abstract fun estimateItemDao(): EstimateItemDao
    abstract fun workCategoryDao(): WorkCategoryDao
    abstract fun organizationDao(): OrganizationDao
    abstract fun reportDao(): ReportDao
    abstract fun photoDao(): PhotoDao
    abstract fun defectDao(): DefectDao
    abstract fun materialDao(): MaterialDao
    abstract fun timeSheetDao(): TimeSheetDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "construction_management.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}