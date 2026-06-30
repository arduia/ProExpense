package com.arduia.expense.feature.reports

import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType

/** Portable category breakdown within a report period. */
data class ReportCategoryBreakdown(
    val categoryId: String,
    val amountCents: Long,
    val fraction: Float,
)

/** Portable totals for a single reporting period (design plan §ReportsViewModel). */
data class ReportPeriodResult(
    val periodStartEpochMillis: Long,
    val totalCents: Long,
    val dailyAvgCents: Long,
    val daysInPeriod: Int,
    val categories: List<ReportCategoryBreakdown>,
    val empty: Boolean,
)

/** Computes spend totals and top-5 category breakdown for a date range, given raw epoch bounds. */
class GenerateReportPeriodUseCase {
    operator fun invoke(
        records: List<FinanceRecord>,
        periodStartEpochMillis: Long,
        periodEndEpochMillis: Long,
        daysInPeriod: Int,
    ): ReportPeriodResult {
        val inPeriod = records.filter { record ->
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

        val categories = inPeriod
            .groupBy { it.categoryId.value }
            .mapValues { (_, group) -> group.sumOf { it.homeCurrencyMoney.amount.valueInCents } }
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { (categoryId, amountCents) ->
                val fraction = if (totalCents == 0L) 0f else amountCents.toFloat() / totalCents
                ReportCategoryBreakdown(categoryId, amountCents, fraction)
            }

        return ReportPeriodResult(
            periodStartEpochMillis = periodStartEpochMillis,
            totalCents = totalCents,
            dailyAvgCents = dailyAvgCents,
            daysInPeriod = daysInPeriod,
            categories = categories,
            empty = false,
        )
    }
}
