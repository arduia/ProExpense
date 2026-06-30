package com.arduia.expense.feature.eventbudget

import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun sampleEvent(budgetCents: Long) = Event(
    id = EventId("ev1"),
    name = "Trip",
    startEpochMillis = 100,
    endEpochMillis = 200,
    budget = Money(Amount(budgetCents), CurrencyCode("USD")),
    status = EventStatus.ACTIVE,
)

class ComputeEventProgressUseCaseTest {

    private val useCase = ComputeEventProgressUseCase()

    @Test
    fun invoke_computesRemainingAndProgressUnderBudget() {
        val event = sampleEvent(budgetCents = 10_00)
        val spent = Money(Amount(4_00), CurrencyCode("USD"))

        val progress = useCase(event, spent)

        assertEquals(400L, progress.spentCents)
        assertEquals(1000L, progress.budgetCents)
        assertEquals(600L, progress.remainingCents)
        assertEquals(0.4f, progress.progress)
        assertFalse(progress.isOverBudget)
    }

    @Test
    fun invoke_flagsOverBudgetAndClampsProgressAtOne() {
        val event = sampleEvent(budgetCents = 10_00)
        val spent = Money(Amount(15_00), CurrencyCode("USD"))

        val progress = useCase(event, spent)

        assertEquals(-500L, progress.remainingCents)
        assertEquals(1f, progress.progress)
        assertTrue(progress.isOverBudget)
    }

    @Test
    fun invoke_treatsNullSpentAsZero() {
        val event = sampleEvent(budgetCents = 10_00)

        val progress = useCase(event, null)

        assertEquals(0L, progress.spentCents)
        assertEquals(0f, progress.progress)
        assertFalse(progress.isOverBudget)
    }

    @Test
    fun invoke_avoidsDivisionByZeroWhenBudgetIsZero() {
        val event = sampleEvent(budgetCents = 0)
        val spent = Money(Amount(5_00), CurrencyCode("USD"))

        val progress = useCase(event, spent)

        assertEquals(0f, progress.progress)
        assertTrue(progress.isOverBudget)
    }
}

private class FakeEventRepository : EventRepository {
    private val flow = MutableStateFlow<List<Event>>(emptyList())
    var lastUpsert: Event? = null

    override suspend fun getAll(): Result<List<Event>> = Result.Success(flow.value)
    override suspend fun getById(id: EventId): Result<Event?> = Result.Success(flow.value.find { it.id == id })
    override suspend fun upsert(event: Event): Result<Unit> {
        lastUpsert = event
        return Result.Success(Unit)
    }
    override suspend fun delete(id: EventId): Result<Unit> = Result.Success(Unit)
    override fun observeAll() = flow.asStateFlow()
    override suspend fun getSpent(id: EventId): Result<Money> = Result.Success(Money.zero(CurrencyCode("USD")))
}

class CreateEventUseCaseTest {

    @Test
    fun invoke_createsActiveEventWithParsedBudget() = runTest {
        val repo = FakeEventRepository()
        val useCase = CreateEventUseCase(repo, nowEpochMillis = { 1_000L })

        val result = useCase("Trip", "12.50")

        assertTrue(result)
        assertEquals(1250L, repo.lastUpsert?.budget?.amount?.valueInCents)
        assertEquals(EventStatus.ACTIVE, repo.lastUpsert?.status)
        assertEquals("trip-1000", repo.lastUpsert?.id?.value)
    }

    @Test
    fun invoke_returnsFalseForUnparsableBudget() = runTest {
        val repo = FakeEventRepository()
        val useCase = CreateEventUseCase(repo, nowEpochMillis = { 1_000L })

        val result = useCase("Trip", "not-a-number")

        assertFalse(result)
        assertEquals(null, repo.lastUpsert)
    }
}

class UpdateEventUseCaseTest {

    @Test
    fun invoke_appliesNewNameBudgetAndDates() = runTest {
        val repo = FakeEventRepository()
        val useCase = UpdateEventUseCase(repo)
        val existing = sampleEvent(budgetCents = 10_00)

        val result = useCase(existing, "Trip 2", "20.00", startEpochMillis = 300, endEpochMillis = 400)

        assertTrue(result)
        assertEquals("Trip 2", repo.lastUpsert?.name)
        assertEquals(2000L, repo.lastUpsert?.budget?.amount?.valueInCents)
        assertEquals(300L, repo.lastUpsert?.startEpochMillis)
        assertEquals(400L, repo.lastUpsert?.endEpochMillis)
    }

    @Test
    fun invoke_returnsFalseForUnparsableBudget() = runTest {
        val repo = FakeEventRepository()
        val useCase = UpdateEventUseCase(repo)
        val existing = sampleEvent(budgetCents = 10_00)

        val result = useCase(existing, "Trip 2", "not-a-number", startEpochMillis = 300, endEpochMillis = 400)

        assertFalse(result)
        assertEquals(null, repo.lastUpsert)
    }

    @Test
    fun invoke_returnsFalseForZeroBudget() = runTest {
        val repo = FakeEventRepository()
        val useCase = UpdateEventUseCase(repo)
        val existing = sampleEvent(budgetCents = 10_00)

        val result = useCase(existing, "Trip 2", "0.00", startEpochMillis = 300, endEpochMillis = 400)

        assertFalse(result)
        assertEquals(null, repo.lastUpsert)
    }

    @Test
    fun invoke_returnsFalseWhenEndBeforeStart() = runTest {
        val repo = FakeEventRepository()
        val useCase = UpdateEventUseCase(repo)
        val existing = sampleEvent(budgetCents = 10_00)

        val result = useCase(existing, "Trip 2", "20.00", startEpochMillis = 400, endEpochMillis = 300)

        assertFalse(result)
        assertEquals(null, repo.lastUpsert)
    }
}

class CloseEventUseCaseTest {

    @Test
    fun invoke_closesActiveEvent() = runTest {
        val repo = FakeEventRepository()
        val useCase = CloseEventUseCase(repo)
        val existing = sampleEvent(budgetCents = 10_00)

        val result = useCase(existing)

        assertTrue(result)
        assertEquals(EventStatus.CLOSED, repo.lastUpsert?.status)
    }

    @Test
    fun invoke_returnsFalseWhenAlreadyClosed() = runTest {
        val repo = FakeEventRepository()
        val useCase = CloseEventUseCase(repo)
        val existing = sampleEvent(budgetCents = 10_00).copy(status = EventStatus.CLOSED)

        val result = useCase(existing)

        assertFalse(result)
        assertEquals(null, repo.lastUpsert)
    }
}
