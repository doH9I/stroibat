package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.MaterialOrder
import com.constructionmanager.app.data.entities.OrderStatus
import java.math.BigDecimal

@Dao
interface MaterialOrderDao {
    
    @Query("SELECT * FROM material_orders ORDER BY created_at DESC")
    fun getAllOrders(): Flow<List<MaterialOrder>>
    
    @Query("SELECT * FROM material_orders WHERE id = :id")
    suspend fun getOrderById(id: Long): MaterialOrder?
    
    @Query("SELECT * FROM material_orders WHERE project_id = :projectId ORDER BY created_at DESC")
    fun getOrdersByProject(projectId: Long): Flow<List<MaterialOrder>>
    
    @Query("SELECT * FROM material_orders WHERE supplier_id = :supplierId ORDER BY created_at DESC")
    fun getOrdersBySupplier(supplierId: Long): Flow<List<MaterialOrder>>
    
    @Query("SELECT * FROM material_orders WHERE status = :status ORDER BY created_at DESC")
    fun getOrdersByStatus(status: OrderStatus): Flow<List<MaterialOrder>>
    
    @Query("SELECT * FROM material_orders WHERE created_by = :userId ORDER BY created_at DESC")
    fun getOrdersByUser(userId: Long): Flow<List<MaterialOrder>>
    
    @Insert
    suspend fun insertOrder(order: MaterialOrder): Long
    
    @Update
    suspend fun updateOrder(order: MaterialOrder)
    
    @Delete
    suspend fun deleteOrder(order: MaterialOrder)
    
    @Query("SELECT COUNT(*) FROM material_orders WHERE status = :status")
    suspend fun getOrdersCountByStatus(status: OrderStatus): Int
    
    @Query("SELECT SUM(total_amount) FROM material_orders WHERE status = 'APPROVED'")
    suspend fun getTotalApprovedOrdersAmount(): BigDecimal?
}