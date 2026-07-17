package com.arduia.expense.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecordKindTest {
    @Test
    fun isVisibleInHomeRecents_plainExpenseAndIncomeAreAlwaysVisible() {
        assertTrue(RecordKind.EXPENSE.isVisibleInHomeRecents())
        assertTrue(RecordKind.INCOME.isVisibleInHomeRecents())
    }

    @Test
    fun isVisibleInHomeRecents_toggleOnDebtIsAlwaysVisible() {
        // A DEBT_LENT/DEBT_OWED RecordKind only ever arises from a real, linked FinanceRecord
        // (see FinanceRecord.kind()) — that only exists when Debt.recordAsTransaction is true, so
        // it's already counted toward spend/income totals and belongs in the feed like any other
        // transaction, independent of SHOW_SPLIT_AND_DEBT_ROWS.
        assertTrue(RecordKind.DEBT_LENT.isVisibleInHomeRecents())
        assertTrue(RecordKind.DEBT_OWED.isVisibleInHomeRecents())
    }

    @Test
    fun isVisibleInHomeRecents_toggleOnSplitIsAlwaysVisible() {
        // A SPLIT RecordKind only ever arises from a real, linked FinanceRecord (see
        // FinanceRecord.kind()) — that only exists when SharedCost.recordAsTransaction is true, so
        // it's already counted toward spend totals and belongs in the feed like any other
        // transaction, independent of SHOW_SPLIT_AND_DEBT_ROWS (which only gates toggle-off debts).
        assertTrue(RecordKind.SPLIT.isVisibleInHomeRecents())
    }

    @Test
    fun debtRowTitle_lentPrefixesWithLentTo() {
        assertEquals("Lent to Alex", debtRowTitle("Alex", isLent = true))
    }

    @Test
    fun debtRowTitle_owePrefixesWithOwe() {
        assertEquals("Owe Alex", debtRowTitle("Alex", isLent = false))
    }

    @Test
    fun splitRowTitle_blankTitleFallsBackToDefault() {
        assertEquals(SPLIT_ROW_SUBTITLE_TYPE, splitRowTitle(null))
        assertEquals(SPLIT_ROW_SUBTITLE_TYPE, splitRowTitle("   "))
    }

    @Test
    fun splitRowTitle_namedTitleGetsSplitPrefix() {
        assertEquals("Split · Dinner", splitRowTitle("Dinner"))
    }
}
