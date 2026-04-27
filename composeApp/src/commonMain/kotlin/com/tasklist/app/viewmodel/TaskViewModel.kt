package com.tasklist.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tasklist.app.database.Task
import com.tasklist.app.database.TaskRepository

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private var userId: Long = -1

    fun init(userId: Long) {
        this.userId = userId
    }

    fun getTasks(): List<Task> {
        return taskRepository.getTasksByUser(userId)
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        taskRepository.addTask(userId, title)
    }

    fun toggleTask(id: Long, isDone: Boolean) {
        taskRepository.toggleTask(id, isDone)
    }

    fun deleteTask(id: Long) {
        taskRepository.deleteTask(id)
    }
}