package com.arduia.expense.shared

/**
 * Derives a key with PBKDF2-HMAC-SHA256 using a platform crypto provider.
 *
 * Declared as an `expect` seam (design §5.2's PBKDF2 choice — native on both Android
 * `javax.crypto` and iOS `CommonCrypto`) so PIN/security-answer hashing logic stays in commonMain.
 *
 * @param keyLengthBits derived key length in bits (e.g. 256).
 */
expect fun platformPbkdf2Sha256(
    input: CharArray,
    salt: ByteArray,
    iterations: Int,
    keyLengthBits: Int,
): ByteArray
