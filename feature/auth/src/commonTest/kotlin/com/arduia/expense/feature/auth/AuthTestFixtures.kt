package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result

internal class AuthFakeBiometricAvailability(
    private val available: Boolean,
) : BiometricAvailability {
    override fun isAvailable(): Boolean = available
}

internal class AuthFakePinAuthRepository(
    private var pinConfigured: Boolean = false,
    var biometricEnrolled: Boolean = false,
    private val verifyPinResult: Boolean = false,
    private val verifySecurityAnswer: Boolean = false,
    private val biometricUnlock: Boolean = false,
) : PinAuthRepository {
    var pinSaved = false

    override suspend fun isPinConfigured(): Result<Boolean> = Result.Success(pinConfigured)

    override suspend fun setPin(pin: String): Result<Unit> {
        pinConfigured = true
        pinSaved = true
        return Result.Success(Unit)
    }

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(verifyPinResult)

    override suspend fun clearPin(): Result<Unit> {
        pinConfigured = false
        biometricEnrolled = false
        return Result.Success(Unit)
    }

    override suspend fun changePin(newPin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun setSecurityAnswer(questionId: String, answer: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = Result.Success(verifySecurityAnswer)

    override suspend fun isBiometricEnrolled(): Boolean = biometricEnrolled

    override suspend fun setBiometricEnrolled(enabled: Boolean): Result<Unit> {
        biometricEnrolled = enabled
        return Result.Success(Unit)
    }

    override suspend fun unlockWithBiometric(): Result<Boolean> = Result.Success(biometricUnlock)

    override suspend fun getSecurityQuestionId(): String? = null
}
