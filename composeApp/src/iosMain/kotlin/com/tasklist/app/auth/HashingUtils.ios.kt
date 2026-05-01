package com.tasklist.app.auth

import platform.CoreCrypto.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
actual fun sha256(input: ByteArray): ByteArray {
    val digest = ByteArray(CC_SHA256_DIGEST_LENGTH)
    val inputUBytes = input.toUByteArray()
    val digestUBytes = UByteArray(CC_SHA256_DIGEST_LENGTH)
    inputUBytes.usePinned { inputPinned ->
        digestUBytes.usePinned { digestPinned ->
            CC_SHA256(inputPinned.addressOf(0), input.size.toUInt(), digestPinned.addressOf(0))
        }
    }
    return digestUBytes.toByteArray()
}