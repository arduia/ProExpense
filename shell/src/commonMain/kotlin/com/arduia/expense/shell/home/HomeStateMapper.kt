package com.arduia.expense.shell.home

import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.SHOW_SPLIT_AND_DEBT_ROWS
import com.arduia.expense.domain.isVisibleInHomeRecents
import com.arduia.expense.domain.kind
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.ui.design.AmountInput
import com.arduia.expense.ui.design.PlatformDateFormatter
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

const val RECENT_HOME_LIMIT = 8

private const val SPARKLINE_DAYS = 7

/** "WEDNESDAY" -> "WED", "JANUARY" -> "JAN" — matches `SimpleDateFormat("EEE"/"MMM").uppercase()`. */
private const val ABBREVIATION_LENGTH = 3

/** Bundles the tag-label lookup maps so they cost one parameter, not four, at every call site. */
data class HomeLinkNames(
    val eventNames: Map<String, String>,
    val debtNames: Map<String, String>,
    val sharedCostNames: Map<String, String>,
    val categoryNames: Map<String, String>,
)

/** Half-open `[start, end)` epoch-millis bounds of the calendar month containing [nowEpochMillis]. */
internal fun monthBounds(
    nowEpochMillis: Long,
    zone: TimeZone = TimeZone.currentSystemDefault(),
): LongRange {
    val date = Instant.fromEpochMilliseconds(nowEpochMillis).toLocalDateTime(zone).date
    val monthStart = LocalDate(date.year, date.month, 1)
    val start = monthStart.atStartOfDayIn(zone).toEpochMilliseconds()
    val end = monthStart.plus(1, DateTimeUnit.MONTH).atStartOfDayIn(zone).toEpochMilliseconds()
    return start until end
}

private fun List<FinanceRecord>.spendInMonthCents(
    nowEpochMillis: Long,
    zone: TimeZone,
): Long {
    val month = monthBounds(nowEpochMillis, zone)
    // Only expenses count as spend — income must never offset it (US-HOME-1 / US-MORE-2).
    return filter { it.recordedAtEpochMillis in month && it.type == RecordType.EXPENSE }
        .sumOf { it.homeCurrencyMoney.amount.valueInCents }
}

/**
 * US-MORE-2: "spend vs. budget" always recalculates from the current calendar month, so the reset
 * on the 1st is a byproduct of the month filter rather than a separate reset mechanism — a new
 * month naturally has zero matching records. [nowEpochMillis] defaults to wall-clock time in
 * production; tests pin it to control which "current month" is in effect.
 */
fun computeBudgetSummary(
    records: List<FinanceRecord>,
    monthlyBudget: Money?,
    homeSymbol: String,
    nowEpochMillis: Long = currentEpochMillis(),
    zone: TimeZone = TimeZone.currentSystemDefault(),
): HomeBudgetSummaryState? {
    val budget = monthlyBudget ?: return null
    val totalCents = records.spendInMonthCents(nowEpochMillis, zone)
    val budgetCents = budget.amount.valueInCents
    return HomeBudgetSummaryState(
        spentLabel = AmountInput.formatMoney(totalCents, homeSymbol),
        budgetLabel = "of " + AmountInput.formatMoney(budgetCents, homeSymbol),
        progress = if (budgetCents > 0) totalCents.toFloat() / budgetCents else 0f,
        statusLabel = if (totalCents > budgetCents) "Over budget" else "On track",
        isOverBudget = totalCents > budgetCents,
    )
}

internal fun buildSparklinePoints(
    records: List<FinanceRecord>,
    nowEpochMillis: Long = currentEpochMillis(),
    zone: TimeZone = TimeZone.currentSystemDefault(),
): List<Float> {
    val today = Instant.fromEpochMilliseconds(nowEpochMillis).toLocalDateTime(zone).date
    return (SPARKLINE_DAYS - 1 downTo 0).map { offset ->
        val dayStart = today.minus(offset, DateTimeUnit.DAY).atStartOfDayIn(zone).toEpochMilliseconds()
        val key = PlatformDateFormatter.dayKey(dayStart)
        records
            .filter { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) == key && it.type == RecordType.EXPENSE }
            // homeCurrencyMoney, not the record's own currency (US-CUR-4) — otherwise a foreign
            // currency amount is added into the sparkline as if it were home-currency cents.
            .sumOf { it.homeCurrencyMoney.amount.valueInCents }
            .toFloat()
    }
}

/** Shared timestamp so [FinanceRecord] and toggle-off [Debt] rows can be merged and truncated together. */
internal data class HomeMergedEntry(
    val recordedAtEpochMillis: Long,
    val item: HomeTransactionItem,
)

