package com.tasklist.app

import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import com.tasklist.app.database.Database
import com.tasklist.app.viewmodel.AuthViewModel
import com.tasklist.app.viewmodel.TaskViewModel
import com.tasklist.app.ui.LoginScreen
import com.tasklist.app.ui.TaskScreen

@Composable
fun App(database: Database) {
    val authViewModel = remember { AuthViewModel(database.authRepository) }
    val taskViewModel = remember { TaskViewModel(database.taskRepository) }

    var isLoggedIn by remember { mutableStateOf(false) }

    MaterialTheme {
        if (isLoggedIn) {
            TaskScreen(
                taskViewModel = taskViewModel,
                onLogout = {
                    authViewModel.logout()
                    isLoggedIn = false
                }
            )
        } else {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { userId ->
                    taskViewModel.init(userId)
                    isLoggedIn = true
                }
            )
        }
    }
}