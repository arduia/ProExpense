package com.arduia.expense.feature.auth

interface PinKeyDeriver {
    fun deriveKey(pin: String, salt: ByteArray): ByteArray

    fun generateSalt(length: Int = SALT_LENGTH): ByteArray

    companion object {
        const val SALT_LENGTH = 16
        const val KEY_LENGTH_BITS = 256
        const val PBKDF2_ITERATIONS = 100_000
    }
}
