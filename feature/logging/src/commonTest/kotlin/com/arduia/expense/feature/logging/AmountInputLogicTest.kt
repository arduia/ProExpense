/**
 * Domain rules (quick log / PRD):
 * - Max 9 digits before decimal, 2 decimal places (product cap alignment).
 * - canProceed requires amount > 0.
 * - toCents converts decimal string to integer cents.
 */
package com.arduia.expense.feature.logging

import com.arduia.expense.domain.Amount
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AmountInputLogicTest {
    @Test
    fun applyKey_appendsDigitsUntilDecimalLimit() {
        assertEquals("12.5", AmountInputLogic.applyKey("12.", "5"))
        // Two decimal places are allowed (MAX_DECIMAL_PLACES = 2).
        assertEquals("12.56", AmountInputLogic.applyKey("12.5", "6"))
        // A third decimal place is rejected.
        assertEquals("12.56", AmountInputLogic.applyKey("12.56", "7"))
    }

    @Test
    fun applyKey_blocksMoreThanNineDigitsBeforeDecimal() {
        val nineDigits = "123456789"
        assertEquals(nineDigits, AmountInputLogic.applyKey(nineDigits.dropLast(1), "9"))
        assertEquals(nineDigits, AmountInputLogic.applyKey(nineDigits, "0"))
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

    @Test
    fun toCents_parsesMaxProductAmount() {
        val cents = AmountInputLogic.toCents("999999999.99")
        assertEquals(Amount.MAX_VALUE_IN_CENTS, cents)
    }
}
