package com.arduia.expense.shell.home

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The Home screen's shared mapping rules, pinned to a fixed clock and UTC so both platform shells
 * are provably rendering the same figures. Traces to US-HOME-1 (spend this month), US-HOME-2
 * (recent list), US-HOME-3/US-MORE-2 (budget planner + monthly reset).
 */
class HomeStateMapperTest {
    private val utc = TimeZone.UTC
    private val usd = CurrencyCode("USD")

    private fun millis(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 12,
        minute: Int = 0,
    ): Long = LocalDateTime(year, month, day, hour, minute).toInstant(utc).toEpochMilliseconds()

    private val julyFifteenth = millis(2026, 7, 15)

    private fun emptyLinkNames() = HomeLinkNames(emptyMap(), emptyMap(), emptyMap(), emptyMap())

    private fun homeState(
        records: List<com.arduia.expense.domain.FinanceRecord>,
        monthlyBudget: Money? = null,
        userName: String = "Maya",
        now: Long = julyFifteenth,
    ) = buildHomeUiState(
        records = records,
        visibleDebts = emptyList(),
        linkNames = emptyLinkNames(),
        homeCurrencySymbol = "$",
        userName = userName,
        monthlyBudget = monthlyBudget,
        activeEvent = null,
        recordsLoading = false,
        nowEpochMillis = now,
        zone = utc,
    )

    // --- US-HOME-1: spend this month -------------------------------------------------------

    @Test
    fun monthSpend_sumsOnlyCurrentMonthExpenses() {
        val state =
            homeState(
                listOf(
                    testRecord("r1", 3_000, millis(2026, 7, 5)),
                    testRecord("r2", 2_000, millis(2026, 7, 10)),
                    testRecord("r3", 90_000, millis(2026, 6, 28)),
                ),
            )

        assertEquals("$50", state.monthSpend)
        assertFalse(state.showEmptyHint)
    }

    @Test
    fun monthSpend_excludesIncome() {
        val state =
            homeState(
                listOf(
                    testRecord("r1", 3_000, millis(2026, 7, 5)),
                    testRecord("r2", 500_000, millis(2026, 7, 6), type = RecordType.INCOME),
                ),
            )

        assertEquals("$30", state.monthSpend)
    }

    @Test
    fun monthSpend_includesTheFirstInstantOfTheMonthAndExcludesTheNext() {
        val state =
            homeState(
                listOf(
                    testRecord("first", 1_000, millis(2026, 7, 1, hour = 0, minute = 0)),
                    testRecord("nextMonth", 7_000, millis(2026, 8, 1, hour = 0, minute = 0)),
                ),
            )

        assertEquals("$10", state.monthSpend)
    }

    @Test
    fun emptyRecords_keepEmptyHintAndZeroSpend() {
        val state = homeState(emptyList())

        assertTrue(state.showEmptyHint)
        assertTrue(state.isEmpty)
        assertEquals("$0", state.monthSpend)
        assertEquals("Maya", state.greetingName)
        assertEquals("welcome", state.greetingPrefixRes)
    }

    @Test
    fun loadingBeforeFirstEmission_isNotTreatedAsEmpty() {
        val state =
            buildHomeUiState(
                records = emptyList(),
                visibleDebts = emptyList(),
                linkNames = emptyLinkNames(),
                homeCurrencySymbol = "$",
                userName = "Maya",
                monthlyBudget = null,
                activeEvent = null,
                recordsLoading = true,
                nowEpochMillis = julyFifteenth,
                zone = utc,
            )

        assertTrue(state.isLoading)
        assertFalse(state.isEmpty)
    }

    // --- US-HOME-2: recent list ------------------------------------------------------------

    @Test
    fun recentList_isTruncatedToTheHomeLimitNewestFirst() {
        val records = (1..12).map { day -> testRecord("r$day", day * 100L, millis(2026, 7, day)) }

        val state = homeState(records)

        val rows = state.dayGroups.flatMap { it.transactions }
        assertEquals(RECENT_HOME_LIMIT, rows.size)
        // Newest first: day 12 down to day 5.
        assertEquals("r12", rows.first().id)
        assertEquals("r5", rows.last().id)
    }

    @Test
    fun dayGroupTotal_sumsThatDaysExpensesOnly() {
        // Identical timestamps: day grouping runs in the device timezone, so sharing one instant
        // keeps these in a single group no matter where the test runs.
        val sameInstant = millis(2026, 7, 14, hour = 9)
        val state =
            homeState(
                listOf(
                    testRecord("a", 1_500, sameInstant),
                    testRecord("b", 2_500, sameInstant),
                    testRecord("income", 99_000, sameInstant, type = RecordType.INCOME),
                ),
            )

        val group = state.dayGroups.single()
        assertEquals(3, group.transactions.size)
        assertEquals("$40", group.dayTotal)
    }

