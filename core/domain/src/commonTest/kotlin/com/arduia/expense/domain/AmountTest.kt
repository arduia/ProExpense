package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AmountTest {

    @Test
    fun `constructs with valid cents`() {
        val amount = Amount(1000)
        assertEquals(1000, amount.valueInCents)
    }

    @Test
    fun `plus and minus stay within range`() {
        val sum = Amount(100) + Amount(200)
        assertEquals(300, sum.valueInCents)
        val diff = Amount(300) - Amount(200)
        assertEquals(100, diff.valueInCents)
    }

    @Test
    fun `compareTo orders by cents`() {
        assertTrue(Amount(100) < Amount(200))
        assertTrue(Amount(200) > Amount(100))
        assertEquals(0, Amount(100).compareTo(Amount(100)))
    }

    @Test
    fun `rejects negative cents`() {
        assertFailsWith<IllegalArgumentException> { Amount(-1) }
    }

    @Test
    fun `rejects cents above max value`() {
        assertFailsWith<IllegalArgumentException> { Amount(Amount.MAX_VALUE_IN_CENTS + 1) }
    }

    @Test
    fun `accepts max value boundary`() {
        val amount = Amount(Amount.MAX_VALUE_IN_CENTS)
        assertEquals(Amount.MAX_VALUE_IN_CENTS, amount.valueInCents)
    }
}
