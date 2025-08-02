package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.WorkType
import com.constructionmanager.app.data.entities.WorkCategory

@Dao
interface WorkTypeDao {
    
    @Query("SELECT * FROM work_types WHERE is_active = 1 ORDER BY category, name")
    fun getAllActiveWorkTypes(): Flow<List<WorkType>>
    
    @Query("SELECT * FROM work_types WHERE id = :id")
    suspend fun getWorkTypeById(id: Long): WorkType?
    
    @Query("SELECT * FROM work_types WHERE category = :category AND is_active = 1 ORDER BY name")
    fun getWorkTypesByCategory(category: WorkCategory): Flow<List<WorkType>>
    
    @Insert
    suspend fun insertWorkType(workType: WorkType): Long
    
    @Update
    suspend fun updateWorkType(workType: WorkType)
    
    @Delete
    suspend fun deleteWorkType(workType: WorkType)
    
    @Query("UPDATE work_types SET is_active = :isActive WHERE id = :id")
    suspend fun updateWorkTypeStatus(id: Long, isActive: Boolean)
    
    @Query("SELECT COUNT(*) FROM work_types WHERE is_active = 1")
    suspend fun getActiveWorkTypesCount(): Int
    
    @Query("SELECT * FROM work_types WHERE name LIKE '%' || :query || '%' AND is_active = 1")
    fun searchWorkTypes(query: String): Flow<List<WorkType>>
}