package com.arduia.expense.feature.history

import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
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
class EventDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    @Test
    fun onConfirmDelete_invokesCallbackAfterDelete() = runTest(dispatcher) {
        val event = Event(
            id = "event_1",
            name = "Trip",
            startEpochMillis = 1L,
            endEpochMillis = 2L,
            budgetAmount = Amount(10000),
            status = EventStatus.ACTIVE,
        )
        val eventRepository = FakeEventRepository(event)
        var deleted = false
        val viewModel = EventDetailViewModel(
            eventId = "event_1",
            eventRepository = eventRepository,
            historyRepository = FakeHistoryRepositoryForEvent(emptyList()),
            formatter = FakeRecordDateFormatter(),
            homeCurrencyCode = "USD",
            categoryLabel = { "Food" },
            scope = scope.backgroundScope,
            onDeleted = { deleted = true },
        )
        advanceUntilIdle()

        viewModel.onConfirmDelete()
        advanceUntilIdle()

        assertTrue(deleted)
        assertTrue(eventRepository.deletedIds.contains("event_1"))
    }

    @Test
    fun onDeleteRequested_setsLinkedExpenseCount() = runTest(dispatcher) {
        val event = Event(
            id = "event_1",
            name = "Trip",
            startEpochMillis = 1L,
            endEpochMillis = 2L,
            budgetAmount = Amount(10000),
            status = EventStatus.ACTIVE,
        )
        val records = listOf(
            FinanceRecord(
                id = "r1",
                amount = Amount(500),
                currency = com.arduia.expense.domain.CurrencyCode("USD"),
                homeCurrencyAmount = Amount(500),
                categoryId = "food",
                type = RecordType.EXPENSE,
                recordedAtEpochMillis = 1L,
                tagType = ExpenseTagType.EVENT,
                tagId = "event_1",
            ),
        )
        val viewModel = EventDetailViewModel(
            eventId = "event_1",
            eventRepository = FakeEventRepository(event),
            historyRepository = FakeHistoryRepositoryForEvent(records),
            formatter = FakeRecordDateFormatter(),
            homeCurrencyCode = "USD",
            categoryLabel = { "Food" },
            scope = scope.backgroundScope,
        )
        advanceUntilIdle()

        viewModel.onDeleteRequested()
        assertEquals(1, viewModel.uiState.value.linkedExpenseCount)
        assertTrue(viewModel.uiState.value.showDeleteConfirm)
    }
}

private class FakeEventRepository(
    private var event: Event?,
) : EventRepository {
    val deletedIds = mutableListOf<String>()

    override suspend fun getAll(): Result<List<Event>> =
        Result.Success(event?.let { listOf(it) } ?: emptyList())

    override suspend fun getById(id: String): Result<Event?> =
        Result.Success(if (event?.id == id) event else null)

    override suspend fun upsert(event: Event): Result<Unit> {
        this.event = event
        return Result.Success(Unit)
    }

    override suspend fun delete(id: String): Result<Unit> {
        deletedIds.add(id)
        if (event?.id == id) event = null
        return Result.Success(Unit)
    }
}

private class FakeHistoryRepositoryForEvent(
    private val records: List<FinanceRecord>,
) : HistoryRepository {
    override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> =
        Result.Success(records)

    override suspend fun getById(id: String): Result<FinanceRecord?> =
        Result.Success(records.firstOrNull { it.id == id })

    override suspend fun deleteRecord(id: String): Result<Unit> = Result.Success(Unit)

    override suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary> =
        Result.Success(RecordSummary(period, Amount(0), 0))
}

private class FakeRecordDateFormatter : RecordDateFormatter {
    override fun dayKey(epochMillis: Long): Long = epochMillis
    override fun formatDayTitle(dayKey: Long): String = "May 12"
    override fun formatMonthYear(epochMillis: Long): String = "May 2025"
    override fun formatMeta(epochMillis: Long, categoryLabel: String): String = categoryLabel
    override fun nowEpochMillis(): Long = 1L
    override fun daysInMonth(epochMillis: Long): Int = 30
}
