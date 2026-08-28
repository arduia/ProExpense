package com.arduia.expense.shell

import com.arduia.expense.feature.auth.PinEntryLogic
import com.arduia.expense.feature.auth.PinEntryMode
import com.arduia.expense.feature.auth.VerifyPinUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Backbone coverage for the unlock gate's keypad.
 *
 * Traceability: US-AUTH-2 Scenario 2 (correct PIN unlocks), Scenario 3 (incorrect PIN shows the
 * error and clears the dots), and US-AUTH-3 (lockout after repeated failures, with a countdown).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PinEntryViewModelTest {
    /**
     * The clock is driven by the scheduler's virtual time, so the countdown's `delay` actually
     * advances it — against a real clock the tick loop would spin for the whole lockout.
     */
    private fun TestScope.viewModel(repository: FakePinAuth): PinEntryViewModel =
        PinEntryViewModel(
            verifyPin = VerifyPinUseCase(repository),
            pinAuthRepository = repository,
            nowEpochMillis = { testScheduler.currentTime },
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    private fun PinEntryViewModel.enter(pin: String) {
        pin.forEach { onDigit(it.digitToInt()) }
    }

    @Test
    fun `opens in the default state rather than pre-showing an error`() =
        runTest {
            val vm = viewModel(FakePinAuth(correctPin = "123456"))

            advanceUntilIdle()

            assertEquals(PinEntryMode.Default, vm.uiState.value.mode)
            assertNull(vm.uiState.value.countdownLabel)
        }

    @Test
    fun `entering the correct pin unlocks`() =
        runTest {
            val vm = viewModel(FakePinAuth(correctPin = "123456"))
            advanceUntilIdle()

            vm.enter("123456")
            advanceUntilIdle()

            assertTrue(vm.uiState.value.unlocked)
        }

    @Test
    fun `an incorrect pin clears the dots and shows the error state`() =
        runTest {
            val vm = viewModel(FakePinAuth(correctPin = "123456"))
            advanceUntilIdle()

            vm.enter("000000")
            advanceUntilIdle()

            assertFalse(vm.uiState.value.unlocked)
            assertEquals(0, vm.uiState.value.filledDots)
            assertEquals(PinEntryMode.Error, vm.uiState.value.mode)
        }

    @Test
    fun `typing after an error starts a fresh buffer rather than appending`() =
        runTest {
            val vm = viewModel(FakePinAuth(correctPin = "123456"))
            advanceUntilIdle()
            vm.enter("000000")
            advanceUntilIdle()

            vm.onDigit(9)

            assertEquals(1, vm.uiState.value.filledDots)
            assertEquals(PinEntryMode.Default, vm.uiState.value.mode)
        }

    @Test
    fun `a lockout disables entry and surfaces a countdown`() =
        runTest {
            // Relative to the scheduler's virtual clock, which starts at 0.
            val vm = viewModel(FakePinAuth(correctPin = "123456", lockoutUntilMs = 30_000L))

            runCurrent()

            assertTrue(vm.uiState.value.isLockedOut)
            assertEquals("0:30", vm.uiState.value.countdownLabel)
        }

    @Test
    fun `entry becomes usable again once the lockout expires`() =
        runTest {
            val vm = viewModel(FakePinAuth(correctPin = "123456", lockoutUntilMs = 30_000L))
            runCurrent()

            advanceTimeBy(30_001)
            runCurrent()

            assertFalse(vm.uiState.value.isLockedOut)
            assertNull(vm.uiState.value.countdownLabel)
        }

    @Test
    fun `backspace removes the last digit`() =
        runTest {
            val vm = viewModel(FakePinAuth(correctPin = "123456"))
            advanceUntilIdle()

            vm.enter("123")
            vm.onBackspace()

            assertEquals(2, vm.uiState.value.filledDots)
        }

    @Test
    fun `pin length matches the shared keypad contract`() =
        runTest {
            val vm = viewModel(FakePinAuth(correctPin = "123456"))

            assertEquals(PinEntryLogic.PIN_LENGTH, vm.uiState.value.pinLength)
        }
}
