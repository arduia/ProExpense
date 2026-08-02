package com.arduia.expense.shared

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

actual fun platformSecureRandomBytes(size: Int): ByteArray = ByteArray(size).apply { SecureRandom().nextBytes(this) }

actual fun platformPbkdf2Sha256Hex(
    password: String,
    salt: ByteArray,
    iterations: Int,
    keyLengthBits: Int,
): String {
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits)
    val key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec)
    return key.encoded.toLowerHex()
}
