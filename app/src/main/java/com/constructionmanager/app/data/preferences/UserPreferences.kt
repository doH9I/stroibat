package com.constructionmanager.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.constructionmanager.app.data.entities.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "construction_manager_prefs"
        private const val KEY_CURRENT_USER = "current_user"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_AUTO_SYNC = "auto_sync"
        private const val KEY_LAST_SYNC = "last_sync"
    }
    
    fun saveCurrentUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit()
            .putString(KEY_CURRENT_USER, userJson)
            .apply()
    }
    
    fun getCurrentUser(): User? {
        val userJson = sharedPreferences.getString(KEY_CURRENT_USER, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }
    
    fun clearCurrentUser() {
        sharedPreferences.edit()
            .remove(KEY_CURRENT_USER)
            .apply()
    }
    
    fun setThemeMode(mode: String) {
        sharedPreferences.edit()
            .putString(KEY_THEME_MODE, mode)
            .apply()
    }
    
    fun getThemeMode(): String {
        return sharedPreferences.getString(KEY_THEME_MODE, "system") ?: "system"
    }
    
    fun setLanguage(language: String) {
        sharedPreferences.edit()
            .putString(KEY_LANGUAGE, language)
            .apply()
    }
    
    fun getLanguage(): String {
        return sharedPreferences.getString(KEY_LANGUAGE, "ru") ?: "ru"
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled)
            .apply()
    }
    
    fun isNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }
    
    fun setAutoSyncEnabled(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_AUTO_SYNC, enabled)
            .apply()
    }
    
    fun isAutoSyncEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_AUTO_SYNC, true)
    }
    
    fun setLastSyncTime(timestamp: Long) {
        sharedPreferences.edit()
            .putLong(KEY_LAST_SYNC, timestamp)
            .apply()
    }
    
    fun getLastSyncTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_SYNC, 0)
    }
}