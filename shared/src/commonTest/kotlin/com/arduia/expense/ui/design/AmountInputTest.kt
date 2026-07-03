package com.arduia.expense.ui.design

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AmountInputTest {

    @Test
    fun applyDigit_buildsWholeAmount() {
        assertEquals("12", AmountInput.applyKey("", "1").let { AmountInput.applyKey(it, "2") })
    }

    @Test
    fun applyDecimal_insertsSinglePoint() {
        assertEquals("0.", AmountInput.applyKey("", "."))
        assertEquals("12.5", AmountInput.applyKey("12.", "5"))
        assertEquals("12.5", AmountInput.applyKey("12.5", "."))
    }

    @Test
    fun canProceed_requiresPositiveValue() {
        assertFalse(AmountInput.canProceed(""))
        assertFalse(AmountInput.canProceed("0"))
        assertTrue(AmountInput.canProceed("12.50"))
    }

    @Test
    fun formatDisplay_groupsThousands() {
        assertEquals("1,234.50", AmountInput.formatDisplay("1234.50"))
    }

    @Test
    fun formatMoney_dropsFractionWhenWhole() {
        assertEquals("22", AmountInput.formatMoney(2200))
        assertEquals("0", AmountInput.formatMoney(0))
    }

    @Test
    fun formatMoney_keepsTwoDigitFractionWhenNonZero() {
        assertEquals("22.50", AmountInput.formatMoney(2250))
        assertEquals("22.05", AmountInput.formatMoney(2205))
    }

    @Test
    fun formatMoney_groupsThousandsOnWholeAndFractional() {
        assertEquals("1,234", AmountInput.formatMoney(123_400))
        assertEquals("1,234.56", AmountInput.formatMoney(123_456))
    }

    @Test
    fun formatMoney_withSymbol_prefixesSymbol() {
        assertEquals("$22", AmountInput.formatMoney(2200, "$"))
        assertEquals("$22.50", AmountInput.formatMoney(2250, "$"))
    }

    @Test
    fun formatMoneySigned_prependsMinusBeforeSymbol() {
        assertEquals("-$22.50", AmountInput.formatMoneySigned(-2250, "$"))
        assertEquals("$22.50", AmountInput.formatMoneySigned(2250, "$"))
        assertEquals("$0", AmountInput.formatMoneySigned(0, "$"))
    }
}
