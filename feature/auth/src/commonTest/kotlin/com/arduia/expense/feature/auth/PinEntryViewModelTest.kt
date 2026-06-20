package com.arduia.expense.feature.auth

import com.arduia.expense.data.LockoutState
import com.arduia.expense.data.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PinEntryViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onScreenLoaded_setsBiometricTriggerWhenEnrolled() = runTest(dispatcher) {
        val viewModel = PinEntryViewModel(
            pinAuthRepository = FakePinAuthRepository(biometricEnrolled = true),
            lockoutRepository = FakeLockoutRepository(),
            nowEpochMillis = { 1_000L },
            scope = scope.backgroundScope,
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
            pinAuthRepository = FakePinAuthRepository(biometricEnrolled = true),
            lockoutRepository = FakeLockoutRepository(lockedOut = true, lockoutUntil = 10_000L),
            nowEpochMillis = { 1_000L },
            scope = scope.backgroundScope,
            onUnlocked = {},
            onRecoverySuccess = {},
        )

        viewModel.onScreenLoaded()
        advanceUntilIdle()

        assertEquals(PinEntryMode.LOCKOUT, viewModel.uiState.value.mode)
        assertEquals(false, viewModel.uiState.value.biometricShouldTrigger)
    }

    @Test
    fun onBiometricFailed_entersWrongPinMode() = runTest(dispatcher) {
        val lockout = FakeLockoutRepository()
        val viewModel = PinEntryViewModel(
            pinAuthRepository = FakePinAuthRepository(),
            lockoutRepository = lockout,
            nowEpochMillis = { 1_000L },
            scope = scope.backgroundScope,
            onUnlocked = {},
            onRecoverySuccess = {},
        )

        viewModel.onBiometricFailed()
        advanceUntilIdle()

        assertEquals(PinEntryMode.WRONG, viewModel.uiState.value.mode)
        assertEquals(1, lockout.failedAttempts)
    }
}

private class FakePinAuthRepository(
    private val biometricEnrolled: Boolean = false,
) : PinAuthRepository {
    override suspend fun isPinConfigured(): Result<Boolean> = Result.Success(true)

    override suspend fun setPin(pin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifyPin(pin: String): Result<Boolean> = Result.Success(false)

    override suspend fun clearPin(): Result<Unit> = Result.Success(Unit)

    override suspend fun changePin(newPin: String): Result<Unit> = Result.Success(Unit)

    override suspend fun setSecurityAnswer(answer: String): Result<Unit> = Result.Success(Unit)

    override suspend fun verifySecurityAnswer(answer: String): Result<Boolean> = Result.Success(false)

    override suspend fun isBiometricEnrolled(): Boolean = biometricEnrolled

    override suspend fun setBiometricEnrolled(enabled: Boolean): Result<Unit> = Result.Success(Unit)
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
        failedAttempts += 1
        if (failedAttempts >= maxAttempts) {
            lockedOut = true
            lockoutUntil = nowEpochMillis + lockoutDurationMs
            return LockoutState(isLockedOut = true, secondsRemaining = (lockoutDurationMs / 1000).toInt())
        }
        return LockoutState(isLockedOut = false, secondsRemaining = 0)
    }

    override suspend fun resetLockout() {
        lockedOut = false
        lockoutUntil = null
        failedAttempts = 0
    }
}
