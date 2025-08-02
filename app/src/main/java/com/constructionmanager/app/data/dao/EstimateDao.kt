package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.Estimate
import com.constructionmanager.app.data.entities.EstimateStatus
import java.math.BigDecimal

@Dao
interface EstimateDao {
    
    @Query("SELECT * FROM estimates ORDER BY created_at DESC")
    fun getAllEstimates(): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE id = :id")
    suspend fun getEstimateById(id: Long): Estimate?
    
    @Query("SELECT * FROM estimates WHERE project_id = :projectId ORDER BY version DESC")
    fun getEstimatesByProject(projectId: Long): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE status = :status ORDER BY created_at DESC")
    fun getEstimatesByStatus(status: EstimateStatus): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE created_by = :userId ORDER BY created_at DESC")
    fun getEstimatesByUser(userId: Long): Flow<List<Estimate>>
    
    @Insert
    suspend fun insertEstimate(estimate: Estimate): Long
    
    @Update
    suspend fun updateEstimate(estimate: Estimate)
    
    @Delete
    suspend fun deleteEstimate(estimate: Estimate)
    
    @Query("SELECT COUNT(*) FROM estimates")
    suspend fun getEstimatesCount(): Int
    
    @Query("SELECT COUNT(*) FROM estimates WHERE status = :status")
    suspend fun getEstimatesCountByStatus(status: EstimateStatus): Int
    
    @Query("SELECT SUM(total_with_vat) FROM estimates WHERE status = 'APPROVED'")
    suspend fun getTotalApprovedAmount(): BigDecimal?
    
    @Query("SELECT MAX(version) FROM estimates WHERE project_id = :projectId")
    suspend fun getLatestVersionForProject(projectId: Long): Int?
}