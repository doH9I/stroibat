package com.constructionmanager.app.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.entities.User
import java.util.Date

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY first_name, last_name")
    fun getAllActiveUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun getAllUsers(): Flow<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?
    
    @Query("SELECT * FROM users WHERE username = :username AND is_active = 1")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT * FROM users WHERE email = :email AND is_active = 1")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("SELECT * FROM users WHERE role_id = :roleId AND is_active = 1")
    fun getUsersByRole(roleId: Long): Flow<List<User>>
    
    @Insert
    suspend fun insertUser(user: User): Long
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("UPDATE users SET is_active = :isActive WHERE id = :id")
    suspend fun updateUserStatus(id: Long, isActive: Boolean)
    
    @Query("UPDATE users SET last_login = :lastLogin WHERE id = :id")
    suspend fun updateLastLogin(id: Long, lastLogin: Date)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("SELECT COUNT(*) FROM users WHERE is_active = 1")
    suspend fun getActiveUsersCount(): Int
    
    @Query("SELECT COUNT(*) FROM users WHERE role_id = :roleId AND is_active = 1")
    suspend fun getUsersCountByRole(roleId: Long): Int
}