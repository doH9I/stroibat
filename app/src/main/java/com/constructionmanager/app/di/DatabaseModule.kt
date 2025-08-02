package com.constructionmanager.app.di

import android.content.Context
import androidx.room.Room
import com.constructionmanager.app.data.database.ConstructionDatabase
import com.constructionmanager.app.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideConstructionDatabase(@ApplicationContext context: Context): ConstructionDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ConstructionDatabase::class.java,
            "construction_database"
        ).build()
    }
    
    @Provides
    fun provideUserDao(database: ConstructionDatabase): UserDao = database.userDao()
    
    @Provides
    fun provideRoleDao(database: ConstructionDatabase): RoleDao = database.roleDao()
    
    @Provides
    fun provideProjectDao(database: ConstructionDatabase): ProjectDao = database.projectDao()
    
    @Provides
    fun provideOrganizationDao(database: ConstructionDatabase): OrganizationDao = database.organizationDao()
    
    @Provides
    fun provideWorkTypeDao(database: ConstructionDatabase): WorkTypeDao = database.workTypeDao()
    
    @Provides
    fun provideEstimateDao(database: ConstructionDatabase): EstimateDao = database.estimateDao()
    
    @Provides
    fun provideEstimateItemDao(database: ConstructionDatabase): EstimateItemDao = database.estimateItemDao()
    
    @Provides
    fun provideReportDao(database: ConstructionDatabase): ReportDao = database.reportDao()
    
    @Provides
    fun providePhotoDao(database: ConstructionDatabase): PhotoDao = database.photoDao()
    
    @Provides
    fun provideIssueDao(database: ConstructionDatabase): IssueDao = database.issueDao()
    
    @Provides
    fun provideMaterialOrderDao(database: ConstructionDatabase): MaterialOrderDao = database.materialOrderDao()
    
    @Provides
    fun provideTimeEntryDao(database: ConstructionDatabase): TimeEntryDao = database.timeEntryDao()
}