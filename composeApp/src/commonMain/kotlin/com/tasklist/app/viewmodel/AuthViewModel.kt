package com.tasklist.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tasklist.app.database.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var currentUserId: Long? = null
        private set

    var currentKey: ByteArray? = null
        private set

    fun register(username: String, password: String): Boolean {
        return authRepository.register(username, password)
    }

    fun login(username: String, password: String): Boolean {
        val result = authRepository.login(username, password) ?: return false
        currentUserId = result.first
        currentKey = result.second
        return true
    }

    fun logout() {
        currentUserId = null
        currentKey = null
    }

    fun isLoggedIn(): Boolean = currentUserId != null

    fun getDecryptedUsername(): String? {
        val userId = currentUserId ?: return null
        val key = currentKey ?: return null
        return authRepository.getDecryptedUsername(userId, key)
    }

    fun updateUsername(newUsername: String): Boolean {
        val userId = currentUserId ?: return false
        val key = currentKey ?: return false
        return authRepository.updateUsername(userId, newUsername, key)
    }

    fun updatePassword(oldPassword: String, newPassword: String): Boolean {
        val userId = currentUserId ?: return false
        val result = authRepository.updatePassword(userId, oldPassword, newPassword)
        if (result.first && result.second != null) {
            currentKey = result.second
        }
        return result.first
    }

    fun deleteAccount(taskRepository: com.tasklist.app.database.TaskRepository) {
        val userId = currentUserId ?: return
        taskRepository.deleteTasksByUser(userId)
        authRepository.deleteUser(userId)
        logout()
    }
}