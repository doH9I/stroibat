package com.construction.management.data.database.dao

import androidx.room.*
import com.construction.management.data.model.Estimate
import com.construction.management.data.model.EstimateStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface EstimateDao {
    
    @Query("SELECT * FROM estimates ORDER BY createdAt DESC")
    fun getAllEstimates(): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE id = :estimateId")
    suspend fun getEstimateById(estimateId: Long): Estimate?
    
    @Query("SELECT * FROM estimates WHERE projectId = :projectId")
    fun getEstimatesByProject(projectId: Long): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE status = :status")
    fun getEstimatesByStatus(status: EstimateStatus): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE createdBy = :userId")
    fun getEstimatesByCreator(userId: Long): Flow<List<Estimate>>
    
    @Query("SELECT * FROM estimates WHERE approvedBy = :userId")
    fun getEstimatesByApprover(userId: Long): Flow<List<Estimate>>
    
    @Query("SELECT COUNT(*) FROM estimates")
    suspend fun getEstimatesCount(): Int
    
    @Query("SELECT COUNT(*) FROM estimates WHERE status = :status")
    suspend fun getEstimatesCountByStatus(status: EstimateStatus): Int
    
    @Query("SELECT SUM(totalAmount) FROM estimates WHERE status = :status")
    suspend fun getTotalAmountByStatus(status: EstimateStatus): Double?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimate(estimate: Estimate): Long
    
    @Update
    suspend fun updateEstimate(estimate: Estimate)
    
    @Query("UPDATE estimates SET status = :status WHERE id = :estimateId")
    suspend fun updateEstimateStatus(estimateId: Long, status: EstimateStatus)
    
    @Query("UPDATE estimates SET approvedBy = :approvedBy, approvedAt = :approvedAt WHERE id = :estimateId")
    suspend fun approveEstimate(estimateId: Long, approvedBy: Long, approvedAt: LocalDateTime)
    
    @Delete
    suspend fun deleteEstimate(estimate: Estimate)
    
    @Query("DELETE FROM estimates WHERE id = :estimateId")
    suspend fun deleteEstimateById(estimateId: Long)
}