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
import com.tasklist.app.database.Task
import com.tasklist.app.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel,
    onLogout: () -> Unit
) {
    var tasks by remember { mutableStateOf(taskViewModel.getTasks()) }
    var newTaskTitle by remember { mutableStateOf("") }

    fun refresh() {
        tasks = taskViewModel.getTasks()
    }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("New task") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    taskViewModel.addTask(newTaskTitle)
                    newTaskTitle = ""
                    refresh()
                }) {
                    Text("Add")
                }
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
                                taskViewModel.toggleTask(task.id, task.isDone != 1L)
                                refresh()
                            },
                            onDelete = {
                                taskViewModel.deleteTask(task.id)
                                refresh()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isDone == 1L,
                onCheckedChange = { onToggle() }
            )
            Text(
                text = task.title,
                modifier = Modifier.weight(1f),
                textDecoration = if (task.isDone == 1L) TextDecoration.LineThrough else null
            )
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}