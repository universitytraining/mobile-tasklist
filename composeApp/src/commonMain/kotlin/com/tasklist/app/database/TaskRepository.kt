package com.tasklist.app.database

import com.tasklist.app.auth.aesDecrypt
import com.tasklist.app.auth.aesEncrypt
//import kotlinx.datetime.Clock
import kotlin.time.Clock

class TaskRepository(private val db: AppDatabase) {

    fun addTask(userId: Long, title: String, description: String, key: ByteArray) {
        db.taskQueries.insertTask(
            userId = userId,
            titleEncrypted = aesEncrypt(title, key),
            descriptionEncrypted = aesEncrypt(description, key),
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
    }

    fun getTasksByUser(userId: Long, key: ByteArray): List<DecryptedTask> {
        return db.taskQueries.getTasksByUser(userId).executeAsList().map { task ->
            DecryptedTask(
                id = task.id,
                title = aesDecrypt(task.titleEncrypted, key),
                description = aesDecrypt(task.descriptionEncrypted, key),
                isDone = task.isDone == 1L
            )
        }
    }

    fun toggleTask(id: Long, isDone: Boolean) {
        db.taskQueries.toggleTask(if (isDone) 1L else 0L, id)
    }

    fun updateTask(id: Long, title: String, description: String, key: ByteArray) {
        db.taskQueries.updateTask(
            titleEncrypted = aesEncrypt(title, key),
            descriptionEncrypted = aesEncrypt(description, key),
            id = id
        )
    }

    fun deleteTask(id: Long) {
        db.taskQueries.deleteTask(id)
    }

    fun deleteTasksByUser(userId: Long) {
        db.taskQueries.deleteTasksByUser(userId)
    }
}

data class DecryptedTask(
    val id: Long,
    val title: String,
    val description: String,
    val isDone: Boolean
)