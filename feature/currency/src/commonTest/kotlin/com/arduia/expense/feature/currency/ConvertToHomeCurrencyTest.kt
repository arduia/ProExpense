/**
 * Domain rules (PRD multi-currency):
 * - Manual exchange rate converts foreign amount to home currency (cents × rate).
 * - Exchange rate must be strictly positive.
 */
package com.arduia.expense.feature.currency

import com.arduia.expense.domain.Amount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConvertToHomeCurrencyTest {
    @Test
    fun convertToHomeCurrency_appliesRate() {
        val home = convertToHomeCurrency(Amount(10000), exchangeRate = 1.5)
        assertEquals(15000L, home.valueInCents)
    }

    @Test
    fun convertToHomeCurrency_rejectsNonPositiveRate() {
        assertFailsWith<IllegalArgumentException> {
            convertToHomeCurrency(Amount(100), exchangeRate = 0.0)
        }
        assertFailsWith<IllegalArgumentException> {
            convertToHomeCurrency(Amount(100), exchangeRate = -1.0)
        }
    }
}
