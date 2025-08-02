package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.EstimateItem

@Dao
interface EstimateItemDao {
    
    @Query("SELECT * FROM estimate_items WHERE estimate_id = :estimateId ORDER BY order_index")
    fun getItemsByEstimate(estimateId: Long): Flow<List<EstimateItem>>
    
    @Query("SELECT * FROM estimate_items WHERE id = :id")
    suspend fun getEstimateItemById(id: Long): EstimateItem?
    
    @Insert
    suspend fun insertEstimateItem(item: EstimateItem): Long
    
    @Insert
    suspend fun insertEstimateItems(items: List<EstimateItem>)
    
    @Update
    suspend fun updateEstimateItem(item: EstimateItem)
    
    @Delete
    suspend fun deleteEstimateItem(item: EstimateItem)
    
    @Query("DELETE FROM estimate_items WHERE estimate_id = :estimateId")
    suspend fun deleteItemsByEstimate(estimateId: Long)
    
    @Query("SELECT COUNT(*) FROM estimate_items WHERE estimate_id = :estimateId")
    suspend fun getItemsCountByEstimate(estimateId: Long): Int
}