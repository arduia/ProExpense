package com.arduia.expense.feature.history

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JournalGroupingTest {
    private val formatter = FakeRecordDateFormatter()
    private val categoryLabel: (String) -> String = { id -> id.replaceFirstChar { it.uppercase() } }

    @Test
    fun groupRecordsByDay_groupsByExpenseDate() {
        val dayOne = 1_700_000_000_000L
        val dayTwo = dayOne - 86_400_000L
        val records = listOf(
            sampleRecord("1", dayOne, 500),
            sampleRecord("2", dayOne, 300),
            sampleRecord("3", dayTwo, 200),
        )

        val groups = groupRecordsByDay(
            records = records,
            formatter = formatter,
            homeCurrencyCode = "USD",
            categoryLabel = categoryLabel,
        )

        assertEquals(2, groups.size)
        assertEquals(2, groups.first().transactions.size)
        assertEquals("$8.00", groups.first().dayTotal)
    }

    @Test
    fun groupRecordsByDay_recentLimit_capsEntries() {
        val dayOne = 1_700_000_000_000L
        val records = (1..5).map { index ->
            sampleRecord(index.toString(), dayOne - index, index * 100L)
        }

        val groups = groupRecordsByDay(
            records = records,
            formatter = formatter,
            homeCurrencyCode = "USD",
            categoryLabel = categoryLabel,
            recentLimit = 2,
        )

        assertEquals(1, groups.size)
        assertEquals(2, groups.first().transactions.size)
    }

    private fun sampleRecord(id: String, epochMillis: Long, cents: Long): FinanceRecord =
        FinanceRecord(
            id = id,
            amount = Amount(cents),
            currency = CurrencyCode("USD"),
            homeCurrencyAmount = Amount(cents),
            categoryId = "food",
            type = RecordType.EXPENSE,
            note = "Lunch",
            recordedAtEpochMillis = epochMillis,
        )
}

private class FakeRecordDateFormatter : RecordDateFormatter {
    override fun dayKey(epochMillis: Long): Long = epochMillis / DAY_MS * DAY_MS

    override fun formatDayTitle(dayKey: Long): String = "Day-$dayKey"

    override fun formatMeta(epochMillis: Long, categoryLabel: String): String = "$categoryLabel · $epochMillis"

    override fun nowEpochMillis(): Long = 1_700_000_000_000L

    override fun daysInMonth(epochMillis: Long): Int = 30

    companion object {
        private const val DAY_MS = 86_400_000L
    }
}
