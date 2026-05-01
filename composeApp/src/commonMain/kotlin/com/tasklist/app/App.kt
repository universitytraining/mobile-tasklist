package com.tasklist.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.tasklist.app.auth.BiometricAuth
import com.tasklist.app.database.Database
import com.tasklist.app.ui.LoginScreen
import com.tasklist.app.ui.TaskScreen
import com.tasklist.app.ui.TasklistTheme
import com.tasklist.app.viewmodel.AuthViewModel
import com.tasklist.app.viewmodel.TaskViewModel

@Composable
fun App(
    database: Database,
    biometricAuth: BiometricAuth
) {
    val authViewModel = remember { AuthViewModel(database.authRepository) }
    val taskViewModel = remember { TaskViewModel(database.taskRepository) }

    var isLoggedIn by remember { mutableStateOf(false) }

    TasklistTheme {
        if (isLoggedIn) {
            TaskScreen(
                taskViewModel = taskViewModel,
                onLogout = {
                    authViewModel.logout()
                    biometricAuth.clearSession()
                    isLoggedIn = false
                },
                onDeleteAccount = { password ->
                    authViewModel.deleteAccount(
                        password = password,
                        taskRepository = database.taskRepository
                    ).also { success ->
                        if (success) {
                            biometricAuth.clearSession()
                            isLoggedIn = false
                        }
                    }
                }
            )
        } else {
            LoginScreen(
                authViewModel = authViewModel,
                biometricAuth = biometricAuth,
                onLoginSuccess = { userId, key ->
                    biometricAuth.storeSession(userId, key)
                    taskViewModel.init(userId, key)
                    isLoggedIn = true
                }
            )
        }
    }
}