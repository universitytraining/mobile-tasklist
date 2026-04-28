package com.tasklist.app.auth

expect fun aesEncrypt(data: String, key: ByteArray): String
expect fun aesDecrypt(data: String, key: ByteArray): String

fun deriveKey(password: String, salt: String): ByteArray {
    val input = (password + salt + "aes").encodeToByteArray()
    return sha256(input)
}