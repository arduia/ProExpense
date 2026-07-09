package com.arduia.expense.feature.reports

import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.domain.UNCATEGORIZED_CATEGORY_ID

/** Portable category breakdown within a report period. */
data class ReportCategoryBreakdown(
    val categoryId: String,
    val amountCents: Long,
    val fraction: Float,
    val isOtherRollup: Boolean = false,
)

/** Sentinel id for the rolled-up "Other" bucket beyond the top-5 ranked categories. */
const val REPORT_OTHER_CATEGORY_ID = "other"

/** Portable totals for a single reporting period (design plan §ReportsViewModel). */
data class ReportPeriodResult(
    val periodStartEpochMillis: Long,
    val totalCents: Long,
    val dailyAvgCents: Long,
    val daysInPeriod: Int,
    val categories: List<ReportCategoryBreakdown>,
    val empty: Boolean,
    /** True only when every expense in the period is Uncategorized (US-REP-3 Scenario 3). */
    val allUncategorized: Boolean = false,
)

/** Computes spend totals and top-5 category breakdown for a date range, given raw epoch bounds. */
class GenerateReportPeriodUseCase {
    operator fun invoke(
        records: List<FinanceRecord>,
        periodStartEpochMillis: Long,
        periodEndEpochMillis: Long,
        daysInPeriod: Int,
    ): ReportPeriodResult {
        val inPeriod =
            records.filter { record ->
                record.type == RecordType.EXPENSE &&
                    record.recordedAtEpochMillis >= periodStartEpochMillis &&
                    record.recordedAtEpochMillis < periodEndEpochMillis
            }

        if (inPeriod.isEmpty()) {
            return ReportPeriodResult(
                periodStartEpochMillis = periodStartEpochMillis,
                totalCents = 0L,
                dailyAvgCents = 0L,
                daysInPeriod = daysInPeriod,
                categories = emptyList(),
                empty = true,
            )
        }

        val totalCents = inPeriod.sumOf { it.homeCurrencyMoney.amount.valueInCents }
        val dailyAvgCents = if (daysInPeriod > 0) totalCents / daysInPeriod else 0L

        val rankedEntries =
            inPeriod
                .groupBy { it.categoryId.value }
                .mapValues { (_, group) -> group.sumOf { it.homeCurrencyMoney.amount.valueInCents } }
                .entries
                .sortedByDescending { it.value }

        val topEntries = rankedEntries.take(5)
        val otherCents = rankedEntries.drop(5).sumOf { it.value }

        val categories =
            topEntries.map { (categoryId, amountCents) ->
                val fraction = if (totalCents == 0L) 0f else amountCents.toFloat() / totalCents
                ReportCategoryBreakdown(categoryId, amountCents, fraction)
            } +
                if (otherCents > 0L) {
                    listOf(
                        ReportCategoryBreakdown(
                            categoryId = REPORT_OTHER_CATEGORY_ID,
                            amountCents = otherCents,
                            fraction = if (totalCents == 0L) 0f else otherCents.toFloat() / totalCents,
                            isOtherRollup = true,
                        ),
                    )
                } else {
                    emptyList()
                }

        return ReportPeriodResult(
            periodStartEpochMillis = periodStartEpochMillis,
            totalCents = totalCents,
            dailyAvgCents = dailyAvgCents,
            daysInPeriod = daysInPeriod,
            categories = categories,
            empty = false,
            allUncategorized = inPeriod.all { it.categoryId.value == UNCATEGORIZED_CATEGORY_ID },
        )
    }
}
