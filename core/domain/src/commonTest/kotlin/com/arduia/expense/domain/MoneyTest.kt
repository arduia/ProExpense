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
}
