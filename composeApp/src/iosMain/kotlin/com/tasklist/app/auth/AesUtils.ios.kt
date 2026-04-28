package com.tasklist.app.auth

import kotlinx.cinterop.*
import platform.CommonCrypto.*
import platform.Foundation.*

actual fun aesEncrypt(data: String, key: ByteArray): String {
    val dataBytes = data.encodeToByteArray()
    val outputLength = dataBytes.size + kCCBlockSizeAES128
    val output = ByteArray(outputLength)
    var numBytesEncrypted = 0.convert<UInt>()

    val status = dataBytes.usePinned { dataPinned ->
        key.usePinned { keyPinned ->
            output.usePinned { outputPinned ->
                CCCrypt(
                    kCCEncrypt,
                    kCCAlgorithmAES,
                    kCCOptionPKCS7Padding or kCCOptionECBMode,
                    keyPinned.addressOf(0),
                    key.size.convert(),
                    null,
                    dataPinned.addressOf(0),
                    dataBytes.size.convert(),
                    outputPinned.addressOf(0),
                    outputLength.convert(),
                    numBytesEncrypted.ptr
                )
            }
        }
    }
    return output.take(numBytesEncrypted.toInt()).toByteArray().let {
        NSData.dataWithBytes(it.toCValues().ptr, it.size.convert())
            .base64EncodedStringWithOptions(0u)
    }
}

actual fun aesDecrypt(data: String, key: ByteArray): String {
    val inputData = NSData.alloc().initWithBase64EncodedString(data, 0u)
        ?: return ""
    val inputBytes = ByteArray(inputData.length.toInt()).apply {
        inputData.bytes?.let { ptr ->
            for (i in indices) this[i] = (ptr as CPointer<ByteVar>)[i]
        }
    }
    val outputLength = inputBytes.size + kCCBlockSizeAES128
    val output = ByteArray(outputLength)
    var numBytesDecrypted = 0.convert<UInt>()

    inputBytes.usePinned { inputPinned ->
        key.usePinned { keyPinned ->
            output.usePinned { outputPinned ->
                CCCrypt(
                    kCCDecrypt,
                    kCCAlgorithmAES,
                    kCCOptionPKCS7Padding or kCCOptionECBMode,
                    keyPinned.addressOf(0),
                    key.size.convert(),
                    null,
                    inputPinned.addressOf(0),
                    inputBytes.size.convert(),
                    outputPinned.addressOf(0),
                    outputLength.convert(),
                    numBytesDecrypted.ptr
                )
            }
        }
    }
    return output.take(numBytesDecrypted.toInt()).toByteArray().decodeToString()
}