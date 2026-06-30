package com.arduia.expense.shared

/**
 * Computes a lowercase-hex message digest using a platform crypto provider.
 *
 * Declared as an `expect` seam so the integrity layer stays in `commonMain` while the actual hashing
 * uses each platform's native provider (Android `MessageDigest`, later iOS CommonCrypto).
 *
 * @param algorithm provider algorithm name (e.g. "MD5", "SHA-256").
 */
expect fun platformDigestHex(algorithm: String, input: String): String
