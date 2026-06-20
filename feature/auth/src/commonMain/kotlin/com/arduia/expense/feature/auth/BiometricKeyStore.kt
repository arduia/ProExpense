package com.arduia.expense.feature.auth

interface BiometricKeyStore {
    fun wrapKey(key: ByteArray): ByteArray

    fun unwrapKey(wrapped: ByteArray): ByteArray?
}
