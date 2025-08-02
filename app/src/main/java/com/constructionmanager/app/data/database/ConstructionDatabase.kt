package com.constructionmanager.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.constructionmanager.app.data.entities.*
import com.constructionmanager.app.data.dao.*
import com.constructionmanager.app.data.converters.Converters

@Database(
    entities = [
        User::class,
        Role::class,
        Project::class,
        Organization::class,
        WorkType::class,
        Estimate::class,
        EstimateItem::class,
        Report::class,
        Photo::class,
        Issue::class,
        MaterialOrder::class,
        TimeEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ConstructionDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun roleDao(): RoleDao
    abstract fun projectDao(): ProjectDao
    abstract fun organizationDao(): OrganizationDao
    abstract fun workTypeDao(): WorkTypeDao
    abstract fun estimateDao(): EstimateDao
    abstract fun estimateItemDao(): EstimateItemDao
    abstract fun reportDao(): ReportDao
    abstract fun photoDao(): PhotoDao
    abstract fun issueDao(): IssueDao
    abstract fun materialOrderDao(): MaterialOrderDao
    abstract fun timeEntryDao(): TimeEntryDao
    
    companion object {
        @Volatile
        private var INSTANCE: ConstructionDatabase? = null
        
        fun getDatabase(context: Context): ConstructionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConstructionDatabase::class.java,
                    "construction_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}