package com.arduia.expense.shared

/**
 * Cryptographically secure random bytes from the platform CSPRNG (Android `SecureRandom`, iOS
 * `SecRandomCopyBytes`). Declared as an `expect` seam so PIN salting stays in `commonMain`.
 */
expect fun platformSecureRandomBytes(size: Int): ByteArray

/**
 * PBKDF2-HMAC-SHA256 key derivation, returned as lowercase hex.
 *
 * The seam exists because neither platform's KDF is portable — Android uses
 * `SecretKeyFactory("PBKDF2WithHmacSHA256")`, iOS uses CommonCrypto's `CCKeyDerivationPBKDF` — while
 * the PIN hash format and verification logic they feed must stay identical across both.
 */
expect fun platformPbkdf2Sha256Hex(
    password: String,
    salt: ByteArray,
    iterations: Int,
    keyLengthBits: Int,
): String

/** Shared by both actuals and by the PIN hash encoding, so the wire format cannot drift. */
fun ByteArray.toLowerHex(): String {
    val bytes = this
    return buildString(bytes.size * 2) {
        // Explicit receiver: an unqualified `forEach` here binds to CharSequence.forEach on the
        // StringBuilder under construction, silently producing an empty string.
        bytes.forEach { byte ->
            val value = byte.toInt() and BYTE_MASK
            append(HEX_DIGITS[value ushr NIBBLE_BITS])
            append(HEX_DIGITS[value and NIBBLE_MASK])
        }
    }
}

/** Inverse of [toLowerHex]; returns null when the input is not valid lowercase hex. */
fun String.hexToByteArrayOrNull(): ByteArray? {
    val out = ByteArray(length / 2)
    var valid = length % 2 == 0
    var i = 0
    while (valid && i < out.size) {
        val high = HEX_DIGITS.indexOf(this[i * 2])
        val low = HEX_DIGITS.indexOf(this[i * 2 + 1])
        if (high < 0 || low < 0) {
            valid = false
        } else {
            out[i] = ((high shl NIBBLE_BITS) or low).toByte()
            i++
        }
    }
    return out.takeIf { valid }
}

private const val HEX_DIGITS = "0123456789abcdef"
private const val NIBBLE_BITS = 4
private const val NIBBLE_MASK = 0x0F
private const val BYTE_MASK = 0xFF
