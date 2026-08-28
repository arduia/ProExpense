package com.arduia.expense.shell

import com.arduia.expense.feature.onboarding.GetOnboardingStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Backbone coverage for the launch gate.
 *
 * Traceability: US-ONB-1 Scenario 1 (carousel shows on first launch) for the Onboarding gate,
 * US-AUTH-4 Scenario 4 (re-lock on resume) and Scenario 5 (stay unlocked during app-switch) for
 * the background transition, and US-AUTH-2 for the PIN gate on a configured device.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AppShellViewModelTest {
    private fun TestScope.viewModel(
        onboardingComplete: Boolean = true,
        displayName: String = "Maya",
        pinConfigured: Boolean = false,
        stayUnlocked: Boolean = false,
        pinConfiguredFails: Boolean = false,
    ): AppShellViewModel =
        AppShellViewModel(
            getOnboardingStatus =
                GetOnboardingStatusUseCase(
                    FakeProfile(name = displayName, complete = onboardingComplete),
                ),
            pinAuthRepository =
                FakePinAuth(
                    correctPin = if (pinConfigured) "123456" else null,
                    stayUnlocked = stayUnlocked,
                    failPinLookup = pinConfiguredFails,
                ),
            // Shares runTest's scheduler so the splash delay runs on the virtual clock.
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `holds on splash until the splash delay elapses`() =
        runTest {
            val vm = viewModel()

            advanceTimeBy(SPLASH_DURATION_MILLIS - 1)
            assertEquals(AppGate.Splash, vm.uiState.value.gate)

            advanceUntilIdle()
            assertEquals(AppGate.Ready, vm.uiState.value.gate)
        }

    @Test
    fun `routes to onboarding when setup is incomplete`() =
        runTest {
            val vm = viewModel(onboardingComplete = false)

            advanceUntilIdle()

            assertEquals(AppGate.Onboarding, vm.uiState.value.gate)
        }

    @Test
    fun `routes to the pin lock when a pin is configured and the session is not unlocked`() =
        runTest {
            val vm = viewModel(pinConfigured = true)

            advanceUntilIdle()
            assertEquals(AppGate.PinLock, vm.uiState.value.gate)

            vm.onUnlocked()
            assertEquals(AppGate.Ready, vm.uiState.value.gate)
        }

    @Test
    fun `re-locks on background by default`() =
        runTest {
            val vm = viewModel(pinConfigured = true)
            advanceUntilIdle()
            vm.onUnlocked()

            vm.onEnterBackground()

            assertFalse(vm.uiState.value.unlocked)
            assertEquals(AppGate.PinLock, vm.uiState.value.gate)
        }

    @Test
    fun `stays unlocked on background when the opt-in setting is enabled`() =
        runTest {
            val vm = viewModel(pinConfigured = true, stayUnlocked = true)
            advanceUntilIdle()
            vm.onUnlocked()

            vm.onEnterBackground()

            assertTrue(vm.uiState.value.unlocked)
            assertEquals(AppGate.Ready, vm.uiState.value.gate)
        }

    @Test
    fun `treats a failed pin lookup as no pin rather than locking the user out`() =
        runTest {
            val vm = viewModel(pinConfiguredFails = true)

            advanceUntilIdle()

            assertEquals(AppGate.Ready, vm.uiState.value.gate)
        }

    @Test
    fun `setting a pin in-session does not lock the session that created it`() =
        runTest {
            val vm = viewModel()
            advanceUntilIdle()

            vm.onPinConfigured(configured = true)

            assertEquals(AppGate.Ready, vm.uiState.value.gate)
        }
}
