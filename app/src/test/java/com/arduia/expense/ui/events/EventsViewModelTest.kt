package com.arduia.expense.ui.events

import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.ExpenseTagType
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.fake.FakeEventRepository
import com.arduia.expense.fake.FakeFinanceRecordRepository
import com.arduia.expense.fake.FakeProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EventsViewModelTest {

    private val now = 1_700_000_000_000L

    private fun event(id: String, budgetCents: Long, status: EventStatus = EventStatus.ACTIVE) = Event(
        id = id,
        name = "Trip $id",
        startEpochMillis = now,
        endEpochMillis = now + 14L * 24 * 60 * 60 * 1000,
        budgetAmount = Amount(budgetCents),
        status = status,
    )

    private fun tagged(id: String, cents: Long, eventId: String) = FinanceRecord(
        id = id,
        amount = Amount(cents),
        currency = CurrencyCode("USD"),
        homeCurrencyAmount = Amount(cents),
        categoryId = "food",
        type = RecordType.EXPENSE,
        note = "Hotel",
        recordedAtEpochMillis = now,
        tagType = ExpenseTagType.EVENT,
        tagId = eventId,
    )

    private fun TestScope.eventsViewModel(
        events: List<Event> = emptyList(),
        records: List<FinanceRecord> = emptyList(),
        eventRepo: FakeEventRepository = FakeEventRepository(events),
    ) = EventsViewModel(
        eventRepository = eventRepo,
        financeRepository = FakeFinanceRecordRepository(records),
        profileRepository = FakeProfileRepository(currency = CurrencyCode("USD"), onboardingCompleted = true),
        scope = CoroutineScope(UnconfinedTestDispatcher(testScheduler)),
        clock = { now },
        idGenerator = { "new-event" },
    )

    // Rule: an event's spend is the sum of records tagged to it (others ignored).
    @Test
    fun spendSumsTaggedRecordsOnly() = runTest {
        val vm = eventsViewModel(
            events = listOf(event("bali", 200_000)),
            records = listOf(
                tagged("a", 50_000, "bali"),
                tagged("b", 26_000, "bali"),
                tagged("other", 99_000, "someoneelse"),
            ),
        )
        val card = vm.state.value.cards.single()
        assertTrue(card.spentLabel.contains("760.00"))
        assertEquals(2, vm.state.value.details["bali"]?.linkedExpenses?.size)
    }

    // Rule: spend over budget flags the card and detail.
    @Test
    fun overBudgetFlagged() = runTest {
        val vm = eventsViewModel(
            events = listOf(event("party", 40_000)),
            records = listOf(tagged("a", 42_000, "party")),
        )
        assertTrue(vm.state.value.cards.single().isOverBudget)
        assertNotNull(vm.state.value.details["party"]?.summary?.overNoticeLabel)
    }

    // Rule: a closed event is read-only and shows the final summary.
    @Test
    fun closedEventIsReadOnly() = runTest {
        val vm = eventsViewModel(events = listOf(event("wedding", 80_000, EventStatus.CLOSED)))
        val detail = vm.state.value.details["wedding"]
        assertNotNull(detail)
        assertTrue(detail.readOnly)
        assertTrue(detail.summary.isFinal)
    }

    // Rule: create persists a new event with the parsed budget.
    @Test
    fun createPersistsEvent() = runTest {
        val repo = FakeEventRepository()
        val vm = eventsViewModel(eventRepo = repo)

        vm.create("Bali Trip", "2000")

        val saved = repo.store["new-event"]
        assertNotNull(saved)
        assertEquals("Bali Trip", saved.name)
        assertEquals(200_000L, saved.budgetAmount.valueInCents)
    }
}
