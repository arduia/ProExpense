package com.arduia.expense.shared

private const val HEX_CHARS = "0123456789abcdef"

/** Lowercase-hex encoding, pure Kotlin so callers don't need a JVM-only `"%02x".format(...)`. */
fun ByteArray.toHexString(): String = buildString(size * 2) {
    for (byte in this@toHexString) {
        val value = byte.toInt() and 0xFF
        append(HEX_CHARS[value ushr 4])
        append(HEX_CHARS[value and 0x0F])
    }
}
