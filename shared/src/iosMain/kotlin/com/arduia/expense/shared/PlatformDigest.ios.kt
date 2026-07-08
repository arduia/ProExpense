package com.arduia.expense.shared

/**
 * iOS-readiness stub (compiles in the iOS klib gate but still throws). Implement with CommonCrypto
 * (CC_MD5 / CC_SHA256) when the iosApp phase starts.
 */
actual fun platformDigestHex(algorithm: String, input: String): String {
    TODO("iOS digest not implemented yet — use CommonCrypto in the iosApp phase")
}
