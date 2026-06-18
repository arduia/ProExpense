package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormattingTest {
    @Test
    fun formatWithSymbol_includesFractionWhenNeeded() {
        assertEquals("$12.50", Amount(1250).formatWithSymbol("USD"))
        assertEquals("$12", Amount(1200).formatWithSymbol("USD"))
    }
}
