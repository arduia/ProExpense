package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result

data class AuthSession(
    val isPinEnabled: Boolean,
    val isUnlocked: Boolean,
)

interface PinAuthRepository {
    suspend fun isPinConfigured(): Result<Boolean>

    suspend fun setPin(pin: String): Result<Unit>

    suspend fun verifyPin(pin: String): Result<Boolean>

    suspend fun clearPin(): Result<Unit>

    // Security question recovery (Phase 1)
    suspend fun setSecurityQuestion(
        questionId: String,
        answer: String,
    ): Result<Unit>

    suspend fun getSecurityQuestionId(): Result<String?>

    suspend fun verifySecurityAnswer(answer: String): Result<Boolean>

    // Biometric enrollment (Phase 1)
    suspend fun isBiometricEnrolled(): Result<Boolean>

    suspend fun enrollBiometric(): Result<Unit>

    suspend fun clearBiometric(): Result<Unit>

    // Lockout management
    suspend fun getFailedAttemptCount(): Result<Long>

    suspend fun incrementFailedAttempts(): Result<Unit>

    suspend fun resetFailedAttempts(): Result<Unit>

    suspend fun getLockoutUntilMs(): Result<Long?>

    suspend fun setLockoutUntilMs(lockedUntilMs: Long): Result<Unit>
}
