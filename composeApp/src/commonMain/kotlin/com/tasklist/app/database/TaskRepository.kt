package com.tasklist.app.database

import com.tasklist.app.database.AppDatabase

class TaskRepository(private val db: AppDatabase) {

    fun addTask(userId: Long, title: String) {
        db.taskQueries.insertTask(
            userId = userId,
            title = title,
            createdAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }

    fun getTasksByUser(userId: Long) =
        db.taskQueries.getTasksByUser(userId).executeAsList()

    fun toggleTask(id: Long, isDone: Boolean) {
        db.taskQueries.toggleTask(if (isDone) 1L else 0L, id)
    }

    fun deleteTask(id: Long) {
        db.taskQueries.deleteTask(id)
    }
}