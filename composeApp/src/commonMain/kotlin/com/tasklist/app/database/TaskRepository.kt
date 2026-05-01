package com.tasklist.app.database

import com.tasklist.app.auth.aesDecrypt
import com.tasklist.app.auth.aesEncrypt
import kotlin.time.Clock

class TaskRepository(private val db: AppDatabase) {

    fun addTask(userId: Long, title: String, description: String, key: ByteArray) {
        try {
            db.taskQueries.insertTask(
                userId = userId,
                titleEncrypted = aesEncrypt(title, key),
                descriptionEncrypted = aesEncrypt(description, key),
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
        } catch (e: Exception) { }
    }

    fun getTasksByUser(userId: Long, key: ByteArray): List<DecryptedTask> {
        return try {
            db.taskQueries.getTasksByUser(userId).executeAsList().map { task ->
                DecryptedTask(
                    id = task.id,
                    title = aesDecrypt(task.titleEncrypted, key),
                    description = aesDecrypt(task.descriptionEncrypted, key),
                    isDone = task.isDone == 1L
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun toggleTask(id: Long, isDone: Boolean) {
        try {
            db.taskQueries.toggleTask(if (isDone) 1L else 0L, id)
        } catch (e: Exception) { }
    }

    fun updateTask(id: Long, title: String, description: String, key: ByteArray) {
        try {
            db.taskQueries.updateTask(
                titleEncrypted = aesEncrypt(title, key),
                descriptionEncrypted = aesEncrypt(description, key),
                id = id
            )
        } catch (e: Exception) { }
    }

    fun deleteTask(id: Long) {
        try {
            db.taskQueries.deleteTask(id)
        } catch (e: Exception) { }
    }

    fun deleteTasksByUser(userId: Long) {
        try {
            db.taskQueries.deleteTasksByUser(userId)
        } catch (e: Exception) { }
    }
}

data class DecryptedTask(
    val id: Long,
    val title: String,
    val description: String,
    val isDone: Boolean
)