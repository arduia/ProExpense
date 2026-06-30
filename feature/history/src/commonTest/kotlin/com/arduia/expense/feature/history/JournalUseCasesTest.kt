package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

private fun sampleRecord(id: String = "r1", note: String? = "lunch") = FinanceRecord(
    id = RecordId(id),
    money = Money(Amount(1000), CurrencyCode("USD")),
    homeCurrencyMoney = Money(Amount(1000), CurrencyCode("USD")),
    categoryId = CategoryId("food"),
    type = RecordType.EXPENSE,
    note = note,
    recordedAtEpochMillis = 1_000L,
)

private class FakeFinanceRecordRepository(
    private val records: MutableMap<String, FinanceRecord> = mutableMapOf(),
) : FinanceRecordRepository {
    var deletedId: RecordId? = null
    var lastUpsert: FinanceRecord? = null

    fun put(record: FinanceRecord) {
        records[record.id.value] = record
    }

    override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(records.values.toList())
    override suspend fun getById(id: RecordId): Result<FinanceRecord?> = Result.Success(records[id.value])
    override suspend fun upsert(record: FinanceRecord): Result<Unit> {
        lastUpsert = record
        records[record.id.value] = record
        return Result.Success(Unit)
    }
    override suspend fun delete(id: RecordId): Result<Unit> {
        deletedId = id
        records.remove(id.value)
        return Result.Success(Unit)
    }
    override fun observeAll() = MutableStateFlow<List<FinanceRecord>>(emptyList()).asStateFlow()
    override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> = Result.Success(true)
}

class DeleteRecordUseCaseTest {

    @Test
    fun invoke_deletesRecordById() = runTest {
        val repo = FakeFinanceRecordRepository().apply { put(sampleRecord("r1")) }
        val useCase = DeleteRecordUseCase(repo)

        useCase("r1")

        assertEquals(RecordId("r1"), repo.deletedId)
    }
}

class UpdateRecordNoteUseCaseTest {

    @Test
    fun invoke_updatesNoteLeavingOtherFieldsUntouched() = runTest {
        val repo = FakeFinanceRecordRepository().apply { put(sampleRecord("r1", note = "old")) }
        val useCase = UpdateRecordNoteUseCase(repo)

        useCase("r1", "new note")

        assertEquals("new note", repo.lastUpsert?.note)
        assertEquals(CategoryId("food"), repo.lastUpsert?.categoryId)
    }

    @Test
    fun invoke_blankNoteIsStoredAsNull() = runTest {
        val repo = FakeFinanceRecordRepository().apply { put(sampleRecord("r1", note = "old")) }
        val useCase = UpdateRecordNoteUseCase(repo)

        useCase("r1", "   ")

        assertNull(repo.lastUpsert?.note)
    }

    @Test
    fun invoke_doesNothingWhenRecordMissing() = runTest {
        val repo = FakeFinanceRecordRepository()
        val useCase = UpdateRecordNoteUseCase(repo)

        useCase("missing", "note")

        assertEquals(null, repo.lastUpsert)
    }
}

private class FakeHistoryRepository(
    var recordsResult: Result<List<FinanceRecord>> = Result.Success(emptyList()),
) : HistoryRepository {
    var lastFilter: RecordHistoryFilter? = null

    override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> {
        lastFilter = filter
        return recordsResult
    }

    override suspend fun getSummary(period: SummaryPeriod, anchorEpochMillis: Long): Result<RecordSummary> =
        Result.Error("not implemented")
}

class SearchRecordsUseCaseTest {

    @Test
    fun invoke_delegatesToRepositoryWithFilter() = runTest {
        val record = sampleRecord("r1")
        val repo = FakeHistoryRepository(recordsResult = Result.Success(listOf(record)))
        val useCase = SearchRecordsUseCase(repo)
        val filter = RecordHistoryFilter(categoryId = CategoryId("food"), query = "lunch")

        val result = useCase(filter)

        assertIs<Result.Success<List<FinanceRecord>>>(result)
        assertEquals(listOf(record), result.data)
        assertEquals(filter, repo.lastFilter)
    }

    @Test
    fun invoke_propagatesRepositoryError() = runTest {
        val repo = FakeHistoryRepository(recordsResult = Result.Error("db error"))
        val useCase = SearchRecordsUseCase(repo)

        val result = useCase(RecordHistoryFilter())

        assertIs<Result.Error>(result)
    }
}
