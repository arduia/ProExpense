package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.data.SecurityStateReader
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryPinAuthRepository : PinAuthRepository, SecurityStateReader {
    private val mutex = Mutex()
    private var pinConfigured = false
    private var storedPin: String? = null

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
        Result.Success(Unit)
    }

    override suspend fun hasPinConfigured(): Boolean = mutex.withLock {
        pinConfigured
    }
}
