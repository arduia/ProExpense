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

    suspend fun changePin(newPin: String): Result<Unit>

    suspend fun setSecurityAnswer(answer: String): Result<Unit>

    suspend fun verifySecurityAnswer(answer: String): Result<Boolean>

    suspend fun isBiometricEnrolled(): Boolean

    suspend fun setBiometricEnrolled(enabled: Boolean): Result<Unit>
}
