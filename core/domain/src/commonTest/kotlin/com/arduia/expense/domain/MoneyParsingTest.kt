/**
 * Domain rules (money parsing):
 * - Decimal strings convert to integer cents without floating-point rounding error.
 * - Fraction truncated to two places; missing fraction padded.
 * - Malformed input returns null.
 */
package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MoneyParsingTest {
    @Test
    fun parsesFloatUnsafeDecimalsExactly() {
        // (4.35 * 100).toLong() == 434 — the bug this guards against.
        assertEquals(435L, parseAmountToCents("4.35"))
        assertEquals(29L, parseAmountToCents("0.29"))
        assertEquals(1250L, parseAmountToCents("12.5"))
    }

    @Test
    fun padsAndTruncatesFraction() {
        assertEquals(1200L, parseAmountToCents("12"))
        assertEquals(1200L, parseAmountToCents("12."))
        assertEquals(50L, parseAmountToCents(".5"))
        assertEquals(199L, parseAmountToCents("1.999"))
    }

    @Test
    fun parsesMaxProductAmount() {
        assertEquals(Amount.MAX_VALUE_IN_CENTS, parseAmountToCents("999999999.99"))
    }

    @Test
    fun returnsNullForMalformedInput() {
        assertNull(parseAmountToCents(""))
        assertNull(parseAmountToCents("  "))
        assertNull(parseAmountToCents("12.3.4"))
        assertNull(parseAmountToCents("abc"))
        assertNull(parseAmountToCents("-5"))
    }
}
