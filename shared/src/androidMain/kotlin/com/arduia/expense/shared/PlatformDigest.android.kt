package com.arduia.expense.shared

import java.security.MessageDigest

actual fun platformDigestHex(algorithm: String, input: String): String {
    val bytes = MessageDigest.getInstance(algorithm).digest(input.encodeToByteArray())
    return buildString(bytes.size * 2) {
        bytes.forEach { byte ->
            val value = byte.toInt() and 0xFF
            append(HEX[value ushr 4])
            append(HEX[value and 0x0F])
        }
    }
}

private const val HEX = "0123456789abcdef"
