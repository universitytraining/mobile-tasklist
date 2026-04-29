package com.tasklist.app.auth

actual class BiometricAuth {
    actual fun isAvailable(): Boolean = false
    actual fun hasStoredSession(): Boolean = false
    actual fun storeSession(userId: Long, key: ByteArray) {}
    actual fun clearSession() {}
    actual fun authenticate(onSuccess: (Long, ByteArray) -> Unit, onFailure: () -> Unit) {
        onFailure()
    }
}