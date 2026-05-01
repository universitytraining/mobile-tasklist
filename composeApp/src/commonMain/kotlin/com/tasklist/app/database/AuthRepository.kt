package com.tasklist.app.database

import com.tasklist.app.auth.aesDecrypt
import com.tasklist.app.auth.aesEncrypt
import com.tasklist.app.auth.deriveKey
import com.tasklist.app.auth.generateSalt
import com.tasklist.app.auth.hashPassword
import com.tasklist.app.auth.verifyPassword

class AuthRepository(private val db: AppDatabase) {

    fun register(username: String, password: String): Boolean {
        return try {
            val usernameHash = hashPassword(username, "username-salt")
            val existing = db.userQueries.getUserByUsernameHash(usernameHash).executeAsOneOrNull()
            if (existing != null) return false

            val salt = generateSalt()
            val passwordHash = hashPassword(password, salt)
            val key = deriveKey(password, salt)
            val usernameEncrypted = aesEncrypt(username, key)

            db.userQueries.insertUser(usernameHash, usernameEncrypted, passwordHash, salt)
            return true
        } catch (e: Exception) {
            false
        }
    }

    fun login(username: String, password: String): Pair<Long, ByteArray>? {
        return try {
            val usernameHash = hashPassword(username, "username-salt")
            val user = db.userQueries.getUserByUsernameHash(usernameHash).executeAsOneOrNull()
                ?: return null

            if (!verifyPassword(password, user.salt, user.passwordHash)) return null

            val key = deriveKey(password, user.salt)
            return Pair(user.id, key)
        } catch (e: Exception) {
            null
        }
    }

    fun getDecryptedUsername(userId: Long, key: ByteArray): String? {
        return try {
            val user = db.userQueries.getUserById(userId).executeAsOneOrNull() ?: return null
            return aesDecrypt(user.usernameEncrypted, key)
        } catch (e: Exception) {
            null
        }
    }

    fun updateUsername(userId: Long, newUsername: String, key: ByteArray): Boolean {
        return try {
            val newHash = hashPassword(newUsername, "username-salt")
            val existing = db.userQueries.getUserByUsernameHash(newHash).executeAsOneOrNull()
            if (existing != null) return false
            val newEncrypted = aesEncrypt(newUsername, key)
            db.userQueries.updateUsernameById(newHash, newEncrypted, userId)
            return true
        } catch (e: Exception) {
            false
        }
    }

    fun updatePassword(userId: Long, oldPassword: String, newPassword: String): Pair<Boolean, ByteArray?> {
        return try {
            val user = db.userQueries.getUserById(userId).executeAsOneOrNull() ?: return Pair(false, null)
            if (!verifyPassword(oldPassword, user.salt, user.passwordHash)) return Pair(false, null)
            val newSalt = generateSalt()
            val newHash = hashPassword(newPassword, newSalt)
            db.userQueries.updatePasswordById(newHash, newSalt, userId)
            val newKey = deriveKey(newPassword, newSalt)
            return Pair(true, newKey)
        } catch (e: Exception) {
            Pair(false, null)
        }
    }


    fun deleteUser(userId: Long) {
        db.userQueries.deleteUserById(userId)
    }
}