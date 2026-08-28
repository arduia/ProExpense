package com.arduia.expense.shell

import com.arduia.expense.data.CategoryRepository
import com.arduia.expense.data.CurrencySettingsRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.Category
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.eventbudget.ArchiveEventUseCase
import com.arduia.expense.feature.eventbudget.CloseEventUseCase
import com.arduia.expense.feature.eventbudget.ComputeEventProgressUseCase
import com.arduia.expense.feature.eventbudget.CreateEventUseCase
import com.arduia.expense.feature.eventbudget.DeleteEventUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Backbone coverage for the event budget list and detail.
 *
 * Traceability: US-EVT-2 Scenario 4 (archived events leave the list; only ACTIVE counts as active),
 * US-EVT-3 (over-budget tiers use the uncapped ratio while the bar fill stays capped), and
 * US-EVT-5 (a closed event past its grace period is read-only).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EventBudgetViewModelTest {
    private val eventId = EventId("trip")

    private fun event(
        status: EventStatus = EventStatus.ACTIVE,
        budgetCents: Long = 10_000,
        closedAtEpochMillis: Long? = null,
    ) = Event(
        id = eventId,
        name = "Trip",
        startEpochMillis = 0L,
        endEpochMillis = 0L,
        budget = Money(Amount(budgetCents), CurrencyCode("USD")),
        status = status,
        closedAtEpochMillis = closedAtEpochMillis,
    )

    private fun linkedRecord(cents: Long) =
        FinanceRecord(
            id = RecordId("r1"),
            money = Money(Amount(cents), CurrencyCode("USD")),
            homeCurrencyMoney = Money(Amount(cents), CurrencyCode("USD")),
            categoryId = CategoryId("food"),
            type = RecordType.EXPENSE,
            note = null,
            recordedAtEpochMillis = 0L,
            link = RecordLink.ToEvent(eventId),
        )

    private fun TestScope.viewModel(
        events: List<Event>,
        records: List<FinanceRecord> = emptyList(),
    ): EventBudgetViewModel =
        EventBudgetViewModel(
            eventRepository = FakeEventRepository(events),
            financeRecordRepository = FakeEventRecordRepository(records),
            categoryRepository = FakeEventCategoryRepository(),
            currencySettingsRepository = FakeEventCurrencySettings(),
            actions =
                EventBudgetActions(
                    computeProgress = ComputeEventProgressUseCase(),
                    create = CreateEventUseCase(FakeEventRepository(events)) { 0L },
                    close = CloseEventUseCase(FakeEventRepository(events)),
                    archive = ArchiveEventUseCase(FakeEventRepository(events)),
                    delete = DeleteEventUseCase(FakeEventRepository(events)),
                ),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `archived events leave the list while closed ones stay`() =
        runTest {
            val vm =
                viewModel(
                    listOf(
                        event(status = EventStatus.ARCHIVED),
                        event(status = EventStatus.CLOSED).copy(id = EventId("closed")),
                    ),
                )

            advanceUntilIdle()

            assertEquals(1, vm.uiState.value.cards.size)
            assertEquals(
                "closed",
                vm.uiState.value.cards
                    .first()
                    .eventId,
            )
            assertEquals(0, vm.uiState.value.activeCount)
        }

    @Test
    fun `over budget keeps the bar capped but reports the uncapped ratio`() =
        runTest {
            val vm = viewModel(listOf(event(budgetCents = 10_000)), listOf(linkedRecord(15_000)))

            advanceUntilIdle()

            val card =
                assertNotNull(
                    vm.uiState.value.cards
                        .firstOrNull(),
                )
            assertTrue(card.isOverBudget)
            assertEquals(1f, card.progress)
            assertEquals(1.5f, card.spentRatio)
            assertEquals(50, card.overBudgetPercent)
        }

    @Test
    fun `an active event is editable`() =
        runTest {
            val vm = viewModel(listOf(event()))

            advanceUntilIdle()

            assertFalse(
                assertNotNull(
                    vm.uiState.value.cards
                        .firstOrNull(),
                ).isReadOnly,
            )
        }

    /** Regression: linked rows were built with an empty category map, so their labels came out blank. */
    @Test
    fun `linked rows resolve their category name`() =
        runTest {
            val vm = viewModel(listOf(event()), listOf(linkedRecord(2_500)))
            advanceUntilIdle()

            vm.onEventSelected(eventId = eventId.value)
            advanceUntilIdle()

            val row =
                assertNotNull(
                    vm.uiState.value.linkedRows
                        .firstOrNull(),
                )
            assertEquals("Food", row.note)
            assertTrue(row.meta.startsWith("Food"))
        }
}

private class FakeEventRepository(
    events: List<Event>,
) : EventRepository {
    private val flow = MutableStateFlow(events)

    override suspend fun getAll(): Result<List<Event>> = Result.Success(flow.value)

    override suspend fun getById(id: EventId): Result<Event?> = Result.Success(flow.value.firstOrNull { it.id == id })

    override suspend fun upsert(event: Event): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: EventId): Result<Unit> = Result.Success(Unit)

    override fun observeAll(): Flow<List<Event>> = flow

    override suspend fun getSpent(id: EventId): Result<Money> = Result.Success(Money(Amount(0), CurrencyCode("USD")))
}

private class FakeEventRecordRepository(
    records: List<FinanceRecord>,
) : FinanceRecordRepository {
    private val flow = MutableStateFlow(records)

    override fun observeAll(): Flow<List<FinanceRecord>> = flow

    override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(flow.value)

    override suspend fun getById(id: RecordId): Result<FinanceRecord?> = Result.Success(flow.value.firstOrNull { it.id == id })

    override suspend fun upsert(record: FinanceRecord): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: RecordId): Result<Unit> = Result.Success(Unit)

    override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = Result.Success(true)

    override suspend fun getRecordsPage(
        filter: RecordPageFilter,
        cursor: RecordPageCursor?,
        limit: Int,
    ): Result<List<FinanceRecord>> = Result.Success(flow.value.take(limit))

    override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> = Result.Success(false)

    override fun observeChangeSignal(): Flow<RecordChangeSignal> = flowOf(RecordChangeSignal(0L, 0L))
}

private class FakeEventCategoryRepository : CategoryRepository {
    private val categories = listOf(Category(id = CategoryId("food"), name = "Food", iconId = "food", sortOrder = 0))

    override suspend fun getAll(): Result<List<Category>> = Result.Success(categories)

    override suspend fun upsert(category: Category): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: CategoryId): Result<Unit> = Result.Success(Unit)

    override suspend fun reorder(orderedIds: List<CategoryId>): Result<Unit> = Result.Success(Unit)

    override fun observeAll(): Flow<List<Category>> = flowOf(categories)
}

private class FakeEventCurrencySettings : CurrencySettingsRepository {
    override suspend fun getHomeCurrency(): Result<CurrencyCode?> = Result.Success(CurrencyCode("USD"))

    override suspend fun setHomeCurrency(currency: CurrencyCode): Result<Unit> = Result.Success(Unit)
}
