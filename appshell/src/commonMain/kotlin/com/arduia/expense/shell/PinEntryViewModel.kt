package com.arduia.expense.shell

import com.arduia.expense.data.Result
import com.arduia.expense.feature.auth.PinAuthRepository
import com.arduia.expense.feature.auth.PinEntryLogic
import com.arduia.expense.feature.auth.PinEntryMode
import com.arduia.expense.feature.auth.VerifyPinResult
import com.arduia.expense.feature.auth.VerifyPinUseCase
import com.arduia.expense.shared.StatefulViewModel
import com.arduia.expense.shared.currentEpochMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val COUNTDOWN_TICK_MILLIS = 500L

data class PinEntryScreenState(
    val digits: String = "",
    val mode: PinEntryMode = PinEntryMode.Default,
    val countdownLabel: String? = null,
    val unlocked: Boolean = false,
    val errorMessage: String? = null,
) {
    val filledDots: Int get() = digits.length

    val pinLength: Int get() = PinEntryLogic.PIN_LENGTH

    val isLockedOut: Boolean get() = mode == PinEntryMode.Locked
}

/**
 * 15 · PIN Entry — unlock gate.
 *
 * All keypad rules (buffer handling, error-consumes-digits, lockout precedence, countdown label)
 * come from the already-shared [PinEntryLogic]; this ViewModel only sequences them against
 * [VerifyPinUseCase] and drives the lockout countdown tick.
 */
class PinEntryViewModel(
    private val verifyPin: VerifyPinUseCase,
    private val pinAuthRepository: PinAuthRepository,
    /** Injected like the feature use cases' `nowEpochMillis` so the countdown is testable — the
     *  tick loop reads it every pass, and against the real clock under a virtual dispatcher it
     *  would otherwise busy-spin for the full lockout duration. */
    private val nowEpochMillis: () -> Long = ::currentEpochMillis,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : StatefulViewModel<PinEntryScreenState>(PinEntryScreenState(), dispatcher) {
    private var lockoutUntilMs: Long? = null

    init {
        viewModelScope.launch { refreshLockout() }
    }

    fun onDigit(digit: Int) {
        val state = currentState()
        if (state.isLockedOut) return
        val hadError = state.mode == PinEntryMode.Error
        when (val result = PinEntryLogic.appendDigit(state.digits, digit, hadError)) {
            is PinEntryLogic.DigitResult.Updated ->
                setState { it.copy(digits = result.buffer, mode = PinEntryMode.Default, errorMessage = null) }
            is PinEntryLogic.DigitResult.Completed -> {
                setState { it.copy(digits = result.pin, mode = PinEntryMode.Default, errorMessage = null) }
                viewModelScope.launch { submit(result.pin) }
            }
        }
    }

    fun onBackspace() {
        setState { it.copy(digits = PinEntryLogic.backspace(it.digits), errorMessage = null) }
    }

    private suspend fun submit(pin: String) {
        when (val result = verifyPin(pin)) {
            is VerifyPinResult.Unlocked -> {
                lockoutUntilMs = null
                setState { it.copy(unlocked = true, digits = "", mode = PinEntryMode.Default, countdownLabel = null) }
            }
            is VerifyPinResult.Incorrect -> {
                lockoutUntilMs = result.lockoutUntilMs
                applyLockoutState(clearDigits = true)
                startCountdownIfLocked()
            }
            is VerifyPinResult.Error ->
                setState {
                    it.copy(digits = "", mode = PinEntryMode.Error, errorMessage = result.message)
                }
        }
    }

    private suspend fun refreshLockout() {
        lockoutUntilMs = (pinAuthRepository.getLockoutUntilMs() as? Result.Success)?.data
        applyLockoutState(clearDigits = false)
        startCountdownIfLocked()
    }

    private fun applyLockoutState(clearDigits: Boolean) {
        val remaining = remainingLockoutMs()
        val locked = remaining > 0
        setState {
            it.copy(
                digits = if (clearDigits) "" else it.digits,
                mode = PinEntryLogic.entryMode(lockedOut = locked, error = !locked),
                countdownLabel = if (locked) PinEntryLogic.countdownLabel(remaining) else null,
            )
        }
    }

    private fun startCountdownIfLocked() {
        if (remainingLockoutMs() <= 0) return
        viewModelScope.launch {
            while (remainingLockoutMs() > 0) {
                setState { it.copy(countdownLabel = PinEntryLogic.countdownLabel(remainingLockoutMs())) }
                delay(COUNTDOWN_TICK_MILLIS)
            }
            // Lockout expired — drop back to a plain entry state so the keypad is usable again.
            setState { it.copy(mode = PinEntryMode.Default, countdownLabel = null) }
        }
    }

    private fun remainingLockoutMs(): Long = ((lockoutUntilMs ?: 0L) - nowEpochMillis()).coerceAtLeast(0L)
}
