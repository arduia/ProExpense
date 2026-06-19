package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.storage.InMemoryDataStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryPinAuthRepository(
    private val store: InMemoryDataStore,
) : PinAuthRepository, SecurityStateReader {
    private val mutex = Mutex()
    private var pinConfigured = false
    private var storedPin: String? = null
    private var biometricEnrolled = false

    override suspend fun isPinConfigured(): Result<Boolean> = mutex.withLock {
        Result.Success(pinConfigured)
    }

    override suspend fun setPin(pin: String): Result<Unit> = mutex.withLock {
        if (pin.length != 6 || pin.any { !it.isDigit() }) {
            return Result.Error("PIN must be 6 digits")
        }
        storedPin = pin
        pinConfigured = true
        Result.Success(Unit)
    }

    override suspend fun verifyPin(pin: String): Result<Boolean> = mutex.withLock {
        Result.Success(storedPin == pin)
    }

    override suspend fun clearPin(): Result<Unit> = mutex.withLock {
        storedPin = null
        pinConfigured = false
        biometricEnrolled = false
        store.securityAnswerNormalized = null
        Result.Success(Unit)
    }

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

    override suspend fun hasPinConfigured(): Boolean = mutex.withLock { pinConfigured }
}
