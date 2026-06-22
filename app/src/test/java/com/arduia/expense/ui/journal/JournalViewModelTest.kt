package com.arduia.expense.ui.journal

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.fake.FakeFinanceRecordRepository
import com.arduia.expense.fake.FakeProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JournalViewModelTest {

    private val now = 1_700_000_000_000L // fixed "today"
    private val yesterday = now - 24L * 60 * 60 * 1000

    private fun record(
        id: String,
        cents: Long,
        at: Long,
        type: RecordType = RecordType.EXPENSE,
        categoryId: String = "food",
        note: String? = "Lunch",
    ) = FinanceRecord(
        id = id,
        amount = Amount(cents),
        currency = CurrencyCode("USD"),
        homeCurrencyAmount = Amount(cents),
        categoryId = categoryId,
        type = type,
        note = note,
        recordedAtEpochMillis = at,
    )

    private fun TestScope.journalViewModel(
        records: List<FinanceRecord> = emptyList(),
        finance: FakeFinanceRecordRepository = FakeFinanceRecordRepository(records),
    ) = JournalViewModel(
        financeRepository = finance,
        profileRepository = FakeProfileRepository(currency = CurrencyCode("USD"), onboardingCompleted = true),
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        clock = { now },
    )

    // Rule: expenses are grouped into day buckets (one per calendar day).
    @Test
    fun groupsRecordsByDay() = runTest {
        val vm = journalViewModel(
            records = listOf(
                record("a", 1000, now),
                record("b", 2000, now),
                record("c", 3000, yesterday),
            ),
        )
        assertEquals(2, vm.state.value.days.size)
    }

    // Rule: income is excluded — Journal shows spend history only.
    @Test
    fun excludesIncome() = runTest {
        val vm = journalViewModel(
            records = listOf(
                record("a", 1000, now),
                record("income", 9999, now, type = RecordType.INCOME),
            ),
        )
        val rowIds = vm.state.value.days.flatMap { it.rows }.map { it.id }
        assertEquals(listOf("a"), rowIds)
    }

    // Rule: a detail projection exists per record id, formatted in the home currency.
    @Test
    fun buildsDetailPerRecord() = runTest {
        val vm = journalViewModel(records = listOf(record("rec-1", 1240, now)))
        val detail = vm.state.value.details["rec-1"]
        assertNotNull(detail)
        assertTrue(detail.amountLabel.contains("12.40"))
    }

    // Rule: deleting a record removes it from the projection (primary failure-free write path).
    @Test
    fun deleteRemovesRecord() = runTest {
        val finance = FakeFinanceRecordRepository(listOf(record("rec-1", 1000, now)))
        val vm = journalViewModel(finance = finance)
        assertNotNull(vm.state.value.details["rec-1"])

        vm.delete("rec-1")

        assertNull(vm.state.value.details["rec-1"])
        assertTrue(vm.state.value.days.isEmpty())
    }
}
