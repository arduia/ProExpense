package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

private class FakePinAuthRepository(
    var pin: String? = null,
    var securityQuestionId: String? = null,
    var securityAnswer: String? = null,
    var biometricEnrolled: Boolean = false,
    var failedAttempts: Long = 0,
    var lockoutUntilMs: Long? = null,
    var setPinError: String? = null,
    var verifyAnswerError: String? = null,
) : PinAuthRepository {
    override suspend fun isPinConfigured(): Result<Boolean> = Result.Success(pin != null)

    override suspend fun setPin(pin: String): Result<Unit> {
        setPinError?.let { return Result.Error(it) }
        this.pin = pin
        return Result.Success(Unit)
    }

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(pin == this.pin)

    override suspend fun clearPin(): Result<Unit> {
        pin = null
        return Result.Success(Unit)
    }

    override suspend fun setSecurityQuestion(questionId: String, answer: String): Result<Unit> {
        securityQuestionId = questionId
        securityAnswer = answer
        return Result.Success(Unit)
    }

    override suspend fun getSecurityQuestionId(): Result<String?> = Result.Success(securityQuestionId)

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> {
        verifyAnswerError?.let { return Result.Error(it) }
        return Result.Success(answer == securityAnswer)
    }

    override suspend fun isBiometricEnrolled(): Result<Boolean> = Result.Success(biometricEnrolled)

    override suspend fun enrollBiometric(): Result<Unit> {
        biometricEnrolled = true
        return Result.Success(Unit)
    }

    override suspend fun clearBiometric(): Result<Unit> {
        biometricEnrolled = false
        return Result.Success(Unit)
    }

    override suspend fun getFailedAttemptCount(): Result<Long> = Result.Success(failedAttempts)

    override suspend fun incrementFailedAttempts(): Result<Unit> {
        failedAttempts += 1
        return Result.Success(Unit)
    }

    override suspend fun resetFailedAttempts(): Result<Unit> {
        failedAttempts = 0
        return Result.Success(Unit)
    }

    override suspend fun getLockoutUntilMs(): Result<Long?> = Result.Success(lockoutUntilMs)

    override suspend fun setLockoutUntilMs(lockedUntilMs: Long): Result<Unit> {
        lockoutUntilMs = lockedUntilMs
        return Result.Success(Unit)
    }
}

class SetupPinUseCaseTest {

    @Test
    fun invoke_setsPinQuestionAndBiometricOnSuccess() = runTest {
        val repo = FakePinAuthRepository()
        val useCase = SetupPinUseCase(repo)

        val result = useCase(
            pin = "1234",
            securityQuestionId = "pet_name",
            securityAnswer = "Rex",
            enableBiometric = true,
        )

        assertIs<Result.Success<Unit>>(result)
        assertEquals("1234", repo.pin)
        assertEquals("pet_name", repo.securityQuestionId)
        assertTrue(repo.biometricEnrolled)
    }

    @Test
    fun invoke_skipsBiometricEnrollmentWhenDisabled() = runTest {
        val repo = FakePinAuthRepository()
        val useCase = SetupPinUseCase(repo)

        useCase(pin = "1234", securityQuestionId = "pet_name", securityAnswer = "Rex", enableBiometric = false)

        assertTrue(!repo.biometricEnrolled)
    }

    @Test
    fun invoke_shortCircuitsWhenSetPinFails() = runTest {
        val repo = FakePinAuthRepository(setPinError = "storage failure")
        val useCase = SetupPinUseCase(repo)

        val result = useCase(pin = "1234", securityQuestionId = "pet_name", securityAnswer = "Rex", enableBiometric = true)

        assertIs<Result.Error>(result)
        assertEquals(null, repo.securityQuestionId)
        assertTrue(!repo.biometricEnrolled)
    }
}

class VerifyPinUseCaseTest {

    @Test
    fun invoke_unlocksAndResetsAttemptsOnCorrectPin() = runTest {
        val repo = FakePinAuthRepository(pin = "1234", failedAttempts = 3)
        val useCase = VerifyPinUseCase(repo)

        val result = useCase("1234")

        assertEquals(VerifyPinResult.Unlocked, result)
        assertEquals(0, repo.failedAttempts)
    }

    @Test
    fun invoke_incrementsAttemptsAndReportsLockoutOnWrongPin() = runTest {
        val repo = FakePinAuthRepository(pin = "1234", lockoutUntilMs = 5000L)
        val useCase = VerifyPinUseCase(repo)

        val result = useCase("0000")

        assertIs<VerifyPinResult.Incorrect>(result)
        assertEquals(5000L, (result as VerifyPinResult.Incorrect).lockoutUntilMs)
        assertEquals(1, repo.failedAttempts)
    }

    @Test
    fun invoke_propagatesRepositoryError() = runTest {
        val repo = object : PinAuthRepository by FakePinAuthRepository() {
            override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Error("db error")
        }
        val useCase = VerifyPinUseCase(repo)

        val result = useCase("1234")

        assertEquals(VerifyPinResult.Error("db error"), result)
    }
}

class VerifyRecoveryAnswerUseCaseTest {

    @Test
    fun invoke_returnsCorrectOnMatchingAnswer() = runTest {
        val repo = FakePinAuthRepository(securityAnswer = "Rex")
        val useCase = VerifyRecoveryAnswerUseCase(repo)

        val result = useCase("Rex", currentAttempts = 0)

        assertEquals(RecoveryAnswerResult.Correct, result)
    }

    @Test
    fun invoke_incrementsAttemptsAndFlagsExhaustionAtMax() = runTest {
        val repo = FakePinAuthRepository(securityAnswer = "Rex")
        val useCase = VerifyRecoveryAnswerUseCase(repo)

        val result = useCase("Fido", currentAttempts = PIN_RECOVERY_MAX_ATTEMPTS - 1)

        assertEquals(RecoveryAnswerResult.Incorrect(PIN_RECOVERY_MAX_ATTEMPTS, attemptsExhausted = true), result)
    }

    @Test
    fun invoke_doesNotFlagExhaustionBelowMax() = runTest {
        val repo = FakePinAuthRepository(securityAnswer = "Rex")
        val useCase = VerifyRecoveryAnswerUseCase(repo)

        val result = useCase("Fido", currentAttempts = 0)

        assertEquals(RecoveryAnswerResult.Incorrect(1, attemptsExhausted = false), result)
    }

    @Test
    fun invoke_propagatesRepositoryError() = runTest {
        val repo = FakePinAuthRepository(verifyAnswerError = "db error")
        val useCase = VerifyRecoveryAnswerUseCase(repo)

        val result = useCase("Rex", currentAttempts = 0)

        assertEquals(RecoveryAnswerResult.Error("db error"), result)
    }
}

class ResetPinUseCaseTest {

    @Test
    fun invoke_setsNewPinAndResetsFailedAttemptsOnSuccess() = runTest {
        val repo = FakePinAuthRepository(pin = "old", failedAttempts = 4)
        val useCase = ResetPinUseCase(repo)

        val result = useCase("new")

        assertIs<Result.Success<Unit>>(result)
        assertEquals("new", repo.pin)
        assertEquals(0, repo.failedAttempts)
    }

    @Test
    fun invoke_doesNotResetAttemptsWhenSetPinFails() = runTest {
        val repo = FakePinAuthRepository(pin = "old", failedAttempts = 4, setPinError = "storage failure")
        val useCase = ResetPinUseCase(repo)

        val result = useCase("new")

        assertIs<Result.Error>(result)
        assertEquals(4, repo.failedAttempts)
    }
}
