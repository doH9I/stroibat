package com.construction.management.data.repository

import com.construction.management.data.database.dao.UserDao
import com.construction.management.data.model.User
import com.construction.management.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    
    fun getAllActiveUsers(): Flow<List<User>> = userDao.getAllActiveUsers()
    
    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)
    
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    
    fun getUsersByRole(role: UserRole): Flow<List<User>> = userDao.getUsersByRole(role)
    
    fun getUsersByOrganization(organizationId: Long): Flow<List<User>> = userDao.getUsersByOrganization(organizationId)
    
    suspend fun getActiveUsersCount(): Int = userDao.getActiveUsersCount()
    
    suspend fun getUsersCountByRole(role: UserRole): Int = userDao.getUsersCountByRole(role)
    
    fun getRecentlyActiveUsers(since: LocalDateTime): Flow<List<User>> = userDao.getRecentlyActiveUsers(since)
    
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun updateUserStatus(userId: Long, isActive: Boolean) = userDao.updateUserStatus(userId, isActive)
    
    suspend fun updateLastLogin(userId: Long, lastLoginAt: LocalDateTime) = userDao.updateLastLogin(userId, lastLoginAt)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    
    suspend fun deleteUserById(userId: Long) = userDao.deleteUserById(userId)
}