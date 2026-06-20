package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.storage.DatabaseRekeyer
import com.arduia.expense.storage.KeyRotationStore
import kotlin.random.Random

class DatabaseKeyManager(
    private val store: KeyRotationStore,
    private val keyDeriver: PinKeyDeriver,
    private val rekeyer: DatabaseRekeyer? = null,
) {
    suspend fun rotateToPinDerivedKey(pin: String): Result<Unit> {
        if (pin.length != 6 || pin.any { !it.isDigit() }) {
            return Result.Error("PIN must be 6 digits")
        }
        val salt = keyDeriver.generateSalt()
        val derived = keyDeriver.deriveKey(pin, salt)
        rekeyer?.rekey(derived)
        store.setPinDerivedKey(derived, salt)
        return Result.Success(Unit)
    }

    suspend fun rotateToRandomKey(): Result<Unit> {
        val randomKey = ByteArray(32)
        Random.nextBytes(randomKey)
        rekeyer?.rekey(randomKey)
        store.setRandomKey(randomKey)
        return Result.Success(Unit)
    }

    suspend fun isPinProtected(): Boolean = store.isPinProtected()

    suspend fun verifyDerivedKey(pin: String): Boolean {
        val salt = store.getPinSalt() ?: return false
        val expected = store.getActiveKey() ?: return false
        val derived = keyDeriver.deriveKey(pin, salt)
        return derived.contentEquals(expected)
    }
}
