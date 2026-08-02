package com.arduia.expense.shell

import com.arduia.expense.data.ProfileRepository
import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.PinAuthRepository
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
                    FakeProfileRepository(isComplete = onboardingComplete, name = displayName),
                ),
            pinAuthRepository =
                FakePinAuthRepository(
                    pinConfigured = pinConfigured,
                    stayUnlocked = stayUnlocked,
                    pinConfiguredFails = pinConfiguredFails,
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

private class FakeProfileRepository(
    private val isComplete: Boolean,
    private val name: String,
) : ProfileRepository {
    override suspend fun isOnboardingComplete(): Result<Boolean> = Result.Success(isComplete)

    override suspend fun setOnboardingComplete(): Result<Unit> = Result.Success(Unit)

    override suspend fun getDisplayName(): Result<String> = Result.Success(name)

    override suspend fun setDisplayName(name: String): Result<Unit> = Result.Success(Unit)
}

private class FakePinAuthRepository(
    private val pinConfigured: Boolean,
    private val stayUnlocked: Boolean,
    private val pinConfiguredFails: Boolean,
) : PinAuthRepository {
    override suspend fun isPinConfigured(): Result<Boolean> =
        if (pinConfiguredFails) Result.Error("boom") else Result.Success(pinConfigured)

    override suspend fun setPin(pin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(true)

    override suspend fun clearPin(): Result<Unit> = Result.Success(Unit)

    override suspend fun setSecurityQuestion(
        questionId: String,
        answer: String,
    ): Result<Unit> = Result.Success(Unit)

    override suspend fun getSecurityQuestionId(): Result<String?> = Result.Success(null)

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = Result.Success(true)

    override suspend fun isBiometricEnrolled(): Result<Boolean> = Result.Success(false)

    override suspend fun enrollBiometric(): Result<Unit> = Result.Success(Unit)

    override suspend fun clearBiometric(): Result<Unit> = Result.Success(Unit)

    override suspend fun isStayUnlockedInBackgroundEnabled(): Result<Boolean> = Result.Success(stayUnlocked)

    override suspend fun setStayUnlockedInBackgroundEnabled(enabled: Boolean): Result<Unit> = Result.Success(Unit)

    override suspend fun getFailedAttemptCount(): Result<Long> = Result.Success(0)

    override suspend fun incrementFailedAttempts(): Result<Unit> = Result.Success(Unit)

    override suspend fun resetFailedAttempts(): Result<Unit> = Result.Success(Unit)

    override suspend fun getLockoutUntilMs(): Result<Long?> = Result.Success(null)

    override suspend fun setLockoutUntilMs(lockedUntilMs: Long): Result<Unit> = Result.Success(Unit)
}
