package com.arduia.expense.feature.logging

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AmountInputLogicTest {
    @Test
    fun applyKey_appendsDigitsUntilDecimalLimit() {
        assertEquals("12.5", AmountInputLogic.applyKey("12.", "5"))
        assertEquals("12.", AmountInputLogic.applyKey("12.", "6"))
    }

    @Test
    fun canProceed_requiresPositiveAmount() {
        assertFalse(AmountInputLogic.canProceed(""))
        assertFalse(AmountInputLogic.canProceed("0"))
        assertTrue(AmountInputLogic.canProceed("0.01"))
    }

    @Test
    fun toCents_parsesDecimalInput() {
        assertEquals(1250L, AmountInputLogic.toCents("12.5"))
    }
}
