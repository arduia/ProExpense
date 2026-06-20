/**
 * Domain rules (quick log / A1):
 * - Selected @ tag (event/debt) persisted on save.
 * - Save uses home currency amount from amount input.
 */
package com.arduia.expense.feature.logging

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddExpenseViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onTagSelected_persistsTagOnSave() = runTest(dispatcher) {
        val loggingRepository = FakeLoggingRepository()
        val event = Event(
            id = "event_1",
            name = "Trip",
            startEpochMillis = 1L,
            endEpochMillis = 2L,
            budgetAmount = Amount(10000),
            status = EventStatus.ACTIVE,
        )
        var saved = false
        val viewModel = AddExpenseViewModel(
            loggingRepository = loggingRepository,
            eventRepository = FakeEventRepository(listOf(event)),
            debtRepository = FakeDebtRepository(),
            homeCurrencyCode = "USD",
            scope = this,
            nowEpochMillis = { 1_700_000_000_000L },
            onSaved = { saved = true },
            onSaveFailed = {},
        )
        advanceUntilIdle()

        viewModel.onAmountChanged("12.50")
        viewModel.onTagSelected(
            ExpenseTagOption(type = ExpenseTagType.EVENT, id = "event_1", label = "Trip"),
        )
        viewModel.onRecordedAtChanged(1_700_000_000_000L)
        viewModel.onSaveFromAmountOnly()
        advanceUntilIdle()

        assertTrue(saved)
        val record = loggingRepository.records.single()
        assertEquals(ExpenseTagType.EVENT, record.tagType)
        assertEquals("event_1", record.tagId)
        assertEquals(1_700_000_000_000L, record.recordedAtEpochMillis)
    }
}

private class FakeLoggingRepository : LoggingRepository {
    val records = mutableListOf<FinanceRecord>()

    override suspend fun createRecord(input: LogRecordInput): Result<FinanceRecord> {
        val record = FinanceRecord(
            id = "rec_${records.size}",
            amount = input.amount,
            currency = input.currency,
            homeCurrencyAmount = input.homeCurrencyAmount,
            categoryId = input.categoryId,
            type = input.type,
            note = input.note,
            recordedAtEpochMillis = input.recordedAtEpochMillis,
            tagType = input.tagType,
            tagId = input.tagId,
        )
        records.add(record)
        return Result.Success(record)
    }

    override suspend fun getRecord(id: String): Result<FinanceRecord?> =
        Result.Success(records.firstOrNull { it.id == id })

    override suspend fun updateRecord(record: FinanceRecord): Result<Unit> = Result.Success(Unit)

    override suspend fun deleteRecord(id: String): Result<Unit> = Result.Success(Unit)
}

private class FakeEventRepository(
    private val events: List<Event>,
) : EventRepository {
    override suspend fun getAll(): Result<List<Event>> = Result.Success(events)

    override suspend fun getById(id: String): Result<Event?> =
        Result.Success(events.firstOrNull { it.id == id })

    override suspend fun upsert(event: Event): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: String): Result<Unit> = Result.Success(Unit)
}

private class FakeDebtRepository(
    private val debts: List<Debt> = emptyList(),
) : DebtRepository {
    override suspend fun getAll(): Result<List<Debt>> = Result.Success(debts)

    override suspend fun getById(id: String): Result<Debt?> =
        Result.Success(debts.firstOrNull { it.id == id })

    override suspend fun upsert(debt: Debt): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: String): Result<Unit> = Result.Success(Unit)

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> =
        Result.Success(debts.filter { it.personName == personName })
}
