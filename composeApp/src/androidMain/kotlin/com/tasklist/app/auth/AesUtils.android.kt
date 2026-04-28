package com.tasklist.app.auth

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

actual fun aesEncrypt(data: String, key: ByteArray): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"))
    return Base64.encodeToString(cipher.doFinal(data.encodeToByteArray()), Base64.NO_WRAP)
}

actual fun aesDecrypt(data: String, key: ByteArray): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"))
    return String(cipher.doFinal(Base64.decode(data, Base64.NO_WRAP)))
}