package com.arduia.expense.shared

import java.security.MessageDigest

actual fun platformDigestHex(algorithm: String, input: String): String =
    MessageDigest.getInstance(algorithm).digest(input.encodeToByteArray()).toHexString()
