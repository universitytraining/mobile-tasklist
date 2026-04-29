package com.tasklist.app.auth

import android.content.Context
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class BiometricAuth(
    private val context: Context,
    private val activity: FragmentActivity
) {
    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "biometric_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    actual fun isAvailable(): Boolean {
        val manager = BiometricManager.from(context)
        return manager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    actual fun hasStoredSession(): Boolean {
        return prefs.contains("user_id") && prefs.contains("aes_key")
    }

    actual fun storeSession(userId: Long, key: ByteArray) {
        prefs.edit()
            .putLong("user_id", userId)
            .putString("aes_key", Base64.encodeToString(key, Base64.NO_WRAP))
            .apply()
    }

    actual fun clearSession() {
        prefs.edit().clear().apply()
    }

    actual fun authenticate(
        onSuccess: (Long, ByteArray) -> Unit,
        onFailure: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val userId = prefs.getLong("user_id", -1L)
                val keyString = prefs.getString("aes_key", null)
                if (userId == -1L || keyString == null) {
                    onFailure()
                    return
                }
                val key = Base64.decode(keyString, Base64.NO_WRAP)
                onSuccess(userId, key)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onFailure()
            }

            override fun onAuthenticationFailed() {
                onFailure()
            }
        }

        val prompt = BiometricPrompt(activity, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Tasklist")
            .setSubtitle("Use your fingerprint to continue")
            .setNegativeButtonText("Use password instead")
            .build()

        prompt.authenticate(promptInfo)
    }
}