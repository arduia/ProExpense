package com.arduia.expense.feature.logging

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.DebtId
import com.arduia.expense.domain.EventId
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
import kotlin.test.assertIs

private fun sampleRecord(
    id: String = "r1",
    note: String? = null,
    link: RecordLink = RecordLink.None,
) = FinanceRecord(
    id = RecordId(id),
    money = Money(Amount(1000), CurrencyCode("USD")),
    homeCurrencyMoney = Money(Amount(1000), CurrencyCode("USD")),
    categoryId = CategoryId("food"),
    type = RecordType.EXPENSE,
    note = note,
    recordedAtEpochMillis = 1_000L,
    link = link,
)

private fun sampleInput(
    rawAmount: String = "10.00",
    linkTagId: String? = null,
    linkTagKind: TagOptionKind? = null,
) = SaveExpenseInput(
    rawAmount = rawAmount,
    currencyCode = "USD",
    categoryId = "food",
    note = "",
    recordedAtEpochMillis = 2_000L,
    linkTagId = linkTagId,
    linkTagKind = linkTagKind,
)

class LogExpenseUseCaseTest {

    private class FakeLoggingRepository(
        var createResult: Result<FinanceRecord>? = null,
    ) : LoggingRepository {
        var lastInput: LogRecordInput? = null

        override suspend fun createRecord(input: LogRecordInput): Result<FinanceRecord> {
            lastInput = input
            return createResult ?: Result.Success(
                FinanceRecord(
                    id = RecordId("new"),
                    money = input.money,
                    homeCurrencyMoney = input.homeCurrencyMoney,
                    categoryId = input.categoryId,
                    type = input.type,
                    note = input.note,
                    recordedAtEpochMillis = input.recordedAtEpochMillis,
                    link = input.link,
                ),
            )
        }

        override suspend fun updateRecord(record: FinanceRecord): Result<Unit> = Result.Success(Unit)
        override suspend fun deleteRecord(id: RecordId): Result<Unit> = Result.Success(Unit)
    }

    @Test
    fun invoke_savesValidAmountAsExpense() = runTest {
        val repo = FakeLoggingRepository()
        val useCase = LogExpenseUseCase(repo)

        val outcome = useCase(sampleInput(rawAmount = "12.50"))

        assertIs<SaveExpenseOutcome.Saved>(outcome)
        assertEquals(1250L, repo.lastInput?.money?.amount?.valueInCents)
    }

    @Test
    fun invoke_returnsInvalidAmountForUnparsableInput() = runTest {
        val repo = FakeLoggingRepository()
        val useCase = LogExpenseUseCase(repo)

        val outcome = useCase(sampleInput(rawAmount = "not-a-number"))

        assertEquals(SaveExpenseOutcome.InvalidAmount, outcome)
    }

    @Test
    fun invoke_mapsRepositoryErrorToFailedOutcome() = runTest {
        val repo = FakeLoggingRepository(createResult = Result.Error("disk full"))
        val useCase = LogExpenseUseCase(repo)

        val outcome = useCase(sampleInput())

        assertEquals(SaveExpenseOutcome.Failed("disk full"), outcome)
    }

    @Test
    fun invoke_linksToEventWhenTagKindIsEvent() = runTest {
        val repo = FakeLoggingRepository()
        val useCase = LogExpenseUseCase(repo)

        useCase(sampleInput(linkTagId = "ev1", linkTagKind = TagOptionKind.EVENT))

        assertEquals(RecordLink.ToEvent(EventId("ev1")), repo.lastInput?.link)
    }
}

class UpdateExpenseUseCaseTest {

    private class FakeFinanceRecordRepository(
        var upsertResult: Result<Unit> = Result.Success(Unit),
    ) : FinanceRecordRepository {
        var lastUpsert: FinanceRecord? = null

        override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(emptyList())
        override suspend fun getById(id: RecordId): Result<FinanceRecord?> = Result.Success(null)
        override suspend fun upsert(record: FinanceRecord): Result<Unit> {
            lastUpsert = record
            return upsertResult
        }
        override suspend fun delete(id: RecordId): Result<Unit> = Result.Success(Unit)
        override fun observeAll() = MutableStateFlow<List<FinanceRecord>>(emptyList()).asStateFlow()
        override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = Result.Success(true)
    }

