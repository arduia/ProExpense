package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.data.YearMonth
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import com.arduia.expense.storage.db.ProExpenseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDelightSyncStateRepositoryTest {
    private fun financeRepository(database: ProExpenseDatabase) =
        SqlDelightFinanceRecordRepository(
            queries = database.financeRecordQueries,
            eventQueries = database.eventQueries,
            tombstoneQueries = database.financeRecordTombstoneQueries,
            monthSyncQueries = database.financeRecordMonthSyncQueries,
            dispatcher = Dispatchers.Unconfined,
        )

    private fun syncStateRepository(database: ProExpenseDatabase) =
        SqlDelightSyncStateRepository(
            queries = database.financeRecordQueries,
            tombstoneQueries = database.financeRecordTombstoneQueries,
            monthSyncQueries = database.financeRecordMonthSyncQueries,
            dispatcher = Dispatchers.Unconfined,
        )

    // 2026-07-15T00:00:00Z
    private fun record(
        id: String,
        recordedAtEpochMillis: Long = 1_784_073_600_000,
    ) = FinanceRecord(
        id = RecordId(id),
        money = Money(Amount(1_000), CurrencyCode("USD")),
        homeCurrencyMoney = Money(Amount(1_000), CurrencyCode("USD")),
        categoryId = CategoryId("food"),
        type = RecordType.EXPENSE,
        note = null,
        recordedAtEpochMillis = recordedAtEpochMillis,
    )

    // Key invariant: every newly-inserted record starts dirty, so a fresh connect (US-SYNC-2) has
    // something to push without any special-casing.
    @Test
    fun insert_defaultsDirtyToTrue() =
        runTest {
            val database = inMemoryDatabase()
            financeRepository(database).upsert(record("rec-1"))

            val row = database.financeRecordQueries.selectRecordById("rec-1").executeAsOne()
            assertEquals(1L, row.dirty)
        }

    @Test
    fun getDirtyMonths_includesMonthOfDirtyRecord() =
        runTest {
            val database = inMemoryDatabase()
            financeRepository(database).upsert(record("rec-1"))

            val months = (syncStateRepository(database).getDirtyMonths() as Result.Success).data
            assertEquals(listOf(YearMonth("2026-07")), months)
        }

    // Primary failure mode this schema exists to prevent: delete() must not silently drop the
    // record without a trace — a tombstone lets a later Drive pull know it was deleted here.
    @Test
    fun delete_writesTombstone_insteadOfSilentHardDeleteOnly() =
        runTest {
            val database = inMemoryDatabase()
            financeRepository(database).upsert(record("rec-1"))

            financeRepository(database).delete(RecordId("rec-1"))

            val tombstones =
                (syncStateRepository(database).getTombstonesForMonth(YearMonth("2026-07")) as Result.Success).data
            assertEquals(listOf(RecordId("rec-1")), tombstones.map { it.id })
            assertTrue(database.financeRecordQueries.selectRecordById("rec-1").executeAsOneOrNull() == null)
        }

    // US-SYNC-3 Business Rule: backdating a record out of its month must not leave that month's
    // remote file stale — the old month needs to be flagged dirty even though the record itself no
    // longer lives there.
    @Test
    fun upsert_movingRecordToDifferentMonth_marksOldMonthDirty() =
        runTest {
            val database = inMemoryDatabase()
            val repo = financeRepository(database)
            repo.upsert(record("rec-1", recordedAtEpochMillis = 1_784_073_600_000)) // 2026-07
            syncStateRepository(database).markMonthPushed(YearMonth("2026-07"), "file-1", 1L)

            // Move to 2026-08 (2026-08-15T00:00:00Z)
            repo.upsert(record("rec-1", recordedAtEpochMillis = 1_786_752_000_000))

            val dirtyMonths = (syncStateRepository(database).getDirtyMonths() as Result.Success).data.toSet()
            assertTrue(YearMonth("2026-07") in dirtyMonths, "old month must be flagged dirty: $dirtyMonths")
            assertTrue(YearMonth("2026-08") in dirtyMonths, "new month must be dirty (freshly inserted record): $dirtyMonths")
        }

    @Test
    fun markMonthPushed_clearsDirtyFlagsForThatMonth() =
        runTest {
            val database = inMemoryDatabase()
            financeRepository(database).upsert(record("rec-1"))
            val repo = syncStateRepository(database)

            repo.markMonthPushed(YearMonth("2026-07"), "remote-file-1", 42L)

            val months = (repo.getDirtyMonths() as Result.Success).data
            assertEquals(emptyList<YearMonth>(), months)
            val row = database.financeRecordQueries.selectRecordById("rec-1").executeAsOne()
            assertEquals(0L, row.dirty)
        }
}
