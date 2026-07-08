package com.arduia.expense.shared

/**
 * iOS-readiness stub (not compiled into a shipping target yet). Implement with CommonCrypto's
 * `CCKeyDerivationPBKDF` (algorithm `kCCPBKDF2`, PRF `kCCPRFHmacAlgSHA256`) when the iosApp phase
 * starts.
 */
actual fun platformPbkdf2Sha256(
    input: CharArray,
    salt: ByteArray,
    iterations: Int,
    keyLengthBits: Int,
): ByteArray {
    TODO("iOS PBKDF2 not implemented yet — use CommonCrypto CCKeyDerivationPBKDF in the iosApp phase")
}
