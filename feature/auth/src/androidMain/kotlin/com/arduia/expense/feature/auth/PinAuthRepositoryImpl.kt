package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.storage.repository.AppMetaStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * PIN/biometric/security-question authentication backed by AppMetaStore.
 * PIN is hashed using PBKDF2-SHA256 with a unique salt per device.
 * Security question answers are hashed similarly.
 * Biometric enrollment is tracked as a boolean flag + wrapped key placeholder.
 */
class PinAuthRepositoryImpl(
    private val appMetaStore: AppMetaStore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PinAuthRepository {

    override suspend fun isPinConfigured(): Result<Boolean> = withContext(dispatcher) {
        try {
            val snapshot = appMetaStore.read()
            Result.Success(snapshot.pinHash != null)
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
                    pinHash = hash,
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
            val storedHash = snapshot.pinHash
            if (storedHash == null) {
                return@withContext Result.Success(false)
            }

            val result = verifyHash(pin, storedHash)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error("Failed to verify PIN", cause = e)
        }
    }

    override suspend fun clearPin(): Result<Unit> = withContext(dispatcher) {
        try {
            appMetaStore.update { snapshot ->
                snapshot.copy(
                    pinHash = null,
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
            val result = verifyHash(answer, storedHash)
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
                val newCount = snapshot.failedAttemptCount + 1
                val lockoutDuration = lockoutDurationMs(newCount)
                snapshot.copy(
                    failedAttemptCount = newCount,
                    lockoutUntil = if (lockoutDuration != null) {
                        System.currentTimeMillis() + lockoutDuration
                    } else {
                        snapshot.lockoutUntil
                    },
                )
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
        val iterations = 120_000
        val keyLength = 256
        val spec = PBEKeySpec(pin.toCharArray(), salt, iterations, keyLength)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = factory.generateSecret(spec)
        val hashBytes = key.encoded
        val saltHex = salt.joinToString("") { "%02x".format(it) }
        val hashHex = hashBytes.joinToString("") { "%02x".format(it) }
        return "v2:$iterations:$saltHex:$hashHex"
    }

    private fun verifyHash(input: String, storedHash: String): Boolean {
        return if (storedHash.startsWith("v2:")) {
            verifyPbkdf2(input, storedHash)
        } else {
            false
        }
    }

    private fun verifyPbkdf2(input: String, storedHash: String): Boolean {
        val parts = storedHash.split(":")
        if (parts.size != 4) return false
        val iterations = parts[1].toIntOrNull() ?: return false
        val saltHex = parts[2]
        val expectedHashHex = parts[3]

        val salt = saltHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        val spec = PBEKeySpec(input.toCharArray(), salt, iterations, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = factory.generateSecret(spec)
        val computedHashHex = key.encoded.joinToString("") { "%02x".format(it) }
        return computedHashHex == expectedHashHex
    }

    private fun lockoutDurationMs(failedAttemptCount: Long): Long? = when {
        failedAttemptCount <= 4 -> null
        failedAttemptCount == 5L -> 30 * 1000
        failedAttemptCount == 6L -> 60 * 1000
        else -> 5 * 60 * 1000
    }
}
