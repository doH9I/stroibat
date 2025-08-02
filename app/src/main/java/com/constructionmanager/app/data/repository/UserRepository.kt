package com.constructionmanager.app.data.repository

import kotlinx.coroutines.flow.Flow
import com.constructionmanager.app.data.dao.UserDao
import com.constructionmanager.app.data.entities.User
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    
    fun getAllActiveUsers(): Flow<List<User>> = userDao.getAllActiveUsers()
    
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    suspend fun getUserById(id: Long): User? = userDao.getUserById(id)
    
    suspend fun getUserByUsername(username: String): User? = userDao.getUserByUsername(username)
    
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    
    fun getUsersByRole(roleId: Long): Flow<List<User>> = userDao.getUsersByRole(roleId)
    
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun updateUserStatus(id: Long, isActive: Boolean) = userDao.updateUserStatus(id, isActive)
    
    suspend fun updateLastLogin(id: Long, lastLogin: Date) = userDao.updateLastLogin(id, lastLogin)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    
    suspend fun getActiveUsersCount(): Int = userDao.getActiveUsersCount()
    
    suspend fun getUsersCountByRole(roleId: Long): Int = userDao.getUsersCountByRole(roleId)
}