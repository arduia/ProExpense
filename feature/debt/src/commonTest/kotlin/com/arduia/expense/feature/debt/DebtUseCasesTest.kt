package com.arduia.expense.feature.debt

import com.arduia.expense.data.DebtRepository
import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.DebtDirection
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordLink
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun sampleRecord(id: String, link: RecordLink) = FinanceRecord(
    id = RecordId(id),
    money = Money(Amount(1000), CurrencyCode("USD")),
    homeCurrencyMoney = Money(Amount(1000), CurrencyCode("USD")),
    categoryId = CategoryId("food"),
    type = RecordType.EXPENSE,
    note = null,
    recordedAtEpochMillis = 0L,
    link = link,
)

private class FakeFinanceRecordRepository(
    private val records: MutableMap<String, FinanceRecord> = mutableMapOf(),
) : FinanceRecordRepository {
    override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(records.values.toList())
    override suspend fun getById(id: RecordId): Result<FinanceRecord?> = Result.Success(records[id.value])
    override suspend fun upsert(record: FinanceRecord): Result<Unit> {
        records[record.id.value] = record
        return Result.Success(Unit)
    }
    override suspend fun delete(id: RecordId): Result<Unit> {
        records.remove(id.value)
        return Result.Success(Unit)
    }
    override fun observeAll() = MutableStateFlow(records.values.toList()).asStateFlow()
    override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = Result.Success(true)
    override suspend fun getRecordsPage(filter: RecordPageFilter, cursor: RecordPageCursor?, limit: Int): Result<List<FinanceRecord>> =
        Result.Success(records.values.toList().take(limit))
    override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> =
        Result.Success(records.values.any { it.categoryId == categoryId })
    override fun observeChangeSignal() =
        MutableStateFlow(RecordChangeSignal(records.size.toLong(), 0L)).asStateFlow()
}

private fun sampleDebt(
    id: String = "d1",
    personName: String = "Alex",
    amountCents: Long = 20_00,
    direction: DebtDirection = DebtDirection.OWED_TO_ME,
    isSettled: Boolean = false,
) = Debt(
    id = DebtId(id),
    personName = personName,
    money = Money(Amount(amountCents), CurrencyCode("USD")),
    direction = direction,
    isSettled = isSettled,
)

private class FakeDebtRepository(
    private val records: MutableMap<String, Debt> = mutableMapOf(),
) : DebtRepository {
    var deletedId: DebtId? = null
    var lastUpsert: Debt? = null

    fun put(debt: Debt) {
        records[debt.id.value] = debt
    }

    override suspend fun getAll(): Result<List<Debt>> = Result.Success(records.values.toList())
    override suspend fun getById(id: DebtId): Result<Debt?> = Result.Success(records[id.value])
    override suspend fun upsert(debt: Debt): Result<Unit> {
        lastUpsert = debt
        records[debt.id.value] = debt
        return Result.Success(Unit)
    }
    override suspend fun delete(id: DebtId): Result<Unit> {
        deletedId = id
        records.remove(id.value)
        return Result.Success(Unit)
    }
    override suspend fun findByPersonName(personName: String): Result<List<Debt>> =
        Result.Success(records.values.filter { it.personName == personName })
    override fun observeAll() = MutableStateFlow(records.values.toList()).asStateFlow()
}

class CreateDebtUseCaseTest {

    @Test
    fun invoke_createsDebtWithParsedAmountAndDirection() = runTest {
        val repo = FakeDebtRepository()
        val useCase = CreateDebtUseCase(repo, nowEpochMillis = { 1_000L })

        val result = useCase("Alex", "12.50", DebtDirection.OWED_TO_ME)

        assertTrue(result)
        assertEquals(1250L, repo.lastUpsert?.money?.amount?.valueInCents)
        assertEquals(DebtDirection.OWED_TO_ME, repo.lastUpsert?.direction)
        assertEquals("alex-1000", repo.lastUpsert?.id?.value)
    }

    @Test
    fun invoke_returnsFalseForUnparsableAmount() = runTest {
        val repo = FakeDebtRepository()
        val useCase = CreateDebtUseCase(repo, nowEpochMillis = { 1_000L })

        val result = useCase("Alex", "not-a-number", DebtDirection.OWED_TO_ME)

        assertFalse(result)
        assertEquals(null, repo.lastUpsert)
    }

    @Test
    fun invoke_returnsFalseForZeroAmount() = runTest {
        val repo = FakeDebtRepository()
        val useCase = CreateDebtUseCase(repo, nowEpochMillis = { 1_000L })

        val result = useCase("Alex", "0", DebtDirection.OWED_TO_ME)

        assertFalse(result)
        assertEquals(null, repo.lastUpsert)
    }

    @Test
    fun invoke_persistsTheOptionalNoteAndBlanksToNull() = runTest {
        val repo = FakeDebtRepository()
        val useCase = CreateDebtUseCase(repo, nowEpochMillis = { 1_000L })

        useCase("Alex", "12.50", DebtDirection.OWED_TO_ME, note = "Dinner split")
        assertEquals("Dinner split", repo.lastUpsert?.note)

        useCase("Alex", "12.50", DebtDirection.OWED_TO_ME, note = "   ")
        assertEquals(null, repo.lastUpsert?.note)
    }
}

class UpdateDebtUseCaseTest {

