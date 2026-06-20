/**
 * Domain rules (design plan C7 / Settings):
 * - Disable PIN clears biometric enrollment state.
 * - Biometric toggle updates enrollment when PIN is configured.
 */
package com.arduia.expense.feature.auth

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SecuritySettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onDisablePin_clearsPinState() = runTest(dispatcher) {
        val repository = AuthFakePinAuthRepository(pinConfigured = true, biometricEnrolled = true)
        val viewModel = SecuritySettingsViewModel(
            pinAuthRepository = repository,
            biometricAvailability = AuthFakeBiometricAvailability(available = true),
            scope = this,
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
        val repository = AuthFakePinAuthRepository(pinConfigured = true)
        val viewModel = SecuritySettingsViewModel(
            pinAuthRepository = repository,
            biometricAvailability = AuthFakeBiometricAvailability(available = true),
            scope = this,
            onChangePinRequested = {},
        )
        advanceUntilIdle()

        viewModel.onBiometricToggled(true)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isBiometricEnabled)
        assertTrue(repository.biometricEnrolled)
    }
}
