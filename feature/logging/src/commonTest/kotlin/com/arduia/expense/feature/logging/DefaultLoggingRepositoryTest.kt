package com.arduia.expense.feature.logging

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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultLoggingRepositoryTest {

    private class FakeFinanceRecordRepository(
        var upsertResult: Result<Unit> = Result.Success(Unit),
    ) : FinanceRecordRepository {
        val stored = MutableStateFlow<List<FinanceRecord>>(emptyList())

        override suspend fun getAll(): Result<List<FinanceRecord>> = Result.Success(stored.value)
        override suspend fun getById(id: RecordId): Result<FinanceRecord?> =
            Result.Success(stored.value.firstOrNull { it.id == id })

        override suspend fun upsert(record: FinanceRecord): Result<Unit> {
            if (upsertResult is Result.Success) {
                stored.value = stored.value.filterNot { it.id == record.id } + record
            }
            return upsertResult
        }

        override suspend fun delete(id: RecordId): Result<Unit> {
            stored.value = stored.value.filterNot { it.id == id }
            return Result.Success(Unit)
        }

        override fun observeAll(): Flow<List<FinanceRecord>> = stored

        override suspend fun verifyIntegrity(id: RecordId): Result<Boolean> =
            Result.Success(stored.value.any { it.id == id })

        override suspend fun getRecordsPage(filter: RecordPageFilter, cursor: RecordPageCursor?, limit: Int): Result<List<FinanceRecord>> =
            Result.Success(stored.value.take(limit))

        override suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean> =
            Result.Success(stored.value.any { it.categoryId == categoryId })

        override fun observeChangeSignal() =
            MutableStateFlow(RecordChangeSignal(stored.value.size.toLong(), 0L))
    }

    private fun input() = LogRecordInput(
        money = Money(Amount(2_500), CurrencyCode("USD")),
        homeCurrencyMoney = Money(Amount(2_500), CurrencyCode("USD")),
        categoryId = CategoryId("food"),
        recordedAtEpochMillis = 42,
    )

    @Test
    fun createRecord_generatesId_andPersists() = runTest {
        val fake = FakeFinanceRecordRepository()
        val repo = DefaultLoggingRepository(fake, idGenerator = { "fixed-id" })

        val result = repo.createRecord(input())

        assertTrue(result is Result.Success)
        assertEquals(RecordId("fixed-id"), result.data.id)
        assertEquals(listOf(RecordId("fixed-id")), fake.stored.value.map { it.id })
    }

    @Test
    fun createRecord_propagatesStorageError_withoutFabricatingSuccess() = runTest {
        val fake = FakeFinanceRecordRepository(upsertResult = Result.Error("disk full"))
        val repo = DefaultLoggingRepository(fake, idGenerator = { "fixed-id" })

        val result = repo.createRecord(input())

        assertTrue(result is Result.Error)
        assertEquals("disk full", result.message)
        assertTrue(fake.stored.value.isEmpty())
    }

    @Test
    fun deleteRecord_delegatesToContract() = runTest {
        val fake = FakeFinanceRecordRepository()
        val repo = DefaultLoggingRepository(fake, idGenerator = { "fixed-id" })
        repo.createRecord(input())

        val result = repo.deleteRecord(RecordId("fixed-id"))

        assertTrue(result is Result.Success)
        assertTrue(fake.stored.value.isEmpty())
    }
}
