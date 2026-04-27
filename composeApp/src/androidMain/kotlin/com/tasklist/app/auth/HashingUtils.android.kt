package com.tasklist.app.auth

import java.security.MessageDigest

actual fun sha256(input: ByteArray): ByteArray =
    MessageDigest.getInstance("SHA-256").digest(input)