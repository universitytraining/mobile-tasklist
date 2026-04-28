package com.tasklist.app.database

import com.tasklist.app.auth.aesDecrypt
import com.tasklist.app.auth.aesEncrypt
import com.tasklist.app.auth.deriveKey
import com.tasklist.app.auth.generateSalt
import com.tasklist.app.auth.hashPassword
import com.tasklist.app.auth.verifyPassword

class AuthRepository(private val db: AppDatabase) {

    fun register(username: String, password: String): Boolean {
        val usernameHash = hashPassword(username, "username-salt")
        val existing = db.userQueries.getUserByUsernameHash(usernameHash).executeAsOneOrNull()
        if (existing != null) return false

        val salt = generateSalt()
        val passwordHash = hashPassword(password, salt)
        val key = deriveKey(password, salt)
        val usernameEncrypted = aesEncrypt(username, key)

        db.userQueries.insertUser(usernameHash, usernameEncrypted, passwordHash, salt)
        return true
    }

    fun login(username: String, password: String): Pair<Long, ByteArray>? {
        val usernameHash = hashPassword(username, "username-salt")
        val user = db.userQueries.getUserByUsernameHash(usernameHash).executeAsOneOrNull()
            ?: return null

        if (!verifyPassword(password, user.salt, user.passwordHash)) return null

        val key = deriveKey(password, user.salt)
        return Pair(user.id, key)
    }

    fun getDecryptedUsername(userId: Long, key: ByteArray): String? {
        val user = db.userQueries.getUserById(userId).executeAsOneOrNull() ?: return null
        return aesDecrypt(user.usernameEncrypted, key)
    }

    fun updateUsername(userId: Long, newUsername: String, key: ByteArray): Boolean {
        val newHash = hashPassword(newUsername, "username-salt")
        val existing = db.userQueries.getUserByUsernameHash(newHash).executeAsOneOrNull()
        if (existing != null) return false
        val newEncrypted = aesEncrypt(newUsername, key)
        db.userQueries.updateUsernameById(newHash, newEncrypted, userId)
        return true
    }

    fun updatePassword(userId: Long, oldPassword: String, newPassword: String): Pair<Boolean, ByteArray?> {
        val user = db.userQueries.getUserById(userId).executeAsOneOrNull() ?: return Pair(false, null)
        if (!verifyPassword(oldPassword, user.salt, user.passwordHash)) return Pair(false, null)
        val newSalt = generateSalt()
        val newHash = hashPassword(newPassword, newSalt)
        db.userQueries.updatePasswordById(newHash, newSalt, userId)
        val newKey = deriveKey(newPassword, newSalt)
        return Pair(true, newKey)
    }

    fun deleteUser(userId: Long) {
        db.userQueries.deleteUserById(userId)
    }
}