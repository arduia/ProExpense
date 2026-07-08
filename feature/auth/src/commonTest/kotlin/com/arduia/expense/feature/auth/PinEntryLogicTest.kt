package com.arduia.expense.feature.auth

import kotlin.test.Test
import kotlin.test.assertEquals

class PinEntryLogicTest {

    // Rule: digits accumulate until PIN_LENGTH, then the entry completes with the full PIN.
    @Test
    fun appendDigit_completesAtPinLength() {
        var buffer = ""
        repeat(5) { index ->
            val result = PinEntryLogic.appendDigit(buffer, index)
            buffer = (result as PinEntryLogic.DigitResult.Updated).buffer
        }
        assertEquals("01234", buffer)

        val completed = PinEntryLogic.appendDigit(buffer, 5)
        assertEquals(PinEntryLogic.DigitResult.Completed("012345"), completed)
    }

    // Rule: a digit typed while the previous attempt's error shows starts a fresh buffer.
    @Test
    fun appendDigit_afterError_startsFreshBuffer() {
        val result = PinEntryLogic.appendDigit("123456", digit = 9, hadError = true)
        assertEquals(PinEntryLogic.DigitResult.Updated("9"), result)
    }

    // Invariant: the buffer never grows past PIN_LENGTH (e.g. taps landing while verify is in flight).
    @Test
    fun appendDigit_atFullBuffer_ignoresExtraDigits() {
        val result = PinEntryLogic.appendDigit("123456", digit = 7)
        assertEquals(PinEntryLogic.DigitResult.Updated("123456"), result)
    }

    // Rule: backspace removes the last digit; failure mode: empty buffer stays empty.
    @Test
    fun backspace_dropsLastDigit_andHandlesEmpty() {
        assertEquals("12", PinEntryLogic.backspace("123"))
        assertEquals("", PinEntryLogic.backspace(""))
    }

    // Rule: lockout takes precedence over a wrong-PIN error.
    @Test
    fun entryMode_lockoutWinsOverError() {
        assertEquals(PinEntryMode.Locked, PinEntryLogic.entryMode(lockedOut = true, error = true))
        assertEquals(PinEntryMode.Error, PinEntryLogic.entryMode(lockedOut = false, error = true))
        assertEquals(PinEntryMode.Default, PinEntryLogic.entryMode(lockedOut = false, error = false))
    }

    // Rule: countdown renders m:ss with zero-padded seconds; failure mode: never below "0:00".
    @Test
    fun countdownLabel_formatsMinutesAndSeconds() {
        assertEquals("1:05", PinEntryLogic.countdownLabel(65_000))
        assertEquals("0:30", PinEntryLogic.countdownLabel(30_000))
        assertEquals("5:00", PinEntryLogic.countdownLabel(300_000))
        assertEquals("0:00", PinEntryLogic.countdownLabel(-1_000))
    }
}
