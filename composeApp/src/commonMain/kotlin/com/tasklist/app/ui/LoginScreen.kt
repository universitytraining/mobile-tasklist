package com.tasklist.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.tasklist.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (Long, ByteArray) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isRegistering) "Register" else "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "Username and password cannot be empty"
                    return@Button
                }
                if (isRegistering) {
                    val success = authViewModel.register(username, password)
                    if (success) {
                        val loggedIn = authViewModel.login(username, password)
                        if (loggedIn) {
                            onLoginSuccess(
                                authViewModel.currentUserId!!,
                                authViewModel.currentKey!!
                            )
                        }
                    } else {
                        errorMessage = "Username already taken"
                    }
                } else {
                    val success = authViewModel.login(username, password)
                    if (success) {
                        onLoginSuccess(
                            authViewModel.currentUserId!!,
                            authViewModel.currentKey!!
                        )
                    } else {
                        errorMessage = "Invalid username or password"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegistering) "Register" else "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            isRegistering = !isRegistering
            errorMessage = ""
        }) {
            Text(if (isRegistering) "Already have an account? Login" else "No account? Register")
        }
    }
}