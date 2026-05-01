package com.tasklist.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tasklist.app.database.DecryptedTask
import com.tasklist.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel,
    onLogout: () -> Unit,
    onDeleteAccount: (String) -> Boolean
) {
    var tasks by remember { mutableStateOf(taskViewModel.getTasks()) }
    var editingTask by remember { mutableStateOf<DecryptedTask?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<DecryptedTask?>(null) }
    var showClearCompletedDialog by remember { mutableStateOf(false) }

    fun refresh() {
        tasks = taskViewModel.getTasks()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("TaskList") },
                    actions = {
                        TextButton(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete Account")
                        }
                        TextButton(onClick = onLogout) {
                            Text("Logout")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { isAddingTask = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Task")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (tasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No tasks yet. Add one above!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    if (tasks.any { it.isDone }) {
                        TextButton(
                            onClick = {
                                showClearCompletedDialog = true
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Clear Completed")
                        }
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onToggle = {
                                    taskViewModel.toggleTask(task.id, !task.isDone)
                                    refresh()
                                },
                                onDelete = { taskToDelete = task },
                                onEdit = { editingTask = task }
                            )
                        }
                    }
                }
            }
        }

        if (showClearCompletedDialog) {
            AlertDialog(
                onDismissRequest = { showClearCompletedDialog = false },
                title = { Text("Clear Completed") },
                text = { Text("Are you sure you want to delete all completed tasks? This cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            tasks.filter { it.isDone }.forEach { taskViewModel.deleteTask(it.id) }
                            showClearCompletedDialog = false
                            refresh()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Clear All")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearCompletedDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (taskToDelete != null) {
            AlertDialog(
                onDismissRequest = { taskToDelete = null },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete \"${taskToDelete!!.title}\"?") },
                confirmButton = {
                    Button(
                        onClick = {
                            taskViewModel.deleteTask(taskToDelete!!.id)
                            taskToDelete = null
                            refresh()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { taskToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (isAddingTask) {
            TaskDialog(
                task = null,
                onConfirm = { title, description ->
                    taskViewModel.addTask(title, description)
                    isAddingTask = false
                    refresh()
                },
                onDismiss = { isAddingTask = false }
            )
        }

        if (editingTask != null) {
            TaskDialog(
                task = editingTask,
                onConfirm = { title, description ->
                    taskViewModel.updateTask(editingTask!!.id, title, description)
                    editingTask = null
                    refresh()
                },
                onDismiss = { editingTask = null }
            )
        }

        if (showDeleteDialog) {
            DeleteAccountDialog(
                onConfirm = { password ->
                    val success = onDeleteAccount(password)
                    if (success) {
                        showDeleteDialog = false
                    }
                    success
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: DecryptedTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onEdit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = { onToggle() }
                )
                Text(
                    text = task.title,
                    modifier = Modifier.weight(1f),
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                )
                TextButton(onClick = onDelete) { Text("Delete") }
            }
            if (task.description.isNotBlank()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 48.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    task: DecryptedTask?,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(task?.title ?: "") }
    var description by remember { mutableStateOf(task?.description ?: "") }
    val isEditing = task != null
    var titleError by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "Edit Task" else "New Task",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (title.isBlank()) {
                                titleError = "Title cannot be empty"
                                return@Button
                            }
                            onConfirm(title, description)
                        }) {
                            Text("Save")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    minLines = 2
                )

                if (titleError.isNotEmpty()) {
                    Text(
                        text = titleError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f),
                    maxLines = Int.MAX_VALUE
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountDialog(
    onConfirm: (String) -> Boolean,
    onDismiss: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account") },
        text = {
            Column {
                Text(
                    text = "This will permanently delete your account and all associated tasks. This cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Enter password to confirm") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password.isBlank()) {
                        errorMessage = "Password cannot be empty"
                        return@Button
                    }
                    val success = onConfirm(password)
                    if (!success) {
                        errorMessage = "Incorrect password"
                        password = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Forever")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}