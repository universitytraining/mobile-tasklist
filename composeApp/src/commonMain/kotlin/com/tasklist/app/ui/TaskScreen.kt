package com.tasklist.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tasklist.app.database.DecryptedTask
import com.tasklist.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel,
    onLogout: () -> Unit
) {
    var tasks by remember { mutableStateOf(taskViewModel.getTasks()) }
    var editingTask by remember { mutableStateOf<DecryptedTask?>(null) }
    var isAddingTask by remember { mutableStateOf(false) }

    fun refresh() {
        tasks = taskViewModel.getTasks()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My Tasks") },
                    actions = {
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
                    Text("Add Note")
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onToggle = {
                                    taskViewModel.toggleTask(task.id, !task.isDone)
                                    refresh()
                                },
                                onDelete = {
                                    taskViewModel.deleteTask(task.id)
                                    refresh()
                                },
                                onEdit = { editingTask = task }
                            )
                        }
                    }
                }
            }
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
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        Button(onClick = { onConfirm(title, description) }) {
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