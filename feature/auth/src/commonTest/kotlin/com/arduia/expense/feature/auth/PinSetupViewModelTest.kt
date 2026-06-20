/**
 * Domain rules (design plan Screen 14 / C6):
 * - PIN must be 6 digits; confirm must match enter step.
 * - Security question + answer required before PIN is saved.
 * - Biometric offer shown only when platform biometric is available.
 * - PIN save triggers key rotation (via PinAuthRepository).
 */
package com.arduia.expense.feature.auth

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
class PinSetupViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onContinueSecurity_movesToBiometricWhenAvailable() = runTest(dispatcher) {
        var completed = false
        val viewModel = PinSetupViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(),
            biometricAvailability = AuthFakeBiometricAvailability(available = true),
            scope = this,
            onComplete = { completed = true },
        )

        repeat(6) { viewModel.onDigit(1) }
        repeat(6) { viewModel.onDigit(1) }
        viewModel.onSecurityAnswerChange("answer")
        viewModel.onContinueSecurity()
        advanceUntilIdle()

        assertEquals(PinSetupStep.BIOMETRIC_OFFER, viewModel.uiState.value.step)
        assertFalse(completed)

        viewModel.onBiometricOffered(true)
        advanceUntilIdle()
        assertTrue(completed)
    }

    @Test
    fun confirmMismatch_showsErrorAndStaysOnConfirm() = runTest(dispatcher) {
        val viewModel = PinSetupViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(),
            biometricAvailability = AuthFakeBiometricAvailability(available = false),
            scope = this,
            onComplete = {},
        )

        repeat(6) { viewModel.onDigit(1) }
        repeat(5) { viewModel.onDigit(2) }
        viewModel.onDigit(3)
        advanceUntilIdle()

        assertEquals(PinSetupStep.CONFIRM, viewModel.uiState.value.step)
        assertTrue(viewModel.uiState.value.mismatchError)
        assertEquals(0, viewModel.uiState.value.filledDots)
    }

    @Test
    fun blankSecurityAnswer_doesNotAdvance() = runTest(dispatcher) {
        val repository = AuthFakePinAuthRepository()
        val viewModel = PinSetupViewModel(
            pinAuthRepository = repository,
            biometricAvailability = AuthFakeBiometricAvailability(available = false),
            scope = this,
            onComplete = {},
        )

        repeat(6) { viewModel.onDigit(1) }
        repeat(6) { viewModel.onDigit(1) }
        viewModel.onContinueSecurity()
        advanceUntilIdle()

        assertEquals(PinSetupStep.SECURITY_QUESTION, viewModel.uiState.value.step)
        assertFalse(repository.pinSaved)
    }

    @Test
    fun noBiometric_skipsOfferAndCompletes() = runTest(dispatcher) {
        var completed = false
        val viewModel = PinSetupViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(),
            biometricAvailability = AuthFakeBiometricAvailability(available = false),
            scope = this,
            onComplete = { completed = true },
        )

        repeat(6) { viewModel.onDigit(1) }
        repeat(6) { viewModel.onDigit(1) }
        viewModel.onSecurityAnswerChange("answer")
        viewModel.onContinueSecurity()
        advanceUntilIdle()

        assertTrue(completed)
    }
}
