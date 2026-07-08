package com.arduia.expense.shared

import java.security.SecureRandom

actual fun secureRandomBytes(size: Int): ByteArray =
    ByteArray(size).also { SecureRandom().nextBytes(it) }
