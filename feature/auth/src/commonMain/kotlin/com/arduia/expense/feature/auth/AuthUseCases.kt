package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result

const val PIN_RECOVERY_MAX_ATTEMPTS = 5

class SetupPinUseCase(
    private val repository: PinAuthRepository,
) {
    suspend operator fun invoke(
        pin: String,
        securityQuestionId: String,
        securityAnswer: String,
        enableBiometric: Boolean,
    ): Result<Unit> {
        val pinResult = repository.setPin(pin)
        if (pinResult is Result.Error) return pinResult
        val questionResult = repository.setSecurityQuestion(securityQuestionId, securityAnswer)
        if (questionResult is Result.Error) return questionResult
        if (enableBiometric) repository.enrollBiometric()
        return Result.Success(Unit)
    }
}

sealed interface VerifyPinResult {
    data object Unlocked : VerifyPinResult

    data class Incorrect(
        val lockoutUntilMs: Long?,
    ) : VerifyPinResult

    data class Error(
        val message: String,
    ) : VerifyPinResult
}

class VerifyPinUseCase(
    private val repository: PinAuthRepository,
) {
    suspend operator fun invoke(pin: String): VerifyPinResult =
        when (val result = repository.verifyPin(pin)) {
            is Result.Success -> {
                if (result.data) {
                    repository.resetFailedAttempts()
                    VerifyPinResult.Unlocked
                } else {
                    repository.incrementFailedAttempts()
                    val lockoutUntilMs = (repository.getLockoutUntilMs() as? Result.Success)?.data
                    VerifyPinResult.Incorrect(lockoutUntilMs)
                }
            }
            is Result.Error -> VerifyPinResult.Error(result.message)
        }
}

sealed interface RecoveryAnswerResult {
    data object Correct : RecoveryAnswerResult

    data class Incorrect(
        val attempts: Int,
        val attemptsExhausted: Boolean,
    ) : RecoveryAnswerResult

    data class Error(
        val message: String,
    ) : RecoveryAnswerResult
}

class VerifyRecoveryAnswerUseCase(
    private val repository: PinAuthRepository,
) {
    suspend operator fun invoke(
        answer: String,
        currentAttempts: Int,
    ): RecoveryAnswerResult =
        when (val result = repository.verifySecurityAnswer(answer)) {
            is Result.Success -> {
                if (result.data) {
                    RecoveryAnswerResult.Correct
                } else {
                    val attempts = currentAttempts + 1
                    RecoveryAnswerResult.Incorrect(attempts, attempts >= PIN_RECOVERY_MAX_ATTEMPTS)
                }
            }
            is Result.Error -> RecoveryAnswerResult.Error(result.message)
        }
}

class ResetPinUseCase(
    private val repository: PinAuthRepository,
) {
    suspend operator fun invoke(newPin: String): Result<Unit> {
        val result = repository.setPin(newPin)
        if (result is Result.Success) repository.resetFailedAttempts()
        return result
    }
}

/** Turns off PIN lock entirely, clearing both the PIN and any enrolled biometric. */
class DisablePinUseCase(
    private val repository: PinAuthRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        val result = repository.clearPin()
        if (result is Result.Error) return result
        repository.clearBiometric()
        return Result.Success(Unit)
    }
}
