package com.arduia.expense.ui

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * US-MORE-2: monthly budget drives the Home Budget-Planner header and resets automatically each
 * month. [computeBudgetSummary] gives the reset "for free" by filtering to the current calendar
 * month — these tests pin an explicit [Calendar] to prove that filter, not real wall-clock time.
 */
class ComputeBudgetSummaryTest {
    private fun calendarFor(
        year: Int,
        month: Int,
        day: Int,
    ) = Calendar.getInstance().apply {
        set(year, month, day, 12, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun record(
        id: String,
        amountCents: Long,
        recordedAt: Calendar,
        type: RecordType = RecordType.EXPENSE,
    ) = FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(amountCents), CurrencyCode("USD")),
        homeCurrencyMoney = Money(Amount(amountCents), CurrencyCode("USD")),
        categoryId = CategoryId("food"),
        type = type,
        note = null,
        recordedAtEpochMillis = recordedAt.timeInMillis,
    )

    @Test
    fun returnsNull_whenNoBudgetSet() {
        val summary = computeBudgetSummary(records = emptyList(), monthlyBudget = null, homeSymbol = "$")
        assertNull(summary)
    }

    @Test
    fun onTrack_whenSpendUnderBudget() {
        val now = calendarFor(2026, Calendar.JULY, 15)
        val records =
            listOf(
                record("r1", 3_000, calendarFor(2026, Calendar.JULY, 5)),
                record("r2", 2_000, calendarFor(2026, Calendar.JULY, 10)),
            )

        val summary = computeBudgetSummary(records, Money(Amount(10_000), CurrencyCode("USD")), "$", now)

        requireNotNull(summary)
        assertEquals("$50", summary.spentLabel)
        assertEquals("of $100", summary.budgetLabel)
        assertEquals(0.5f, summary.progress, 0.001f)
        assertEquals("On track", summary.statusLabel)
        assertFalse(summary.isOverBudget)
    }

    @Test
    fun overBudget_whenSpendExceedsBudget() {
        val now = calendarFor(2026, Calendar.JULY, 20)
        val records = listOf(record("r1", 15_000, calendarFor(2026, Calendar.JULY, 3)))

        val summary = computeBudgetSummary(records, Money(Amount(10_000), CurrencyCode("USD")), "$", now)

        requireNotNull(summary)
        assertEquals("Over budget", summary.statusLabel)
        assertTrue(summary.isOverBudget)
    }

    @Test
    fun incomeRecords_neverCountTowardSpend() {
        val now = calendarFor(2026, Calendar.JULY, 15)
        val records =
            listOf(
                record("r1", 3_000, calendarFor(2026, Calendar.JULY, 5)),
                record("r2", 50_000, calendarFor(2026, Calendar.JULY, 6), type = RecordType.INCOME),
            )

        val summary = computeBudgetSummary(records, Money(Amount(10_000), CurrencyCode("USD")), "$", now)

        requireNotNull(summary)
        assertEquals("$30", summary.spentLabel)
    }

    @Test
    fun resetsAtStartOfMonth_previousMonthRecordsExcluded() {
        val nowInAugust = calendarFor(2026, Calendar.AUGUST, 2)
        val records =
            listOf(
                // Heavy spend in July must not bleed into August's progress.
                record("r1", 90_000, calendarFor(2026, Calendar.JULY, 28)),
            )

        val summary = computeBudgetSummary(records, Money(Amount(10_000), CurrencyCode("USD")), "$", nowInAugust)

        requireNotNull(summary)
        assertEquals("$0", summary.spentLabel)
        assertEquals("On track", summary.statusLabel)
    }

    @Test
    fun budgetFigureItself_isUnaffectedByMonthChange() {
        val budget = Money(Amount(25_000), CurrencyCode("USD"))
        val julySummary = computeBudgetSummary(emptyList(), budget, "$", calendarFor(2026, Calendar.JULY, 15))
        val augustSummary = computeBudgetSummary(emptyList(), budget, "$", calendarFor(2026, Calendar.AUGUST, 15))

        requireNotNull(julySummary)
        requireNotNull(augustSummary)
        assertEquals("of $250", julySummary.budgetLabel)
        assertEquals(julySummary.budgetLabel, augustSummary.budgetLabel)
    }
}
