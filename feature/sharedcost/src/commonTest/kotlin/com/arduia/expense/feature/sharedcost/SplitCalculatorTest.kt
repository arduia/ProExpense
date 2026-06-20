/**
 * Domain rules (design plan D11):
 * - Even split: remainder cents go to earliest participants.
 * - Custom split validation reports over/under total in dollars.
 */
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
    fun calculateEvenSplit_singlePerson_getsFullTotal() {
        assertEquals(listOf(500L), calculateEvenSplit(totalCents = 500, peopleCount = 1))
    }

    @Test
    fun validateCustomSplit_matchesTotal_returnsNull() {
        assertNull(validateCustomSplit(totalCents = 100, customShares = listOf(50, 50)))
    }

    @Test
    fun validateCustomSplit_overTotal_returnsMessage() {
        val message = validateCustomSplit(totalCents = 10000, customShares = listOf(6000, 5000))
        assertEquals("Amounts are $10 over the total", message)
    }

    @Test
    fun validateCustomSplit_underTotal_returnsMessage() {
        val message = validateCustomSplit(totalCents = 10000, customShares = listOf(4000, 4000))
        assertEquals("Amounts are $20 short of the total", message)
    }
}