/**
 * Merges real records with toggle-off debts (visible everywhere, never counted toward totals —
 * see [Debt.recordAsTransaction]) before truncating to [limit], so a recent debt doesn't get
 * evicted by real expenses/income that are actually older than it, then groups by day. Day totals
 * are computed from the [records] subset only.
 */
fun buildHomeDayGroups(
    records: List<FinanceRecord>,
    visibleDebts: List<Debt>,
    linkNames: HomeLinkNames,
    homeCurrencySymbol: String,
    limit: Int = RECENT_HOME_LIMIT,
): List<HomeDayGroup> {
    val recordEntries =
        records
            .filter { it.kind().isVisibleInHomeRecents() }
            .map { record -> HomeMergedEntry(record.recordedAtEpochMillis, record.toHomeTransactionItem(linkNames)) }
    // Unlike the recordEntries filter above, toggle-off debts are never a real FinanceRecord — a
    // debt-kind entry there always means toggle-on (counted), so it keeps its own gate here.
    val debtEntries =
        if (SHOW_SPLIT_AND_DEBT_ROWS) {
            visibleDebts.map { debt -> HomeMergedEntry(debt.recordedAtEpochMillis, debt.toDebtHomeTransactionItem()) }
        } else {
            emptyList()
        }
    val recent = (recordEntries + debtEntries).sortedByDescending { it.recordedAtEpochMillis }.take(limit)

    return recent
        .groupBy { PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) }
        .toList()
        .sortedByDescending { (key, _) -> key }
        .map { (key, dayEntries) ->
            val dayTotalCents =
                records
                    .filter {
                        PlatformDateFormatter.dayKey(it.recordedAtEpochMillis) == key && it.type == RecordType.EXPENSE
                    }.sumOf { it.homeCurrencyMoney.amount.valueInCents }
            HomeDayGroup(
                dayTitle = PlatformDateFormatter.dayLabel(dayEntries.first().recordedAtEpochMillis),
                dayTotal = AmountInput.formatMoney(dayTotalCents, homeCurrencySymbol),
                transactions = dayEntries.map { it.item },
            )
        }
}

/** "WED · MAY 25" — the Home header's date line. */
internal fun buildDateLabel(
    nowEpochMillis: Long = currentEpochMillis(),
    zone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val date = Instant.fromEpochMilliseconds(nowEpochMillis).toLocalDateTime(zone).date
    val weekday = date.dayOfWeek.name.take(ABBREVIATION_LENGTH)
    val month = date.month.name.take(ABBREVIATION_LENGTH)
    return "$weekday · $month ${date.dayOfMonth}"
}

/** "MAY" — the month chip above the spend figure. */
internal fun buildMonthLabel(
    nowEpochMillis: Long = currentEpochMillis(),
    zone: TimeZone = TimeZone.currentSystemDefault(),
): String =
    Instant
        .fromEpochMilliseconds(nowEpochMillis)
        .toLocalDateTime(zone)
        .date.month.name
        .take(ABBREVIATION_LENGTH)

/**
 * Builds the whole Home screen state from already-loaded data. Pure — no coroutines, no
 * repositories — so both [HomeViewModel] and unit tests can call it with fixed inputs.
 */
@Suppress("LongParameterList")
fun buildHomeUiState(
    records: List<FinanceRecord>,
    visibleDebts: List<Debt>,
    linkNames: HomeLinkNames,
    homeCurrencySymbol: String,
    userName: String,
    monthlyBudget: Money?,
    activeEvent: HomeActiveEventState?,
    recordsLoading: Boolean,
    nowEpochMillis: Long = currentEpochMillis(),
    zone: TimeZone = TimeZone.currentSystemDefault(),
): HomeUiState {
    val dateLabel = buildDateLabel(nowEpochMillis, zone)
    val monthLabel = buildMonthLabel(nowEpochMillis, zone)
    if (records.isEmpty() && visibleDebts.isEmpty()) {
        return HomeUiState(
            greetingName = userName,
            dateLabel = dateLabel,
            monthLabel = monthLabel,
            activeEvent = activeEvent,
            isLoading = recordsLoading,
        )
    }
    return HomeUiState(
        greetingName = userName,
        dateLabel = dateLabel,
        monthLabel = monthLabel,
        // "Spend this month" (US-HOME-1), not all-time — the header label promises a monthly figure.
        monthSpend = AmountInput.formatMoney(records.spendInMonthCents(nowEpochMillis, zone), homeCurrencySymbol),
        showEmptyHint = false,
        // Recent shows the last 5-10 entries (US-HOME-2), not the entire history.
        dayGroups = buildHomeDayGroups(records, visibleDebts, linkNames, homeCurrencySymbol),
        sparklinePoints = buildSparklinePoints(records, nowEpochMillis, zone),
        budgetSummary = computeBudgetSummary(records, monthlyBudget, homeCurrencySymbol, nowEpochMillis, zone),
        activeEvent = activeEvent,
    )
}