    @Test
    fun invoke_appliesNewAmountAndCategory() = runTest {
        val repo = FakeFinanceRecordRepository()
        val useCase = UpdateExpenseUseCase(repo)
        val existing = sampleRecord()

        val outcome = useCase(existing, sampleInput(rawAmount = "20.00"))

        assertIs<SaveExpenseOutcome.Saved>(outcome)
        assertEquals(2000L, repo.lastUpsert?.money?.amount?.valueInCents)
    }

    @Test
    fun invoke_preservesSharedCostLinkWhenTagUntouched() = runTest {
        val repo = FakeFinanceRecordRepository()
        val useCase = UpdateExpenseUseCase(repo)
        val existing = sampleRecord(link = RecordLink.ToSharedCost(com.arduia.expense.domain.SharedCostId("sc1")))

        useCase(existing, sampleInput(linkTagId = null, linkTagKind = null))

        assertEquals(RecordLink.ToSharedCost(com.arduia.expense.domain.SharedCostId("sc1")), repo.lastUpsert?.link)
    }

    @Test
    fun invoke_replacesLinkWhenTagExplicitlyChanged() = runTest {
        val repo = FakeFinanceRecordRepository()
        val useCase = UpdateExpenseUseCase(repo)
        val existing = sampleRecord(link = RecordLink.ToSharedCost(com.arduia.expense.domain.SharedCostId("sc1")))

        useCase(existing, sampleInput(linkTagId = "d1", linkTagKind = TagOptionKind.DEBT))

        assertEquals(RecordLink.ToDebt(DebtId("d1")), repo.lastUpsert?.link)
    }

    @Test
    fun invoke_returnsInvalidAmountForUnparsableInput() = runTest {
        val repo = FakeFinanceRecordRepository()
        val useCase = UpdateExpenseUseCase(repo)

        val outcome = useCase(sampleRecord(), sampleInput(rawAmount = "bad"))

        assertEquals(SaveExpenseOutcome.InvalidAmount, outcome)
    }

    @Test
    fun invoke_mapsRepositoryErrorToFailedOutcome() = runTest {
        val repo = FakeFinanceRecordRepository(upsertResult = Result.Error("disk full"))
        val useCase = UpdateExpenseUseCase(repo)

        val outcome = useCase(sampleRecord(), sampleInput())

        assertEquals(SaveExpenseOutcome.Failed("disk full"), outcome)
    }
}

class LoadExpenseForEditUseCaseTest {

    private class FakeFinanceRecordRepository(
        private val byId: Map<String, FinanceRecord>,
    ) : FinanceRecordRepository {
        override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(byId.values.toList())
        override suspend fun getById(id: RecordId): Result<FinanceRecord?> = Result.Success(byId[id.value])
        override suspend fun upsert(record: FinanceRecord): Result<Unit> = Result.Success(Unit)
        override suspend fun delete(id: RecordId): Result<Unit> = Result.Success(Unit)
        override fun observeAll() = MutableStateFlow<List<FinanceRecord>>(emptyList()).asStateFlow()
        override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = Result.Success(true)
    }

    @Test
    fun invoke_returnsRecordWhenFound() = runTest {
        val record = sampleRecord(id = "r1")
        val useCase = LoadExpenseForEditUseCase(FakeFinanceRecordRepository(mapOf("r1" to record)))

        val result = useCase("r1")

        assertEquals(record, result)
    }

    @Test
    fun invoke_returnsNullWhenNotFound() = runTest {
        val useCase = LoadExpenseForEditUseCase(FakeFinanceRecordRepository(emptyMap()))

        val result = useCase("missing")

        assertEquals(null, result)
    }
}
