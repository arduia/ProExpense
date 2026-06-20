package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.data.SecurityStateReader
import com.arduia.expense.storage.EntityDataStore
import com.arduia.expense.storage.KeyRotationStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PinAuthRepositoryImpl(
    private val store: EntityDataStore,
    private val keyStorage: KeyRotationStore,
    private val keyManager: DatabaseKeyManager,
    private val answerHasher: SecurityAnswerHasher,
    private val biometricKeyStore: BiometricKeyStore?,
) : PinAuthRepository, SecurityStateReader {
    private val mutex = Mutex()

    override suspend fun isPinConfigured(): Result<Boolean> = mutex.withLock {
        Result.Success(keyStorage.isPinProtected())
    }

    override suspend fun setPin(pin: String): Result<Unit> = mutex.withLock {
        if (pin.length != 6 || pin.any { !it.isDigit() }) {
            return Result.Error("PIN must be 6 digits")
        }
        when (val rotation = keyManager.rotateToPinDerivedKey(pin)) {
            is Result.Error -> rotation
            is Result.Success -> Result.Success(Unit)
        }
    }

    override suspend fun verifyPin(pin: String): Result<Boolean> = mutex.withLock {
        Result.Success(keyManager.verifyDerivedKey(pin))
    }

    override suspend fun clearPin(): Result<Unit> = mutex.withLock {
        when (val rotation = keyManager.rotateToRandomKey()) {
            is Result.Error -> return rotation
            is Result.Success -> {
                store.securityAnswerHash = null
                store.securityQuestionId = null
                store.biometricEnrolled = false
                store.setBiometricWrappedKey(null)
                store.persistSettings()
                Result.Success(Unit)
            }
        }
    }

    override suspend fun changePin(newPin: String): Result<Unit> = setPin(newPin)

    override suspend fun setSecurityAnswer(questionId: String, answer: String): Result<Unit> = mutex.withLock {
        if (SecurityQuestions.byId(questionId) == null) {
            return Result.Error("Invalid security question")
        }
        val normalized = answer.trim().lowercase()
        if (normalized.isBlank()) return Result.Error("Answer required")
        store.securityQuestionId = questionId
        store.securityAnswerHash = answerHasher.hash(normalized)
        store.persistSettings()
        Result.Success(Unit)
    }

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = mutex.withLock {
        val expected = store.securityAnswerHash
        val normalized = answer.trim().lowercase()
        Result.Success(expected != null && expected == answerHasher.hash(normalized))
    }

    override suspend fun isBiometricEnrolled(): Boolean = mutex.withLock { store.biometricEnrolled }

    override suspend fun setBiometricEnrolled(enabled: Boolean): Result<Unit> = mutex.withLock {
        if (enabled && !keyStorage.isPinProtected()) {
            return Result.Error("PIN must be configured before enabling biometrics")
        }
        if (enabled) {
            val activeKey = keyStorage.getActiveKey()
            val keyStore = biometricKeyStore
            if (activeKey == null || keyStore == null) {
                return Result.Error("Biometric enrollment unavailable")
            }
            val wrapped = keyStore.wrapKey(activeKey)
            store.setBiometricWrappedKey(wrapped)
            store.biometricEnrolled = true
            store.persistSettings()
        } else {
            store.biometricEnrolled = false
            store.setBiometricWrappedKey(null)
            store.persistSettings()
        }
        Result.Success(Unit)
    }

    override suspend fun unlockWithBiometric(): Result<Boolean> = mutex.withLock {
        if (!store.biometricEnrolled) return Result.Success(false)
        val wrapped = store.getBiometricWrappedKey()
        val keyStore = biometricKeyStore
        val activeKey = keyStorage.getActiveKey()
        if (wrapped == null || keyStore == null || activeKey == null) {
            return Result.Success(false)
        }
        val unwrapped = keyStore.unwrapKey(wrapped)
        Result.Success(unwrapped != null && unwrapped.contentEquals(activeKey))
    }

    override suspend fun getSecurityQuestionId(): String? = mutex.withLock { store.securityQuestionId }

    override suspend fun hasPinConfigured(): Boolean = mutex.withLock {
        keyStorage.isPinProtected()
    }
}
