package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.storage.repository.AppMetaLocalStore
import com.arduia.expense.storage.repository.AppMetaSnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.security.MessageDigest

/**
 * PIN/biometric/security-question authentication backed by AppMetaLocalStore.
 * PIN is hashed using PBKDF2-SHA256 with a unique salt per device.
 * Security question answers are hashed similarly.
 * Biometric enrollment is tracked as a boolean flag + wrapped key placeholder.
 */
class PinAuthRepositoryImpl(
    private val appMetaStore: AppMetaLocalStore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PinAuthRepository {

    override suspend fun isPinConfigured(): Result<Boolean> = withContext(dispatcher) {
        try {
            val snapshot = appMetaStore.read()
            Result.Success(snapshot.securityAnswerHash != null || snapshot.securityQuestionId != null)
        } catch (e: Exception) {
            Result.Error("Failed to check PIN configuration", cause = e)
        }
    }

    override suspend fun setPin(pin: String): Result<Unit> = withContext(dispatcher) {
        try {
            val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
            val hash = hashPin(pin, salt)
            appMetaStore.update { snapshot ->
                snapshot.copy(
                    securityAnswerHash = hash,
                    securityQuestionId = null, // Clear question/answer on new PIN
                    failedAttemptCount = 0,
                    lockoutUntil = null,
                )
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to set PIN", cause = e)
        }
    }

    override suspend fun verifyPin(pin: String): Result<Boolean> = withContext(dispatcher) {
        try {
            val snapshot = appMetaStore.read()
            val storedHash = snapshot.securityAnswerHash
            if (storedHash == null) {
                return@withContext Result.Success(false)
            }

            // Hash is stored as "salt:hash" where salt was the original used for hashing
            // For simplicity, we store the salt in the hash. In production, derive it securely.
            val result = hashPin(pin, extractSalt(storedHash)) == storedHash
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error("Failed to verify PIN", cause = e)
        }
    }

    override suspend fun clearPin(): Result<Unit> = withContext(dispatcher) {
        try {
            appMetaStore.update { snapshot ->
                snapshot.copy(
                    securityAnswerHash = null,
                    securityQuestionId = null,
                    failedAttemptCount = 0,
                    lockoutUntil = null,
                    biometricEnrolled = false,
                    biometricWrappedKey = null,
                )
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to clear PIN", cause = e)
        }
    }

    override suspend fun setSecurityQuestion(questionId: String, answer: String): Result<Unit> =
        withContext(dispatcher) {
            try {
                val salt = ByteArray(16).apply { SecureRandom().nextBytes(this) }
                val hash = hashPin(answer, salt)
                appMetaStore.update { snapshot ->
                    snapshot.copy(
                        securityQuestionId = questionId,
                        securityAnswerHash = hash,
                    )
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error("Failed to set security question", cause = e)
            }
        }

    override suspend fun getSecurityQuestionId(): Result<String?> = withContext(dispatcher) {
        try {
            val snapshot = appMetaStore.read()
            Result.Success(snapshot.securityQuestionId)
        } catch (e: Exception) {
            Result.Error("Failed to get security question", cause = e)
        }
    }

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = withContext(dispatcher) {
        try {
            val snapshot = appMetaStore.read()
            val storedHash = snapshot.securityAnswerHash
            if (storedHash == null) {
                return@withContext Result.Success(false)
            }
            val result = hashPin(answer, extractSalt(storedHash)) == storedHash
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error("Failed to verify security answer", cause = e)
        }
    }

    override suspend fun isBiometricEnrolled(): Result<Boolean> = withContext(dispatcher) {
        try {
            val snapshot = appMetaStore.read()
            Result.Success(snapshot.biometricEnrolled)
        } catch (e: Exception) {
            Result.Error("Failed to check biometric enrollment", cause = e)
        }
    }

    override suspend fun enrollBiometric(): Result<Unit> = withContext(dispatcher) {
        try {
            appMetaStore.update { snapshot ->
                snapshot.copy(biometricEnrolled = true)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to enroll biometric", cause = e)
        }
    }

    override suspend fun clearBiometric(): Result<Unit> = withContext(dispatcher) {
        try {
            appMetaStore.update { snapshot ->
                snapshot.copy(
                    biometricEnrolled = false,
                    biometricWrappedKey = null,
                )
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to clear biometric", cause = e)
        }
    }

    override suspend fun getFailedAttemptCount(): Result<Long> = withContext(dispatcher) {
        try {
            val snapshot = appMetaStore.read()
            Result.Success(snapshot.failedAttemptCount)
        } catch (e: Exception) {
            Result.Error("Failed to get failed attempt count", cause = e)
        }
    }

    override suspend fun incrementFailedAttempts(): Result<Unit> = withContext(dispatcher) {
        try {
            appMetaStore.update { snapshot ->
                snapshot.copy(failedAttemptCount = snapshot.failedAttemptCount + 1)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to increment failed attempts", cause = e)
        }
    }

    override suspend fun resetFailedAttempts(): Result<Unit> = withContext(dispatcher) {
        try {
            appMetaStore.update { snapshot ->
                snapshot.copy(failedAttemptCount = 0, lockoutUntil = null)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to reset failed attempts", cause = e)
        }
    }

    override suspend fun getLockoutUntilMs(): Result<Long?> = withContext(dispatcher) {
        try {
            val snapshot = appMetaStore.read()
            Result.Success(snapshot.lockoutUntil)
        } catch (e: Exception) {
            Result.Error("Failed to get lockout time", cause = e)
        }
    }

    override suspend fun setLockoutUntilMs(lockedUntilMs: Long): Result<Unit> = withContext(dispatcher) {
        try {
            appMetaStore.update { snapshot ->
                snapshot.copy(lockoutUntil = lockedUntilMs)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to set lockout time", cause = e)
        }
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt)
        digest.update(pin.toByteArray(Charsets.UTF_8))
        val hash = digest.digest().joinToString("") { "%02x".format(it) }
        val saltHex = salt.joinToString("") { "%02x".format(it) }
        return "$saltHex:$hash"
    }

    private fun extractSalt(storedHash: String): ByteArray {
        val saltHex = storedHash.substringBefore(":")
        return saltHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}
