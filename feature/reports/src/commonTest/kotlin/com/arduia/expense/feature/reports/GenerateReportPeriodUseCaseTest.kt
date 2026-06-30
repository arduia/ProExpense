package com.arduia.expense.feature.reports

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun record(
    id: String,
    categoryId: String,
    amountCents: Long,
    recordedAtEpochMillis: Long,
    type: RecordType = RecordType.EXPENSE,
) = FinanceRecord(
    id = RecordId(id),
    money = Money(Amount(amountCents), CurrencyCode("USD")),
    homeCurrencyMoney = Money(Amount(amountCents), CurrencyCode("USD")),
    categoryId = CategoryId(categoryId),
    type = type,
    note = null,
    recordedAtEpochMillis = recordedAtEpochMillis,
)

class GenerateReportPeriodUseCaseTest {

    private val useCase = GenerateReportPeriodUseCase()

    @Test
    fun invoke_returnsEmptyResultWhenNoRecordsInPeriod() {
        val result = useCase(emptyList(), periodStartEpochMillis = 0, periodEndEpochMillis = 100, daysInPeriod = 7)

        assertTrue(result.empty)
        assertEquals(0L, result.totalCents)
        assertEquals(0L, result.dailyAvgCents)
        assertEquals(emptyList(), result.categories)
    }

    @Test
    fun invoke_filtersByTypeAndDateRangeAndComputesTotals() {
        val records = listOf(
            record("r1", "food", 1000, recordedAtEpochMillis = 10),
            record("r2", "food", 500, recordedAtEpochMillis = 50, type = RecordType.INCOME),
            record("r3", "food", 2000, recordedAtEpochMillis = 200),
            record("r4", "food", 3000, recordedAtEpochMillis = 99),
        )

        val result = useCase(records, periodStartEpochMillis = 0, periodEndEpochMillis = 100, daysInPeriod = 4)

        assertEquals(false, result.empty)
        assertEquals(4000L, result.totalCents)
        assertEquals(1000L, result.dailyAvgCents)
    }

    @Test
    fun invoke_returnsTopFiveCategoriesSortedDescendingWithFraction() {
        val records = (1..6).map { i ->
            record("r$i", "cat$i", amountCents = (100L * i), recordedAtEpochMillis = 10)
        }

        val result = useCase(records, periodStartEpochMillis = 0, periodEndEpochMillis = 100, daysInPeriod = 1)

        assertEquals(5, result.categories.size)
        assertEquals("cat6", result.categories.first().categoryId)
        assertEquals("cat2", result.categories.last().categoryId)
        val totalCents = result.totalCents
        assertEquals(600f / totalCents, result.categories.first().fraction)
    }

    @Test
    fun invoke_zeroDaysInPeriodAvoidsDivisionByZero() {
        val records = listOf(record("r1", "food", 1000, recordedAtEpochMillis = 10))

        val result = useCase(records, periodStartEpochMillis = 0, periodEndEpochMillis = 100, daysInPeriod = 0)

        assertEquals(0L, result.dailyAvgCents)
    }
}
