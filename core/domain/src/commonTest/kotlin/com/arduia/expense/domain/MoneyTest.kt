package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MoneyTest {
    private val usd = CurrencyCode("USD")
    private val eur = CurrencyCode("EUR")

    @Test
    fun `constructs and adds same currency`() {
        val sum = Money(Amount(100), usd) + Money(Amount(200), usd)
        assertEquals(Money(Amount(300), usd), sum)
    }

    @Test
    fun `zero carries the given currency`() {
        assertEquals(Money(Amount.ZERO, usd), Money.zero(usd))
    }

    @Test
    fun `rejects arithmetic across different currencies`() {
        assertFailsWith<IllegalArgumentException> {
            Money(Amount(100), usd) + Money(Amount(100), eur)
        }
        assertFailsWith<IllegalArgumentException> {
            Money(Amount(100), usd) - Money(Amount(100), eur)
        }
    }

    @Test
    fun `convertedTo applies the manual rate and swaps currency`() {
        val converted = Money(Amount(10_00), eur).convertedTo(usd, 1.08)
        assertEquals(Money(Amount(10_80), usd), converted)
    }

    @Test
    fun `convertedTo rounds to the nearest cent`() {
        val converted = Money(Amount(10_00), eur).convertedTo(usd, 1.085)
        assertEquals(Money(Amount(10_85), usd), converted)
    }

    @Test
    fun `convertedTo rejects a non-positive rate`() {
        assertFailsWith<IllegalArgumentException> {
            Money(Amount(10_00), eur).convertedTo(usd, 0.0)
        }
        assertFailsWith<IllegalArgumentException> {
            Money(Amount(10_00), eur).convertedTo(usd, -1.0)
        }
    }
}
