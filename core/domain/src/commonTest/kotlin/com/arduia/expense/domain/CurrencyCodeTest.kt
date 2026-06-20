/**
 * Domain rules (PRD multi-currency):
 * - Currency codes must be 3-letter uppercase ISO strings (e.g. USD, EUR).
 */
package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CurrencyCodeTest {
    @Test
    fun acceptsValidIsoCode() {
        assertEquals("USD", CurrencyCode("USD").code)
    }

    @Test
    fun rejectsLowercaseCode() {
        assertFailsWith<IllegalArgumentException> { CurrencyCode("usd") }
    }

    @Test
    fun rejectsShortCode() {
        assertFailsWith<IllegalArgumentException> { CurrencyCode("US") }
    }

    @Test
    fun rejectsLongCode() {
        assertFailsWith<IllegalArgumentException> { CurrencyCode("USDX") }
    }
}
