package com.arduia.expense.shared

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

actual fun platformPbkdf2Sha256(
    input: CharArray,
    salt: ByteArray,
    iterations: Int,
    keyLengthBits: Int,
): ByteArray {
    val spec = PBEKeySpec(input, salt, iterations, keyLengthBits)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
    return factory.generateSecret(spec).encoded
}
