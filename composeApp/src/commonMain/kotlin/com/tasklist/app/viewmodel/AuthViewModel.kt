package com.tasklist.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tasklist.app.database.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var currentUserId: Long? = null
        private set

    fun register(username: String, password: String): Boolean {
        return authRepository.register(username, password)
    }

    fun login(username: String, password: String): Boolean {
        val userId = authRepository.login(username, password) ?: return false
        currentUserId = userId
        return true
    }

    fun logout() {
        currentUserId = null
    }

    fun isLoggedIn(): Boolean = currentUserId != null
}