package com.tasklist.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tasklist.app.database.AuthRepository
import com.tasklist.app.database.TaskRepository

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

    fun deleteAccount(password: String, taskRepository: TaskRepository): Boolean {
        val userId = currentUserId ?: return false
        val valid = authRepository.login(
            authRepository.getDecryptedUsername(userId, currentKey!!) ?: return false,
            password
        )
        if (valid == null) return false
        taskRepository.deleteTasksByUser(userId)
        authRepository.deleteUser(userId)
        logout()
        return true
    }
}