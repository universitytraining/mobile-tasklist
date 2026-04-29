package com.tasklist.app

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tasklist.app.auth.BiometricAuth
import com.tasklist.app.database.Database
import com.tasklist.app.database.DatabaseDriverFactory

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val database = Database(DatabaseDriverFactory(this))
        val biometricAuth = BiometricAuth(this, this)

        setContent {
            App(
                database = database,
                biometricAuth = biometricAuth
            )
        }
    }
}