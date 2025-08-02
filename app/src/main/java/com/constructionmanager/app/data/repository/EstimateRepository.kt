package com.constructionmanager.app.data.repository

import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.dao.EstimateDao
import com.constructionmanager.app.data.entities.Estimate
import com.constructionmanager.app.data.entities.EstimateStatus
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EstimateRepository @Inject constructor(
    private val estimateDao: EstimateDao
) {
    
    fun getAllEstimates(): Flow<List<Estimate>> = estimateDao.getAllEstimates()
    
    suspend fun getEstimateById(id: Long): Estimate? = estimateDao.getEstimateById(id)
    
    fun getEstimatesByProject(projectId: Long): Flow<List<Estimate>> = 
        estimateDao.getEstimatesByProject(projectId)
    
    fun getEstimatesByStatus(status: EstimateStatus): Flow<List<Estimate>> = 
        estimateDao.getEstimatesByStatus(status)
    
    fun getEstimatesByUser(userId: Long): Flow<List<Estimate>> = 
        estimateDao.getEstimatesByUser(userId)
    
    suspend fun insertEstimate(estimate: Estimate): Long = estimateDao.insertEstimate(estimate)
    
    suspend fun updateEstimate(estimate: Estimate) = estimateDao.updateEstimate(estimate)
    
    suspend fun deleteEstimate(estimate: Estimate) = estimateDao.deleteEstimate(estimate)
    
    suspend fun getEstimatesCount(): Int = estimateDao.getEstimatesCount()
    
    suspend fun getEstimatesCountByStatus(status: EstimateStatus): Int = 
        estimateDao.getEstimatesCountByStatus(status)
    
    suspend fun getTotalApprovedAmount(): BigDecimal? = estimateDao.getTotalApprovedAmount()
    
    suspend fun getLatestVersionForProject(projectId: Long): Int? = 
        estimateDao.getLatestVersionForProject(projectId)
}