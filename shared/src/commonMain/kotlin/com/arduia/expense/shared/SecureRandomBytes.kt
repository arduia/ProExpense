package com.arduia.expense.shared

/**
 * Cryptographically secure random bytes (e.g. PBKDF2 salts) using a platform CSPRNG.
 *
 * `kotlin.random.Random` is multiplatform but not a CSPRNG, so this needs its own seam rather than
 * reusing it — same rationale as [platformDigestHex]/[platformPbkdf2Sha256].
 */
expect fun secureRandomBytes(size: Int): ByteArray
