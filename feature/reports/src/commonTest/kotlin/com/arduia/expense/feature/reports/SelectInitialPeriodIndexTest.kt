package com.arduia.expense.feature.reports

import kotlin.test.Test
import kotlin.test.assertEquals

class SelectInitialPeriodIndexTest {

    @Test
    fun invoke_returnsZeroWhenCurrentMonthHasData() {
        val index = selectInitialPeriodIndex(listOf(false, false, true))

        assertEquals(0, index)
    }

    @Test
    fun invoke_skipsToFirstNonEmptyMonthWhenCurrentMonthIsEmpty() {
        val index = selectInitialPeriodIndex(listOf(true, true, false, true))

        assertEquals(2, index)
    }

    @Test
    fun invoke_returnsZeroWhenEveryMonthIsEmpty() {
        val index = selectInitialPeriodIndex(listOf(true, true, true))

        assertEquals(0, index)
    }

    @Test
    fun invoke_returnsZeroForEmptyList() {
        val index = selectInitialPeriodIndex(emptyList())

        assertEquals(0, index)
    }
}
