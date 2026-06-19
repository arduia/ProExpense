package com.arduia.expense.feature.sharedcost

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SplitCalculatorTest {
    @Test
    fun calculateEvenSplit_distributesRemainderToFirstParticipants() {
        val shares = calculateEvenSplit(totalCents = 100, peopleCount = 3)
        assertEquals(listOf(34L, 33L, 33L), shares)
    }

    @Test
    fun validateCustomSplit_matchesTotal_returnsNull() {
        assertNull(validateCustomSplit(totalCents = 100, customShares = listOf(50, 50)))
    }

    @Test
    fun validateCustomSplit_overTotal_returnsMessage() {
        val message = validateCustomSplit(totalCents = 100, customShares = listOf(60, 50))
        assertEquals("Amounts are $10 over the total", message)
    }
}
