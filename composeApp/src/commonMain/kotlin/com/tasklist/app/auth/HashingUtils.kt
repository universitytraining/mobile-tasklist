package com.tasklist.app.auth

expect fun sha256(input: ByteArray): ByteArray

fun generateSalt(): String {
    val bytes = ByteArray(16) { kotlin.random.Random.nextInt(256).toByte() }
    return bytes.toHexString()
}

fun hashPassword(password: String, salt: String): String {
    val input = (password + salt).encodeToByteArray()
    return sha256(input).toHexString()
}

fun verifyPassword(input: String, salt: String, storedHash: String): Boolean {
    return hashPassword(input, salt) == storedHash
}

fun ByteArray.toHexString(): String =
    joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }