package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.storage.InMemoryDataStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryPinAuthRepository(
    private val store: InMemoryDataStore,
    private val keyManager: DatabaseKeyManager,
) : PinAuthRepository, SecurityStateReader {
    private val mutex = Mutex()
    private var storedPin: String? = null
    private var biometricEnrolled = false

    override suspend fun isPinConfigured(): Result<Boolean> = mutex.withLock {
        Result.Success(storedPin != null && store.isPinProtected())
    }

    override suspend fun setPin(pin: String): Result<Unit> = mutex.withLock {
        if (pin.length != 6 || pin.any { !it.isDigit() }) {
            return Result.Error("PIN must be 6 digits")
        }
        when (val rotation = keyManager.rotateToPinDerivedKey(pin)) {
            is Result.Error -> return rotation
            is Result.Success -> {
                storedPin = pin
                Result.Success(Unit)
            }
        }
    }

    override suspend fun verifyPin(pin: String): Result<Boolean> = mutex.withLock {
        Result.Success(storedPin == pin && keyManager.verifyDerivedKey(pin))
    }

    override suspend fun clearPin(): Result<Unit> = mutex.withLock {
        when (val rotation = keyManager.rotateToRandomKey()) {
            is Result.Error -> return rotation
            is Result.Success -> {
                storedPin = null
                biometricEnrolled = false
                store.securityAnswerNormalized = null
                Result.Success(Unit)
            }
        }
    }

    override suspend fun changePin(newPin: String): Result<Unit> = setPin(newPin)

    override suspend fun setSecurityAnswer(answer: String): Result<Unit> = mutex.withLock {
        val normalized = answer.trim().lowercase()
        if (normalized.isBlank()) return Result.Error("Answer required")
        store.securityAnswerNormalized = normalized
        Result.Success(Unit)
    }

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = mutex.withLock {
        val expected = store.securityAnswerNormalized
        Result.Success(expected != null && expected == answer.trim().lowercase())
    }

    override suspend fun isBiometricEnrolled(): Boolean = mutex.withLock { biometricEnrolled }

    override suspend fun setBiometricEnrolled(enabled: Boolean): Result<Unit> = mutex.withLock {
        if (enabled && storedPin == null) {
            return Result.Error("PIN must be configured before enabling biometrics")
        }
        biometricEnrolled = enabled
        Result.Success(Unit)
    }

    override suspend fun hasPinConfigured(): Boolean = mutex.withLock {
        storedPin != null && store.isPinProtected()
    }
}
