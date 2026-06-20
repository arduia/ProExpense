package com.arduia.expense.feature.auth

import com.arduia.expense.data.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SecuritySettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onDisablePin_clearsPinState() = runTest(dispatcher) {
        val repository = FakePinAuthRepository(pinConfigured = true, biometricEnrolled = true)
        val viewModel = SecuritySettingsViewModel(
            pinAuthRepository = repository,
            biometricAvailability = FakeBiometricAvailability(true),
            scope = scope.backgroundScope,
            onChangePinRequested = {},
        )
        advanceUntilIdle()

        viewModel.onDisablePin()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isPinEnabled)
        assertFalse(viewModel.uiState.value.isBiometricEnabled)
    }

    @Test
    fun onBiometricToggled_updatesEnrollment() = runTest(dispatcher) {
        val repository = FakePinAuthRepository(pinConfigured = true)
        val viewModel = SecuritySettingsViewModel(
            pinAuthRepository = repository,
            biometricAvailability = FakeBiometricAvailability(true),
            scope = scope.backgroundScope,
            onChangePinRequested = {},
        )
        advanceUntilIdle()

        viewModel.onBiometricToggled(true)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isBiometricEnabled)
        assertTrue(repository.biometricEnrolled)
    }
}

private class FakeBiometricAvailability(
    private val available: Boolean,
) : BiometricAvailability {
    override fun isAvailable(): Boolean = available
}

private class FakePinAuthRepository(
    private var pinConfigured: Boolean,
    var biometricEnrolled: Boolean = false,
) : PinAuthRepository {
    override suspend fun isPinConfigured(): Result<Boolean> = Result.Success(pinConfigured)

    override suspend fun setPin(pin: String): Result<Unit> {
        pinConfigured = true
        return Result.Success(Unit)
    }

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(true)

    override suspend fun clearPin(): Result<Unit> {
        pinConfigured = false
        biometricEnrolled = false
        return Result.Success(Unit)
    }

    override suspend fun changePin(newPin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun setSecurityAnswer(questionId: String, answer: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = Result.Success(true)

    override suspend fun isBiometricEnrolled(): Boolean = biometricEnrolled

    override suspend fun setBiometricEnrolled(enabled: Boolean): Result<Unit> {
        biometricEnrolled = enabled
        return Result.Success(Unit)
    }

    override suspend fun unlockWithBiometric(): Result<Boolean> = Result.Success(true)

    override suspend fun getSecurityQuestionId(): String? = null
}
