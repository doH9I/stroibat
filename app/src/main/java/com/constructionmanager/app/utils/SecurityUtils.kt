package com.constructionmanager.app.utils

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

object SecurityUtils {
    
    private const val SALT_LENGTH = 32
    private const val HASH_ALGORITHM = "SHA-256"
    private const val ENCRYPTION_ALGORITHM = "AES"
    
    /**
     * Генерирует случайную соль
     */
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(SALT_LENGTH)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.DEFAULT)
    }
    
    /**
     * Хеширует пароль с солью
     */
    fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        val saltBytes = Base64.decode(salt, Base64.DEFAULT)
        digest.update(saltBytes)
        val hashedBytes = digest.digest(password.toByteArray())
        return Base64.encodeToString(hashedBytes, Base64.DEFAULT)
    }
    
    /**
     * Проверяет пароль
     */
    fun verifyPassword(password: String, salt: String, hashedPassword: String): Boolean {
        val hashedInput = hashPassword(password, salt)
        return hashedInput == hashedPassword
    }
    
    /**
     * Шифрует чувствительные данные
     */
    fun encrypt(data: String, key: String): String {
        val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
        val secretKey = SecretKeySpec(key.toByteArray().copyOf(16), ENCRYPTION_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }
    
    /**
     * Расшифровывает данные
     */
    fun decrypt(encryptedData: String, key: String): String {
        val cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM)
        val secretKey = SecretKeySpec(key.toByteArray().copyOf(16), ENCRYPTION_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
    
    /**
     * Генерирует токен сессии
     */
    fun generateSessionToken(): String {
        val random = SecureRandom()
        val tokenBytes = ByteArray(32)
        random.nextBytes(tokenBytes)
        return Base64.encodeToString(tokenBytes, Base64.NO_WRAP)
    }
    
    /**
     * Проверяет силу пароля
     */
    fun isPasswordStrong(password: String): Boolean {
        if (password.length < 8) return false
        
        var hasUpperCase = false
        var hasLowerCase = false
        var hasDigit = false
        var hasSpecialChar = false
        
        for (char in password) {
            when {
                char.isUpperCase() -> hasUpperCase = true
                char.isLowerCase() -> hasLowerCase = true
                char.isDigit() -> hasDigit = true
                !char.isLetterOrDigit() -> hasSpecialChar = true
            }
        }
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }
}