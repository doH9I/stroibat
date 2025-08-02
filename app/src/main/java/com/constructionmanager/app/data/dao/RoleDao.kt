package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.Role

@Dao
interface RoleDao {
    
    @Query("SELECT * FROM roles ORDER BY display_name")
    fun getAllRoles(): Flow<List<Role>>
    
    @Query("SELECT * FROM roles WHERE id = :id")
    suspend fun getRoleById(id: Long): Role?
    
    @Query("SELECT * FROM roles WHERE name = :name")
    suspend fun getRoleByName(name: String): Role?
    
    @Insert
    suspend fun insertRole(role: Role): Long
    
    @Update
    suspend fun updateRole(role: Role)
    
    @Delete
    suspend fun deleteRole(role: Role)
    
    @Query("SELECT COUNT(*) FROM roles")
    suspend fun getRolesCount(): Int
}