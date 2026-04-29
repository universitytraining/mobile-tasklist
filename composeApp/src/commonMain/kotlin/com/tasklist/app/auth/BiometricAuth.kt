package com.tasklist.app.auth

expect class BiometricAuth {
    fun isAvailable(): Boolean
    fun hasStoredSession(): Boolean
    fun storeSession(userId: Long, key: ByteArray)
    fun clearSession()
    fun authenticate(onSuccess: (Long, ByteArray) -> Unit, onFailure: () -> Unit)
}