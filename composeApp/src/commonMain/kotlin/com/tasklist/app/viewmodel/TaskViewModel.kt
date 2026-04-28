package com.tasklist.app.viewmodel

import androidx.lifecycle.ViewModel
import com.tasklist.app.database.DecryptedTask
import com.tasklist.app.database.TaskRepository

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private var userId: Long = -1
    private var key: ByteArray = ByteArray(0)

    fun init(userId: Long, key: ByteArray) {
        this.userId = userId
        this.key = key
    }

    fun getTasks(): List<DecryptedTask> {
        return taskRepository.getTasksByUser(userId, key)
    }

    fun addTask(title: String, description: String = "") {
        if (title.isBlank()) return
        taskRepository.addTask(userId, title, description, key)
    }

    fun updateTask(id: Long, title: String, description: String) {
        taskRepository.updateTask(id, title, description, key)
    }

    fun toggleTask(id: Long, isDone: Boolean) {
        taskRepository.toggleTask(id, isDone)
    }

    fun deleteTask(id: Long) {
        taskRepository.deleteTask(id)
    }
}