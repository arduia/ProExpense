package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.shared.hexToByteArrayOrNull
import com.arduia.expense.shared.platformPbkdf2Sha256Hex
import com.arduia.expense.shared.platformSecureRandomBytes
import com.arduia.expense.shared.toLowerHex
import com.arduia.expense.storage.repository.AppMetaStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val PBKDF2_ITERATIONS = 120_000
private const val PBKDF2_KEY_LENGTH_BITS = 256
private const val SALT_SIZE_BYTES = 16
private const val HASH_PREFIX = "v2:"

/**
 * PIN/biometric/security-question authentication backed by AppMetaStore.
 * PIN is hashed using PBKDF2-SHA256 with a unique salt per device.
 * Security question answers are hashed similarly.
 * Biometric enrollment is tracked as a boolean flag + wrapped key placeholder.
 *
 * Shared by both platforms: only the CSPRNG and the KDF differ, and both are reached through the
 * `shared` module's `expect` seams so the `v2:` hash wire format stays byte-identical across
 * Android and iOS.
 */
class PinAuthRepositoryImpl(
    private val appMetaStore: AppMetaStore,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : PinAuthRepository {
    override suspend fun isPinConfigured(): Result<Boolean> =
        withContext(dispatcher) {
            try {
                val snapshot = appMetaStore.read()
                Result.Success(snapshot.pinHash != null)
            } catch (e: Exception) {
                Result.Error("Failed to check PIN configuration", cause = e)
            }
        }

    override suspend fun setPin(pin: String): Result<Unit> =
        withContext(dispatcher) {
            try {
                val hash = hashSecret(pin)
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

    override suspend fun verifyPin(pin: String): Result<Boolean> =
        withContext(dispatcher) {
            try {
                val storedHash = appMetaStore.read().pinHash
                Result.Success(storedHash != null && verifyHash(pin, storedHash))
            } catch (e: Exception) {
                Result.Error("Failed to verify PIN", cause = e)
            }
        }

    override suspend fun clearPin(): Result<Unit> =
        withContext(dispatcher) {
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

    override suspend fun setSecurityQuestion(
        questionId: String,
        answer: String,
    ): Result<Unit> =
        withContext(dispatcher) {
            try {
                val hash = hashSecret(answer)
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

    override suspend fun getSecurityQuestionId(): Result<String?> =
        withContext(dispatcher) {
            try {
                Result.Success(appMetaStore.read().securityQuestionId)
            } catch (e: Exception) {
                Result.Error("Failed to get security question", cause = e)
            }
        }

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> =
        withContext(dispatcher) {
            try {
                val storedHash = appMetaStore.read().securityAnswerHash
                Result.Success(storedHash != null && verifyHash(answer, storedHash))
            } catch (e: Exception) {
                Result.Error("Failed to verify security answer", cause = e)
            }
        }

    override suspend fun isBiometricEnrolled(): Result<Boolean> =
        withContext(dispatcher) {
            try {
                Result.Success(appMetaStore.read().biometricEnrolled)
            } catch (e: Exception) {
                Result.Error("Failed to check biometric enrollment", cause = e)
            }
        }

    override suspend fun enrollBiometric(): Result<Unit> =
        withContext(dispatcher) {
            try {
                appMetaStore.update { snapshot ->
                    snapshot.copy(biometricEnrolled = true)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error("Failed to enroll biometric", cause = e)
            }
        }

    override suspend fun clearBiometric(): Result<Unit> =
        withContext(dispatcher) {
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

    override suspend fun isStayUnlockedInBackgroundEnabled(): Result<Boolean> =
        withContext(dispatcher) {
            try {
                Result.Success(appMetaStore.read().stayUnlockedInBackground)
            } catch (e: Exception) {
                Result.Error("Failed to check stay-unlocked setting", cause = e)
            }
        }

    override suspend fun setStayUnlockedInBackgroundEnabled(enabled: Boolean): Result<Unit> =
        withContext(dispatcher) {
            try {
                appMetaStore.update { snapshot -> snapshot.copy(stayUnlockedInBackground = enabled) }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error("Failed to set stay-unlocked setting", cause = e)
            }
        }

    override suspend fun getFailedAttemptCount(): Result<Long> =
        withContext(dispatcher) {
            try {
                Result.Success(appMetaStore.read().failedAttemptCount)
            } catch (e: Exception) {
                Result.Error("Failed to get failed attempt count", cause = e)
            }
        }

    override suspend fun incrementFailedAttempts(): Result<Unit> =
        withContext(dispatcher) {
            try {
                appMetaStore.update { snapshot ->
                    val newCount = snapshot.failedAttemptCount + 1
                    val lockoutDuration = lockoutDurationMs(newCount)
                    snapshot.copy(
                        failedAttemptCount = newCount,
                        lockoutUntil =
                            if (lockoutDuration != null) {
                                currentEpochMillis() + lockoutDuration
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

    override suspend fun resetFailedAttempts(): Result<Unit> =
        withContext(dispatcher) {
            try {
                appMetaStore.update { snapshot ->
                    snapshot.copy(failedAttemptCount = 0, lockoutUntil = null)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error("Failed to reset failed attempts", cause = e)
            }
        }

    override suspend fun getLockoutUntilMs(): Result<Long?> =
        withContext(dispatcher) {
            try {
                Result.Success(appMetaStore.read().lockoutUntil)
            } catch (e: Exception) {
                Result.Error("Failed to get lockout time", cause = e)
            }
        }

    override suspend fun setLockoutUntilMs(lockedUntilMs: Long): Result<Unit> =
        withContext(dispatcher) {
            try {
                appMetaStore.update { snapshot ->
                    snapshot.copy(lockoutUntil = lockedUntilMs)
                }
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error("Failed to set lockout time", cause = e)
            }
        }

    private fun hashSecret(secret: String): String {
        val salt = platformSecureRandomBytes(SALT_SIZE_BYTES)
        val hashHex = platformPbkdf2Sha256Hex(secret, salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH_BITS)
        return "$HASH_PREFIX$PBKDF2_ITERATIONS:${salt.toLowerHex()}:$hashHex"
    }

    private fun verifyHash(
        input: String,
        storedHash: String,
    ): Boolean = storedHash.startsWith(HASH_PREFIX) && verifyPbkdf2(input, storedHash)

    private fun verifyPbkdf2(
        input: String,
        storedHash: String,
    ): Boolean {
        val parts = storedHash.split(":")
        val iterations = parts.getOrNull(1)?.toIntOrNull()
        val salt = parts.getOrNull(2)?.hexToByteArrayOrNull()
        return if (parts.size != 4 || iterations == null || salt == null) {
            false
        } else {
            platformPbkdf2Sha256Hex(input, salt, iterations, PBKDF2_KEY_LENGTH_BITS) == parts[3]
        }
    }

    private fun lockoutDurationMs(failedAttemptCount: Long): Long? =
        when {
            failedAttemptCount <= 4 -> null
            failedAttemptCount == 5L -> 30 * 1000
            failedAttemptCount == 6L -> 60 * 1000
            else -> 5 * 60 * 1000
        }
}
