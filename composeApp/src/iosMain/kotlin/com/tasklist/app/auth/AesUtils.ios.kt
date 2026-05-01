package com.tasklist.app.auth

import kotlinx.cinterop.*
import platform.CoreCrypto.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
actual fun aesEncrypt(data: String, key: ByteArray): String {
    val dataBytes = data.encodeToByteArray()
    val blockSize = kCCBlockSizeAES128.toInt()
    val outputLength = dataBytes.size + blockSize
    val output = ByteArray(outputLength)
    val numBytesEncrypted = ULongArray(1)

    dataBytes.usePinned { dataPinned ->
        key.usePinned { keyPinned ->
            output.usePinned { outputPinned ->
                numBytesEncrypted.usePinned { countPinned ->
                    CCCrypt(
                        kCCEncrypt,
                        kCCAlgorithmAES,
                        (kCCOptionPKCS7Padding or kCCOptionECBMode).toUInt(),
                        keyPinned.addressOf(0),
                        key.size.toULong(),
                        null,
                        dataPinned.addressOf(0),
                        dataBytes.size.toULong(),
                        outputPinned.addressOf(0),
                        outputLength.toULong(),
                        countPinned.addressOf(0)
                    )
                }
            }
        }
    }

    val resultBytes = output.take(numBytesEncrypted[0].toInt()).toByteArray()
    return Base64.encode(resultBytes)
}

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
actual fun aesDecrypt(data: String, key: ByteArray): String {
    val inputBytes = Base64.decode(data)
    val inputLength = inputBytes.size
    val blockSize = kCCBlockSizeAES128.toInt()
    val outputLength = inputLength + blockSize
    val output = ByteArray(outputLength)
    val numBytesDecrypted = ULongArray(1)

    inputBytes.usePinned { inputPinned ->
        key.usePinned { keyPinned ->
            output.usePinned { outputPinned ->
                numBytesDecrypted.usePinned { countPinned ->
                    CCCrypt(
                        kCCDecrypt,
                        kCCAlgorithmAES,
                        (kCCOptionPKCS7Padding or kCCOptionECBMode).toUInt(),
                        keyPinned.addressOf(0),
                        key.size.toULong(),
                        null,
                        inputPinned.addressOf(0),
                        inputLength.toULong(),
                        outputPinned.addressOf(0),
                        outputLength.toULong(),
                        countPinned.addressOf(0)
                    )
                }
            }
        }
    }

    return output.take(numBytesDecrypted[0].toInt()).toByteArray().decodeToString()
}