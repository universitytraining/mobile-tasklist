package com.tasklist.app

import androidx.compose.ui.window.ComposeUIViewController
import com.tasklist.app.auth.BiometricAuth
import com.tasklist.app.database.Database
import com.tasklist.app.database.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController {
    val database = Database(DatabaseDriverFactory())
    val biometricAuth = BiometricAuth()
    App(
        database = database,
        biometricAuth = biometricAuth
    )
}