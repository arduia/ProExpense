/**
 * Domain rules (design plan C6/C8 / PIN Entry Screen 15):
 * - 6-digit PIN entry; verify on sixth digit.
 * - 5 failed attempts → 30s lockout (LOCKOUT mode).
 * - Biometric auto-trigger only when enrolled and not locked out.
 * - Biometric failure counts as a failed attempt.
 * - Biometric unlock blocked during lockout.
 * - Correct PIN or biometric success resets lockout and calls onUnlocked.
 * - Security answer recovery resets lockout on success.
 */
package com.arduia.expense.feature.auth

import com.arduia.expense.data.LockoutState
import com.arduia.expense.data.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PinEntryViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onScreenLoaded_setsBiometricTriggerWhenEnrolled() = runTest(dispatcher) {
        val viewModel = PinEntryViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(biometricEnrolled = true),
            lockoutRepository = FakeLockoutRepository(),
            nowEpochMillis = { 1_000L },
            scope = this,
            onUnlocked = {},
            onRecoverySuccess = {},
        )

        viewModel.onScreenLoaded()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.biometricShouldTrigger)
    }

    @Test
    fun onScreenLoaded_skipsBiometricWhenLockedOut() = runTest(dispatcher) {
        val viewModel = PinEntryViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(biometricEnrolled = true),
            lockoutRepository = FakeLockoutRepository(lockedOut = true, lockoutUntil = 10_000L),
            nowEpochMillis = { 1_000L },
            scope = this,
            onUnlocked = {},
            onRecoverySuccess = {},
        )

        viewModel.onScreenLoaded()
        // runCurrent (not advanceUntilIdle) so the 30s lockout ticker is not fast-forwarded to reset.
        runCurrent()

        assertEquals(PinEntryMode.LOCKOUT, viewModel.uiState.value.mode)
        assertFalse(viewModel.uiState.value.biometricShouldTrigger)
    }

    @Test
    fun onBiometricFailed_entersWrongPinMode() = runTest(dispatcher) {
        val lockout = FakeLockoutRepository()
        val viewModel = PinEntryViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(),
            lockoutRepository = lockout,
            nowEpochMillis = { 1_000L },
            scope = this,
            onUnlocked = {},
            onRecoverySuccess = {},
        )

        viewModel.onBiometricFailed()
        advanceUntilIdle()

        assertEquals(PinEntryMode.WRONG, viewModel.uiState.value.mode)
        assertEquals(1, lockout.failedAttempts)
    }

    @Test
    fun fiveWrongPins_triggersLockout() = runTest(dispatcher) {
        val lockout = FakeLockoutRepository()
        val viewModel = PinEntryViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(verifyPinResult = false),
            lockoutRepository = lockout,
            nowEpochMillis = { 1_000L },
            scope = this,
            onUnlocked = {},
            onRecoverySuccess = {},
        )

        repeat(5) {
            repeat(6) { viewModel.onDigit(1) }
            // runCurrent (not advanceUntilIdle) so the 30s lockout ticker is not fast-forwarded to reset.
            runCurrent()
        }

        assertEquals(PinEntryMode.LOCKOUT, viewModel.uiState.value.mode)
        assertEquals(5, lockout.failedAttempts)
    }

    @Test
    fun correctPin_unlocksAndResetsLockout() = runTest(dispatcher) {
        val lockout = FakeLockoutRepository()
        var unlocked = false
        val viewModel = PinEntryViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(verifyPinResult = true),
            lockoutRepository = lockout,
            nowEpochMillis = { 1_000L },
            scope = this,
            onUnlocked = { unlocked = true },
            onRecoverySuccess = {},
        )

        lockout.failedAttempts = 2
        repeat(6) { viewModel.onDigit(1) }
        advanceUntilIdle()

        assertTrue(unlocked)
        assertEquals(0, lockout.failedAttempts)
    }

    @Test
    fun onBiometricUnlock_whileLockedOut_isIgnored() = runTest(dispatcher) {
        var unlocked = false
        val viewModel = PinEntryViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(biometricEnrolled = true, biometricUnlock = true),
            lockoutRepository = FakeLockoutRepository(lockedOut = true, lockoutUntil = 20_000L),
            nowEpochMillis = { 1_000L },
            scope = this,
            onUnlocked = { unlocked = true },
            onRecoverySuccess = {},
        )

        viewModel.onBiometricUnlock()
        // runCurrent (not advanceUntilIdle) so the 30s lockout ticker is not fast-forwarded to reset.
        runCurrent()

        assertFalse(unlocked)
        assertEquals(PinEntryMode.LOCKOUT, viewModel.uiState.value.mode)
    }

    @Test
    fun onUnlockRecovery_successResetsLockout() = runTest(dispatcher) {
        val lockout = FakeLockoutRepository()
        var recovered = false
        val viewModel = PinEntryViewModel(
            pinAuthRepository = AuthFakePinAuthRepository(verifySecurityAnswer = true),
            lockoutRepository = lockout,
            nowEpochMillis = { 1_000L },
            scope = this,
            onUnlocked = {},
            onRecoverySuccess = { recovered = true },
        )

        lockout.failedAttempts = 3
        viewModel.onRecoveryAnswerChange("answer")
        viewModel.onUnlockRecovery()
        advanceUntilIdle()

        assertTrue(recovered)
        assertEquals(0, lockout.failedAttempts)
    }
}

private class FakeLockoutRepository(
    private var lockedOut: Boolean = false,
    private var lockoutUntil: Long? = null,
    var failedAttempts: Int = 0,
) : com.arduia.expense.data.LockoutRepository {
    override suspend fun getFailedAttemptCount(): Int = failedAttempts

    override suspend fun isLockedOut(nowEpochMillis: Long): Boolean = lockedOut

    override suspend fun getLockoutUntilEpochMillis(): Long? = lockoutUntil

    override suspend fun recordFailedAttempt(
        nowEpochMillis: Long,
        maxAttempts: Int,
        lockoutDurationMs: Long,
    ): LockoutState {
        if (lockedOut) {
            val seconds = ((lockoutUntil!! - nowEpochMillis) / 1000).toInt().coerceAtLeast(1)
            return LockoutState(isLockedOut = true, secondsRemaining = seconds, failedAttempts = failedAttempts)
        }
        failedAttempts += 1
        if (failedAttempts >= maxAttempts) {
            lockedOut = true
            lockoutUntil = nowEpochMillis + lockoutDurationMs
            return LockoutState(
                isLockedOut = true,
                secondsRemaining = (lockoutDurationMs / 1000).toInt(),
                failedAttempts = failedAttempts,
            )
        }
        return LockoutState(isLockedOut = false, secondsRemaining = 0, failedAttempts = failedAttempts)
    }

    override suspend fun resetLockout() {
        lockedOut = false
        lockoutUntil = null
        failedAttempts = 0
    }
}
