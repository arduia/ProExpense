package com.arduia.expense.shared

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CC_MD5
import platform.CoreCrypto.CC_MD5_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA256
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
actual fun platformDigestHex(algorithm: String, input: String): String {
    val message = input.encodeToByteArray()
    val digest = when (algorithm) {
        "MD5" -> ByteArray(CC_MD5_DIGEST_LENGTH).also { output ->
            message.usePinned { inPinned ->
                output.usePinned { outPinned ->
                    CC_MD5(inPinned.addressOf(0), message.size.convert(), outPinned.addressOf(0).reinterpret())
                }
            }
        }
        "SHA-256" -> ByteArray(CC_SHA256_DIGEST_LENGTH).also { output ->
            message.usePinned { inPinned ->
                output.usePinned { outPinned ->
                    CC_SHA256(inPinned.addressOf(0), message.size.convert(), outPinned.addressOf(0).reinterpret())
                }
            }
        }
        else -> error("Unsupported digest algorithm: $algorithm")
    }
    return digest.toHex()
}

private fun ByteArray.toHex(): String = joinToString("") { byte ->
    (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
}
