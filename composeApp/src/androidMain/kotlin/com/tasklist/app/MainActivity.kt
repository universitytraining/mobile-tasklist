package com.tasklist.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tasklist.app.database.Database
import com.tasklist.app.database.DatabaseDriverFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val database = Database(DatabaseDriverFactory(this))

        setContent {
            App(database = database)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // preview doesn't need a real DB
}