package com.constructionmanager.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.constructionmanager.app.data.dao.UserDao
import com.constructionmanager.app.data.dao.RoleDao
import com.constructionmanager.app.data.entities.User
import com.constructionmanager.app.data.entities.Role
import com.constructionmanager.app.utils.SecurityUtils
import com.constructionmanager.app.data.preferences.UserPreferences
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val roleDao: RoleDao,
    private val userPreferences: UserPreferences
) {
    
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByUsername(username)
            if (user != null && user.isActive) {
                if (SecurityUtils.verifyPassword(password, user.salt, user.passwordHash)) {
                    userDao.updateLastLogin(user.id, Date())
                    userPreferences.saveCurrentUser(user)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Неверный пароль"))
                }
            } else {
                Result.failure(Exception("Пользователь не найден или деактивирован"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(
        username: String,
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?,
        roleId: Long
    ): Result<User> {
        return try {
            // Проверяем уникальность username и email
            val existingUserByUsername = userDao.getUserByUsername(username)
            if (existingUserByUsername != null) {
                return Result.failure(Exception("Пользователь с таким именем уже существует"))
            }
            
            val existingUserByEmail = userDao.getUserByEmail(email)
            if (existingUserByEmail != null) {
                return Result.failure(Exception("Пользователь с таким email уже существует"))
            }
            
            // Проверяем силу пароля
            if (!SecurityUtils.isPasswordStrong(password)) {
                return Result.failure(Exception("Пароль должен содержать минимум 8 символов, включая заглавные и строчные буквы, цифры и специальные символы"))
            }
            
            // Создаем пользователя
            val salt = SecurityUtils.generateSalt()
            val hashedPassword = SecurityUtils.hashPassword(password, salt)
            
            val user = User(
                username = username,
                email = email,
                passwordHash = hashedPassword,
                salt = salt,
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                roleId = roleId
            )
            
            val userId = userDao.insertUser(user)
            val createdUser = user.copy(id = userId)
            
            Result.success(createdUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        userPreferences.clearCurrentUser()
    }
    
    suspend fun getCurrentUser(): User? {
        return userPreferences.getCurrentUser()
    }
    
    fun isLoggedIn(): Flow<Boolean> = flow {
        emit(userPreferences.getCurrentUser() != null)
    }
    
    suspend fun changePassword(userId: Long, oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = userDao.getUserById(userId)
            if (user == null) {
                return Result.failure(Exception("Пользователь не найден"))
            }
            
            if (!SecurityUtils.verifyPassword(oldPassword, user.salt, user.passwordHash)) {
                return Result.failure(Exception("Неверный текущий пароль"))
            }
            
            if (!SecurityUtils.isPasswordStrong(newPassword)) {
                return Result.failure(Exception("Новый пароль не соответствует требованиям безопасности"))
            }
            
            val newSalt = SecurityUtils.generateSalt()
            val newHashedPassword = SecurityUtils.hashPassword(newPassword, newSalt)
            
            val updatedUser = user.copy(
                passwordHash = newHashedPassword,
                salt = newSalt,
                updatedAt = Date()
            )
            
            userDao.updateUser(updatedUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserRole(userId: Long): Role? {
        val user = userDao.getUserById(userId)
        return user?.let { roleDao.getRoleById(it.roleId) }
    }
    
    suspend fun hasPermission(userId: Long, permission: String): Boolean {
        val role = getUserRole(userId)
        return role?.let { 
            val permissions = parsePermissions(it.permissions)
            permissions.contains(permission) || permissions.contains("*")
        } ?: false
    }
    
    private fun parsePermissions(permissionsJson: String): List<String> {
        // Простой парсинг JSON строки с разрешениями
        return try {
            permissionsJson.removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
        } catch (e: Exception) {
            emptyList()
        }
    }
}