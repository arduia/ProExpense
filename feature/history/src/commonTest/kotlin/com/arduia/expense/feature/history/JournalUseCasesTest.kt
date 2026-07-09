package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.RecordChangeSignal
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.RecordPageFilter
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

private fun sampleRecord(
    id: String = "r1",
    note: String? = "lunch",
) = FinanceRecord(
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

    override suspend fun getRecordsPage(
        filter: RecordPageFilter,
        cursor: RecordPageCursor?,
        limit: Int,
    ): Result<List<FinanceRecord>> = Result.Success(records.values.toList().take(limit))

    override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> =
        Result.Success(records.values.any { it.categoryId == categoryId })

    override fun observeChangeSignal() = MutableStateFlow(RecordChangeSignal(records.size.toLong(), 0L)).asStateFlow()
}

class DeleteRecordUseCaseTest {
    @Test
    fun invoke_deletesRecordById() =
        runTest {
            val repo = FakeFinanceRecordRepository().apply { put(sampleRecord("r1")) }
            val useCase = DeleteRecordUseCase(repo)

            useCase("r1")

            assertEquals(RecordId("r1"), repo.deletedId)
        }
}

class UpdateRecordNoteUseCaseTest {
    @Test
    fun invoke_updatesNoteLeavingOtherFieldsUntouched() =
        runTest {
            val repo = FakeFinanceRecordRepository().apply { put(sampleRecord("r1", note = "old")) }
            val useCase = UpdateRecordNoteUseCase(repo)

            useCase("r1", "new note")

            assertEquals("new note", repo.lastUpsert?.note)
            assertEquals(CategoryId("food"), repo.lastUpsert?.categoryId)
        }

    @Test
    fun invoke_blankNoteIsStoredAsNull() =
        runTest {
            val repo = FakeFinanceRecordRepository().apply { put(sampleRecord("r1", note = "old")) }
            val useCase = UpdateRecordNoteUseCase(repo)

            useCase("r1", "   ")

            assertNull(repo.lastUpsert?.note)
        }

    @Test
    fun invoke_doesNothingWhenRecordMissing() =
        runTest {
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
    var lastCursor: RecordPageCursor? = null
    var lastLimit: Int? = null

    override suspend fun getRecords(filter: RecordHistoryFilter): Result<List<FinanceRecord>> {
        lastFilter = filter
        return recordsResult
    }

    override suspend fun getSummary(
        period: SummaryPeriod,
        anchorEpochMillis: Long,
    ): Result<RecordSummary> = Result.Error("not implemented")

    override suspend fun getRecordsPage(
        filter: RecordHistoryFilter,
        cursor: RecordPageCursor?,
        limit: Int,
    ): Result<List<FinanceRecord>> {
        lastFilter = filter
        lastCursor = cursor
        lastLimit = limit
        return recordsResult
    }

    override suspend fun hasAnyRecordIn(categoryId: CategoryId): Result<Boolean> = Result.Success(false)

    override fun observeChangeSignal() = MutableStateFlow(RecordChangeSignal(0L, 0L)).asStateFlow()
}

class LoadJournalPageUseCaseTest {
    @Test
    fun invoke_delegatesToRepositoryWithFilterCursorAndLimit() =
        runTest {
            val record = sampleRecord("r1")
            val repo = FakeHistoryRepository(recordsResult = Result.Success(listOf(record)))
            val useCase = LoadJournalPageUseCase(repo)
            val filter = RecordHistoryFilter(categoryId = CategoryId("food"), query = "lunch")
            val cursor = RecordPageCursor(recordedAtEpochMillis = 500L, recordId = RecordId("r0"))

            val result = useCase(filter, cursor, limit = 20)

            assertIs<Result.Success<List<FinanceRecord>>>(result)
            assertEquals(listOf(record), result.data)
            assertEquals(filter, repo.lastFilter)
            assertEquals(cursor, repo.lastCursor)
            assertEquals(20, repo.lastLimit)
        }

    @Test
    fun invoke_defaultsToFirstPageAtDefaultPageSize() =
        runTest {
            val repo = FakeHistoryRepository()
            val useCase = LoadJournalPageUseCase(repo)

            useCase(RecordHistoryFilter())

            assertEquals(null, repo.lastCursor)
            assertEquals(LoadJournalPageUseCase.DEFAULT_PAGE_SIZE, repo.lastLimit)
        }

    @Test
    fun invoke_propagatesRepositoryError() =
        runTest {
            val repo = FakeHistoryRepository(recordsResult = Result.Error("db error"))
            val useCase = LoadJournalPageUseCase(repo)

            val result = useCase(RecordHistoryFilter(), limit = 20)

            assertIs<Result.Error>(result)
        }
}
