package com.tasklist.app.database

import com.tasklist.app.auth.generateSalt
import com.tasklist.app.auth.hashPassword
import com.tasklist.app.auth.verifyPassword
import com.tasklist.app.database.AppDatabase

class AuthRepository(private val db: AppDatabase) {

    fun register(username: String, password: String): Boolean {
        val existing = db.userQueries.getUserByUsername(username).executeAsOneOrNull()
        if (existing != null) return false

        val salt = generateSalt()
        val hash = hashPassword(password, salt)
        db.userQueries.insertUser(username, hash, salt)
        return true
    }

    fun login(username: String, password: String): Long? {
        val user = db.userQueries.getUserByUsername(username).executeAsOneOrNull()
            ?: return null

        return if (verifyPassword(password, user.salt, user.passwordHash)) {
            user.id
        } else {
            null
        }
    }
}