package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PinSetupViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onContinueSecurity_movesToBiometricWhenAvailable() = runTest(dispatcher) {
        var completed = false
        val viewModel = PinSetupViewModel(
            pinAuthRepository = FakePinAuthRepository(),
            biometricAvailability = FakeBiometricAvailability(available = true),
            scope = scope.backgroundScope,
            onComplete = { completed = true },
        )

        repeat(6) { viewModel.onDigit(1) }
        repeat(6) { viewModel.onDigit(1) }
        viewModel.onSecurityAnswerChange("answer")
        viewModel.onContinueSecurity()
        advanceUntilIdle()

        assertEquals(PinSetupStep.BIOMETRIC_OFFER, viewModel.uiState.value.step)
        assertEquals(false, completed)

        viewModel.onBiometricOffered(true)
        advanceUntilIdle()
        assertEquals(true, completed)
    }
}

private class FakeBiometricAvailability(
    private val available: Boolean,
) : BiometricAvailability {
    override fun isAvailable(): Boolean = available
}

private class FakePinAuthRepository : PinAuthRepository {
    override suspend fun isPinConfigured(): Result<Boolean> = Result.Success(false)

    override suspend fun setPin(pin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(true)

    override suspend fun clearPin(): Result<Unit> = Result.Success(Unit)

    override suspend fun changePin(newPin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun setSecurityAnswer(answer: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = Result.Success(true)

    override suspend fun isBiometricEnrolled(): Boolean = false

    override suspend fun setBiometricEnrolled(enabled: Boolean): Result<Unit> = Result.Success(Unit)
}
