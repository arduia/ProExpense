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
}