    @Test
    fun rowNote_fallsBackToTheCategoryLabelWhenTheNoteIsBlank() {
        val linkNames = HomeLinkNames(emptyMap(), emptyMap(), emptyMap(), mapOf("food" to "Food"))
        val state =
            buildHomeUiState(
                records = listOf(testRecord("r1", 1_000, julyFifteenth, note = "   ")),
                visibleDebts = emptyList(),
                linkNames = linkNames,
                homeCurrencySymbol = "$",
                userName = "Maya",
                monthlyBudget = null,
                activeEvent = null,
                recordsLoading = false,
                nowEpochMillis = julyFifteenth,
                zone = utc,
            )

        assertEquals(
            "Food",
            state.dayGroups
                .single()
                .transactions
                .single()
                .note,
        )
    }

    // --- US-HOME-3 / US-MORE-2: budget planner ----------------------------------------------

    @Test
    fun budgetSummary_isNullWhenNoBudgetIsSet() {
        assertNull(computeBudgetSummary(emptyList(), null, "$", julyFifteenth, utc))
    }

    @Test
    fun budgetSummary_onTrackWhenSpendIsUnderBudget() {
        val summary =
            computeBudgetSummary(
                listOf(
                    testRecord("r1", 3_000, millis(2026, 7, 5)),
                    testRecord("r2", 2_000, millis(2026, 7, 10)),
                ),
                Money(Amount(10_000), usd),
                "$",
                julyFifteenth,
                utc,
            )

        assertNotNull(summary)
        assertEquals("$50", summary.spentLabel)
        assertEquals("of $100", summary.budgetLabel)
        assertEquals(0.5f, summary.progress)
        assertEquals("On track", summary.statusLabel)
        assertFalse(summary.isOverBudget)
    }

    @Test
    fun budgetSummary_overBudgetWhenSpendExceedsBudget() {
        val summary =
            computeBudgetSummary(
                listOf(testRecord("r1", 15_000, millis(2026, 7, 3))),
                Money(Amount(10_000), usd),
                "$",
                millis(2026, 7, 20),
                utc,
            )

        assertNotNull(summary)
        assertEquals("Over budget", summary.statusLabel)
        assertTrue(summary.isOverBudget)
    }

    @Test
    fun budgetSummary_resetsAtTheStartOfTheMonth() {
        // Heavy July spend must not bleed into August's progress (US-MORE-2 automatic reset).
        val summary =
            computeBudgetSummary(
                listOf(testRecord("r1", 90_000, millis(2026, 7, 28))),
                Money(Amount(10_000), usd),
                "$",
                millis(2026, 8, 2),
                utc,
            )

        assertNotNull(summary)
        assertEquals("$0", summary.spentLabel)
        assertEquals("On track", summary.statusLabel)
    }

    @Test
    fun budgetFigureItself_isUnaffectedByMonthChange() {
        val budget = Money(Amount(25_000), usd)
        val july = computeBudgetSummary(emptyList(), budget, "$", millis(2026, 7, 15), utc)
        val august = computeBudgetSummary(emptyList(), budget, "$", millis(2026, 8, 15), utc)

        assertNotNull(july)
        assertNotNull(august)
        assertEquals("of $250", july.budgetLabel)
        assertEquals(july.budgetLabel, august.budgetLabel)
    }

    // --- Header labels ------------------------------------------------------------------------

    @Test
    fun headerLabels_matchTheAndroidFormats() {
        assertEquals("WED · JUL 15", buildDateLabel(julyFifteenth, utc))
        assertEquals("JUL", buildMonthLabel(julyFifteenth, utc))
    }

    @Test
    fun monthBounds_spanTheWholeCalendarMonth() {
        val bounds = monthBounds(julyFifteenth, utc)

        assertEquals(millis(2026, 7, 1, hour = 0), bounds.first)
        assertEquals(millis(2026, 8, 1, hour = 0) - 1, bounds.last)
    }

    // --- Sparkline ---------------------------------------------------------------------------

    @Test
    fun sparkline_hasOnePointPerDayOfTheLastWeek() {
        val points = buildSparklinePoints(emptyList(), julyFifteenth, utc)

        assertEquals(7, points.size)
        assertTrue(points.all { it == 0f })
    }
}
