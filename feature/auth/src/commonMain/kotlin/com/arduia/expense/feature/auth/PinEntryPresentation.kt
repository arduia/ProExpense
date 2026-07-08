package com.arduia.expense.feature.auth

enum class PinEntryMode { Default, Error, Locked }

data class PinEntryUiState(
    val filledDots: Int,
    val mode: PinEntryMode = PinEntryMode.Default,
    val countdownLabel: String? = null,
    val showBiometric: Boolean = true,
    val digits: String = "",
)

/**
 * Platform-agnostic PIN keypad presentation rules shared by every PIN flow (unlock, verify,
 * setup, change, recovery reset) so Android and iOS render identical behavior.
 */
object PinEntryLogic {
    const val PIN_LENGTH = 6

    sealed interface DigitResult {
        data class Updated(val buffer: String) : DigitResult
        data class Completed(val pin: String) : DigitResult
    }

    /**
     * A digit typed while the previous attempt's error is still showing starts a fresh buffer —
     * the error state consumes the old digits, matching the reference PIN screens.
     */
    fun appendDigit(buffer: String, digit: Int, hadError: Boolean = false): DigitResult {
        val base = if (hadError) "" else buffer
        if (base.length >= PIN_LENGTH) return DigitResult.Updated(base)
        val updated = base + digit
        return if (updated.length == PIN_LENGTH) {
            DigitResult.Completed(updated)
        } else {
            DigitResult.Updated(updated)
        }
    }

    fun backspace(buffer: String): String = buffer.dropLast(1)

    /** Lockout takes precedence over a wrong-PIN error when both apply. */
    fun entryMode(lockedOut: Boolean, error: Boolean): PinEntryMode = when {
        lockedOut -> PinEntryMode.Locked
        error -> PinEntryMode.Error
        else -> PinEntryMode.Default
    }

    /** "m:ss" label for the lockout countdown, e.g. 65_000ms -> "1:05". Clamps at "0:00". */
    fun countdownLabel(remainingMs: Long): String {
        val totalSeconds = (remainingMs / 1000).coerceAtLeast(0)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "$minutes:${seconds.toString().padStart(2, '0')}"
    }
}
