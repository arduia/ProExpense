package com.arduia.expense.feature.logging

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.EventRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.Event
import com.arduia.expense.domain.EventId
import com.arduia.expense.domain.EventStatus
import com.arduia.expense.domain.Money
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeEventRepository(
    initial: List<Event> = emptyList(),
) : EventRepository {
    private val flow = MutableStateFlow(initial)

    override suspend fun getAll(): Result<List<Event>> = Result.Success(flow.value)

    override suspend fun getById(id: EventId): Result<Event?> = Result.Success(flow.value.find { it.id == id })

    override suspend fun upsert(event: Event): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: EventId): Result<Unit> = Result.Success(Unit)

    override fun observeAll() = flow.asStateFlow()

    override suspend fun getSpent(id: EventId): Result<Money> = Result.Success(Money.zero(CurrencyCode("USD")))
}

private class FakeDebtRepository(
    initial: List<Debt> = emptyList(),
) : DebtRepository {
    private val flow = MutableStateFlow(initial)

    override suspend fun getAll(): Result<List<Debt>> = Result.Success(flow.value)

    override suspend fun getById(id: DebtId): Result<Debt?> = Result.Success(flow.value.find { it.id == id })

    override suspend fun upsert(debt: Debt): Result<Unit> = Result.Success(Unit)

    override suspend fun delete(id: DebtId): Result<Unit> = Result.Success(Unit)

    override suspend fun findByPersonName(personName: String): Result<List<Debt>> = Result.Success(emptyList())

    override fun observeAll() = flow.asStateFlow()
}

class ObserveTagOptionsUseCaseTest {
    @Test
    fun invoke_combinesEventsAndDebtsIntoTagOptions() =
        runTest {
            val event =
                Event(
                    id = EventId("ev1"),
                    name = "Trip",
                    startEpochMillis = 100,
                    endEpochMillis = 200,
                    budget = Money(Amount(50_00), CurrencyCode("USD")),
                    status = EventStatus.ACTIVE,
                )
            val debt =
                Debt(
                    id = DebtId("d1"),
                    personName = "Alex",
                    money = Money(Amount(20_00), CurrencyCode("USD")),
                    direction = DebtDirection.OWED_TO_ME,
                )
            val useCase = ObserveTagOptionsUseCase(FakeEventRepository(listOf(event)), FakeDebtRepository(listOf(debt)))

            val options = useCase().first()

            assertEquals(2, options.size)
            assertEquals(setOf("ev1", "d1"), options.map { it.id }.toSet())
        }

    @Test
    fun invoke_marksClosedEventsSoTheyCanBeExcludedFromLinkableOptions() =
        runTest {
            val active =
                Event(
                    id = EventId("ev1"),
                    name = "Trip",
                    startEpochMillis = 100,
                    endEpochMillis = 200,
                    budget = Money(Amount(50_00), CurrencyCode("USD")),
                    status = EventStatus.ACTIVE,
                )
            val closed =
                Event(
                    id = EventId("ev2"),
                    name = "Wedding",
                    startEpochMillis = 100,
                    endEpochMillis = 200,
                    budget = Money(Amount(50_00), CurrencyCode("USD")),
                    status = EventStatus.CLOSED,
                )
            val useCase = ObserveTagOptionsUseCase(FakeEventRepository(listOf(active, closed)), FakeDebtRepository())

            val options = useCase().first()

            assertEquals(false, options.first { it.id == "ev1" }.eventIsClosed)
            assertEquals(true, options.first { it.id == "ev2" }.eventIsClosed)
        }

    @Test
    fun invoke_emitsEmptyListWhenNoEventsOrDebts() =
        runTest {
            val useCase = ObserveTagOptionsUseCase(FakeEventRepository(), FakeDebtRepository())

            val options = useCase().first()

            assertEquals(emptyList(), options)
        }
}