    @Test
    fun invoke_updatesPersonAmountAndDuePreservingIdAndDirection() = runTest {
        val repo = FakeDebtRepository()
        val useCase = UpdateDebtUseCase(repo)
        val existing = sampleDebt("d1", personName = "Alex", amountCents = 20_00, direction = DebtDirection.I_OWE)

        val result = useCase(existing, "Alexandra", "35.00", 5_000L)

        assertTrue(result)
        val updated = repo.lastUpsert
        assertEquals(DebtId("d1"), updated?.id)
        assertEquals("Alexandra", updated?.personName)
        assertEquals(3500L, updated?.money?.amount?.valueInCents)
        assertEquals(DebtDirection.I_OWE, updated?.direction)
        assertEquals(5_000L, updated?.dueEpochMillis)
    }

    @Test
    fun invoke_returnsFalseForZeroAmount() = runTest {
        val repo = FakeDebtRepository()
        val useCase = UpdateDebtUseCase(repo)
        val existing = sampleDebt("d1")

        val result = useCase(existing, "Alex", "0", null)

        assertFalse(result)
        assertEquals(null, repo.lastUpsert)
    }
}

class DeleteDebtUseCaseTest {

    @Test
    fun invoke_deletesDebtById() = runTest {
        val repo = FakeDebtRepository().apply { put(sampleDebt("d1")) }
        val useCase = DeleteDebtUseCase(repo, FakeFinanceRecordRepository())

        useCase("d1")

        assertEquals(DebtId("d1"), repo.deletedId)
    }

    @Test
    fun invoke_clearsTheLinkOnAnyExpenseTaggedToTheDeletedDebt() = runTest {
        val repo = FakeDebtRepository().apply { put(sampleDebt("d1")) }
        val financeRecordRepo = FakeFinanceRecordRepository(
            mutableMapOf(
                "r1" to sampleRecord("r1", RecordLink.ToDebt(DebtId("d1"))),
                "r2" to sampleRecord("r2", RecordLink.ToDebt(DebtId("other"))),
            ),
        )
        val useCase = DeleteDebtUseCase(repo, financeRecordRepo)

        useCase("d1")

        assertEquals(RecordLink.None, (financeRecordRepo.getAll() as Result.Success).data.first { it.id.value == "r1" }.link)
        assertEquals(RecordLink.ToDebt(DebtId("other")), (financeRecordRepo.getAll() as Result.Success).data.first { it.id.value == "r2" }.link)
    }
}

class SettleDebtUseCaseTest {

    @Test
    fun invoke_marksDebtAsSettled() = runTest {
        val repo = FakeDebtRepository()
        val useCase = SettleDebtUseCase(repo)
        val debt = sampleDebt("d1", isSettled = false)

        useCase(debt)

        assertEquals(true, repo.lastUpsert?.isSettled)
        assertEquals(DebtId("d1"), repo.lastUpsert?.id)
    }
}

class AggregateDebtsUseCaseTest {

    private val useCase = AggregateDebtsUseCase()

    @Test
    fun invoke_filtersByDirectionAndSplitsActiveSettled() {
        val debts = listOf(
            sampleDebt("d1", amountCents = 10_00, direction = DebtDirection.OWED_TO_ME, isSettled = false),
            sampleDebt("d2", amountCents = 20_00, direction = DebtDirection.OWED_TO_ME, isSettled = true),
            sampleDebt("d3", amountCents = 30_00, direction = DebtDirection.I_OWE, isSettled = false),
        )

        val aggregate = useCase(debts, DebtDirection.OWED_TO_ME)

        assertEquals(1000L, aggregate.netCents)
        assertEquals(listOf(DebtId("d1")), aggregate.active.map { it.id })
        assertEquals(listOf(DebtId("d2")), aggregate.settled.map { it.id })
    }

    @Test
    fun invoke_sumsOnlyActiveDebtsForNetCents() {
        val debts = listOf(
            sampleDebt("d1", amountCents = 10_00, isSettled = false),
            sampleDebt("d2", amountCents = 99_00, isSettled = true),
        )

        val aggregate = useCase(debts, DebtDirection.OWED_TO_ME)

        assertEquals(1000L, aggregate.netCents)
    }
}

class CheckDebtConflictUseCaseTest {

    @Test
    fun invoke_returnsTrueWhenPersonHasActiveRecordOnOppositeSide() = runTest {
        val repo = FakeDebtRepository().apply {
            put(sampleDebt("d1", personName = "Alex", direction = DebtDirection.I_OWE, isSettled = false))
        }
        val useCase = CheckDebtConflictUseCase(repo)

        val result = useCase("Alex", DebtDirection.OWED_TO_ME)

        assertTrue(result)
    }

    @Test
    fun invoke_returnsFalseWhenOppositeSideRecordIsSettled() = runTest {
        val repo = FakeDebtRepository().apply {
            put(sampleDebt("d1", personName = "Alex", direction = DebtDirection.I_OWE, isSettled = true))
        }
        val useCase = CheckDebtConflictUseCase(repo)

        val result = useCase("Alex", DebtDirection.OWED_TO_ME)

        assertFalse(result)
    }

    @Test
    fun invoke_returnsFalseWhenNoMatchingPerson() = runTest {
        val repo = FakeDebtRepository()
        val useCase = CheckDebtConflictUseCase(repo)

        val result = useCase("Alex", DebtDirection.OWED_TO_ME)

        assertFalse(result)
    }
}
