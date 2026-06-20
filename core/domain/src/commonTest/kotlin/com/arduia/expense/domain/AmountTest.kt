/**
 * Domain rules (PRD / product guards):
 * - Amount stored as integer cents (×100).
 * - Valid range: 0 … 999,999,999.99 (MAX_VALUE_IN_CENTS).
 * - Negative and above-cap values are rejected at construction.
 */
package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AmountTest {

    @Test
    fun acceptsValidAmount() {
        val amount = Amount(1_500)
        assertEquals(1_500, amount.valueInCents)
    }

    @Test
    fun acceptsZeroAmount() {
        assertEquals(0L, Amount(0).valueInCents)
    }

    @Test
    fun acceptsMaxProductCap() {
        assertEquals(Amount.MAX_VALUE_IN_CENTS, Amount(Amount.MAX_VALUE_IN_CENTS).valueInCents)
    }

    @Test
    fun rejectsNegativeAmount() {
        assertFailsWith<IllegalArgumentException> {
            Amount(-1)
        }
    }

    @Test
    fun rejectsAmountAboveCap() {
        assertFailsWith<IllegalArgumentException> {
            Amount(Amount.MAX_VALUE_IN_CENTS + 1)
        }
    }
}
