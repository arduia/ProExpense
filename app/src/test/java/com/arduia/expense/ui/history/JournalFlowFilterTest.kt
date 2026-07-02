package com.arduia.expense.ui.history

import com.arduia.expense.feature.history.ui.filterJournalDays
import com.arduia.expense.feature.history.ui.preview.JournalDayUi
import com.arduia.expense.ui.design.ProTransactionRowModel
import org.junit.Assert.assertEquals
import org.junit.Test

private fun row(id: String, categoryId: String, note: String, amount: String) = ProTransactionRowModel(
    id = id,
    categoryId = categoryId,
    note = note,
    meta = "",
    amount = amount,
)

private fun day(vararg rows: ProTransactionRowModel, id: String = "day") = JournalDayUi(
    id = id,
    title = "Today",
    total = "$0",
    rows = rows.toList(),
)

class JournalFlowFilterTest {

    private val categoryLabel: (String) -> String = { id -> if (id == "coffee") "Coffee runs" else id }

    @Test
    fun invoke_matchesSearchAgainstNote() {
        val days = listOf(day(row("r1", "food", "Lunch with team", "$12"), row("r2", "food", "Groceries", "$40")))

        val result = filterJournalDays(days, query = "lunch", selectedFilterId = "all", categoryLabelFor = categoryLabel)

        assertEquals(listOf("r1"), result.single().rows.map { it.id })
    }

    @Test
    fun invoke_matchesSearchAgainstCategoryLabel() {
        val days = listOf(day(row("r1", "coffee", "Latte", "$5"), row("r2", "food", "Lunch", "$12")))

        val result = filterJournalDays(days, query = "coffee", selectedFilterId = "all", categoryLabelFor = categoryLabel)

        assertEquals(listOf("r1"), result.single().rows.map { it.id })
    }

    @Test
    fun invoke_matchesSearchAgainstAmount() {
        val days = listOf(day(row("r1", "food", "Lunch", "$12.40"), row("r2", "food", "Dinner", "$8.00")))

        val result = filterJournalDays(days, query = "12.40", selectedFilterId = "all", categoryLabelFor = categoryLabel)

        assertEquals(listOf("r1"), result.single().rows.map { it.id })
    }

    @Test
    fun invoke_filtersByCategoryChip() {
        val days = listOf(day(row("r1", "food", "Lunch", "$12"), row("r2", "coffee", "Latte", "$5")))

        val result = filterJournalDays(days, query = "", selectedFilterId = "coffee", categoryLabelFor = categoryLabel)

        assertEquals(listOf("r2"), result.single().rows.map { it.id })
    }

    @Test
    fun invoke_combinesFilterAndSearch() {
        val days = listOf(
            day(
                row("r1", "coffee", "Latte", "$5"),
                row("r2", "coffee", "Espresso", "$3"),
                row("r3", "food", "Lunch", "$12"),
            ),
        )

        val result = filterJournalDays(days, query = "latte", selectedFilterId = "coffee", categoryLabelFor = categoryLabel)

        assertEquals(listOf("r1"), result.single().rows.map { it.id })
    }

    @Test
    fun invoke_dropsDaysWithNoMatchingRows() {
        val days = listOf(day(row("r1", "food", "Lunch", "$12")))

        val result = filterJournalDays(days, query = "nonexistent", selectedFilterId = "all", categoryLabelFor = categoryLabel)

        assertEquals(emptyList<JournalDayUi>(), result)
    }

    @Test
    fun invoke_allFilterAndBlankQueryReturnsEverything() {
        val days = listOf(day(row("r1", "food", "Lunch", "$12"), row("r2", "coffee", "Latte", "$5")))

        val result = filterJournalDays(days, query = "", selectedFilterId = "all", categoryLabelFor = categoryLabel)

        assertEquals(days, result)
    }

    @Test
    fun invoke_dateRangeKeepsDaysWithinRangeInclusive() {
        val days = listOf(
            day(row("r1", "food", "Lunch", "$12"), id = "2026-120"),
            day(row("r2", "food", "Dinner", "$8"), id = "2026-125"),
            day(row("r3", "food", "Brunch", "$9"), id = "2026-130"),
        )

        val result = filterJournalDays(
            days, query = "", selectedFilterId = "all", categoryLabelFor = categoryLabel,
            startDayKey = "2026-120", endDayKey = "2026-125",
        )

        assertEquals(listOf("2026-120", "2026-125"), result.map { it.id })
    }

    @Test
    fun invoke_dateRangeExcludesDaysOutsideRange() {
        val days = listOf(
            day(row("r1", "food", "Lunch", "$12"), id = "2026-100"),
            day(row("r2", "food", "Dinner", "$8"), id = "2026-200"),
        )

        val result = filterJournalDays(
            days, query = "", selectedFilterId = "all", categoryLabelFor = categoryLabel,
            startDayKey = "2026-150", endDayKey = "2026-160",
        )

        assertEquals(emptyList<JournalDayUi>(), result)
    }

    @Test
    fun invoke_dateRangeCombinesWithCategoryFilterAndSearch() {
        val days = listOf(
            day(
                row("r1", "coffee", "Latte", "$5"),
                row("r2", "food", "Lunch", "$12"),
                id = "2026-120",
            ),
            day(row("r3", "coffee", "Espresso", "$3"), id = "2026-200"),
        )

        val result = filterJournalDays(
            days, query = "", selectedFilterId = "coffee", categoryLabelFor = categoryLabel,
            startDayKey = "2026-110", endDayKey = "2026-130",
        )

        assertEquals(listOf("r1"), result.single().rows.map { it.id })
    }

    @Test
    fun invoke_noDateRangeSetReturnsAllDays() {
        val days = listOf(day(row("r1", "food", "Lunch", "$12"), id = "2026-100"))

        val result = filterJournalDays(
            days, query = "", selectedFilterId = "all", categoryLabelFor = categoryLabel,
            startDayKey = null, endDayKey = null,
        )

        assertEquals(days, result)
    }
}
