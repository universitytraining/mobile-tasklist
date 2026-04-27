package com.tasklist.app.database

class Database(driverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(driverFactory.createDriver())

    val authRepository = AuthRepository(database)
    val taskRepository = TaskRepository(database)
}