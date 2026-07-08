package com.arduia.expense.feature.auth

import com.arduia.expense.data.PinCredentialStore
import com.arduia.expense.data.Result
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.shared.platformPbkdf2Sha256
import com.arduia.expense.shared.secureRandomBytes
import com.arduia.expense.shared.toHexString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * PIN/biometric/security-question authentication backed by [PinCredentialStore].
 * PIN is hashed using PBKDF2-SHA256 with a unique salt per device.
 * Security question answers are hashed similarly.
 * Biometric enrollment is tracked as a boolean flag + wrapped key placeholder.
 */
class DefaultPinAuthRepository(
    private val credentialStore: PinCredentialStore,
    private val dispatcher: CoroutineDispatcher,
) : PinAuthRepository {

    override suspend fun isPinConfigured(): Result<Boolean> = withContext(dispatcher) {
        try {
            val credentials = credentialStore.read()
            Result.Success(credentials.pinHash != null)
        } catch (e: Exception) {
            Result.Error("Failed to check PIN configuration", cause = e)
        }
    }

    override suspend fun setPin(pin: String): Result<Unit> = withContext(dispatcher) {
        try {
            val salt = secureRandomBytes(16)
            val hash = hashPin(pin, salt)
            credentialStore.update { credentials ->
                credentials.copy(
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
            val credentials = credentialStore.read()
            val storedHash = credentials.pinHash
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
            credentialStore.update { credentials ->
                credentials.copy(
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
                val salt = secureRandomBytes(16)
                val hash = hashPin(answer, salt)
                credentialStore.update { credentials ->
                    credentials.copy(
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
            val credentials = credentialStore.read()
            Result.Success(credentials.securityQuestionId)
        } catch (e: Exception) {
            Result.Error("Failed to get security question", cause = e)
        }
    }

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = withContext(dispatcher) {
        try {
            val credentials = credentialStore.read()
            val storedHash = credentials.securityAnswerHash
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
            val credentials = credentialStore.read()
            Result.Success(credentials.biometricEnrolled)
        } catch (e: Exception) {
            Result.Error("Failed to check biometric enrollment", cause = e)
        }
    }

    override suspend fun enrollBiometric(): Result<Unit> = withContext(dispatcher) {
        try {
            credentialStore.update { credentials ->
                credentials.copy(biometricEnrolled = true)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to enroll biometric", cause = e)
        }
    }

    override suspend fun clearBiometric(): Result<Unit> = withContext(dispatcher) {
        try {
            credentialStore.update { credentials ->
                credentials.copy(
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
            val credentials = credentialStore.read()
            Result.Success(credentials.failedAttemptCount)
        } catch (e: Exception) {
            Result.Error("Failed to get failed attempt count", cause = e)
        }
    }

    override suspend fun incrementFailedAttempts(): Result<Unit> = withContext(dispatcher) {
        try {
            credentialStore.update { credentials ->
                val newCount = credentials.failedAttemptCount + 1
                val lockoutDuration = lockoutDurationMs(newCount)
                credentials.copy(
                    failedAttemptCount = newCount,
                    lockoutUntil = if (lockoutDuration != null) {
                        currentEpochMillis() + lockoutDuration
                    } else {
                        credentials.lockoutUntil
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
            credentialStore.update { credentials ->
                credentials.copy(failedAttemptCount = 0, lockoutUntil = null)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to reset failed attempts", cause = e)
        }
    }

    override suspend fun getLockoutUntilMs(): Result<Long?> = withContext(dispatcher) {
        try {
            val credentials = credentialStore.read()
            Result.Success(credentials.lockoutUntil)
        } catch (e: Exception) {
            Result.Error("Failed to get lockout time", cause = e)
        }
    }

    override suspend fun setLockoutUntilMs(lockedUntilMs: Long): Result<Unit> = withContext(dispatcher) {
        try {
            credentialStore.update { credentials ->
                credentials.copy(lockoutUntil = lockedUntilMs)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Failed to set lockout time", cause = e)
        }
    }

    private fun hashPin(pin: String, salt: ByteArray): String {
        val iterations = 120_000
        val keyLength = 256
        val hashBytes = platformPbkdf2Sha256(pin.toCharArray(), salt, iterations, keyLength)
        val saltHex = salt.toHexString()
        val hashHex = hashBytes.toHexString()
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
        val computedHashHex = platformPbkdf2Sha256(input.toCharArray(), salt, iterations, 256).toHexString()
        return computedHashHex == expectedHashHex
    }

    private fun lockoutDurationMs(failedAttemptCount: Long): Long? = when {
        failedAttemptCount <= 4 -> null
        failedAttemptCount == 5L -> 30 * 1000
        failedAttemptCount == 6L -> 60 * 1000
        else -> 5 * 60 * 1000
    }
}
