package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDelightClearDataRepositoryTest {

    private fun seedRecord(database: com.arduia.expense.storage.db.ProExpenseDatabase, id: String, tagType: String?, tagId: String?) {
        database.financeRecordQueries.insertRecord(
            id = id,
            amount_cents = 1_000,
            currency_code = "USD",
            home_amount_cents = null,
            category_id = "food",
            type = 0L,
            note = null,
            recorded_at = 1_000,
            updated_at = 1_000,
            tag_type = tagType,
            tag_id = tagId,
            integrity_algo = "SHA-256",
            integrity_hash = "dummy",
            home_currency_code = null,
        )
    }

    @Test
    fun clearEvents_detachesDanglingEventLinks_onSurvivingRecords() = runTest {
        val database = inMemoryDatabase()
        seedRecord(database, "rec-1", "EVENT", "evt-1")
        seedRecord(database, "rec-2", null, null)
        val repo = SqlDelightClearDataRepository(database, Dispatchers.Unconfined)

        assertTrue(repo.clearEvents() is Result.Success)

        val row = database.financeRecordQueries.selectRecordById("rec-1").executeAsOne()
        assertEquals(null, row.tag_type)
        assertEquals(null, row.tag_id)
    }

    @Test
    fun clearDebts_detachesDanglingDebtLinks_onSurvivingRecords() = runTest {
        val database = inMemoryDatabase()
        seedRecord(database, "rec-1", "DEBT", "debt-1")
        val repo = SqlDelightClearDataRepository(database, Dispatchers.Unconfined)

        assertTrue(repo.clearDebts() is Result.Success)

        val row = database.financeRecordQueries.selectRecordById("rec-1").executeAsOne()
        assertEquals(null, row.tag_type)
    }

    @Test
    fun clearSharedCosts_detachesDanglingSharedCostLinks_onSurvivingRecords() = runTest {
        val database = inMemoryDatabase()
        seedRecord(database, "rec-1", "SHARED_COST", "sc-1")
        val repo = SqlDelightClearDataRepository(database, Dispatchers.Unconfined)

        assertTrue(repo.clearSharedCosts() is Result.Success)

        val row = database.financeRecordQueries.selectRecordById("rec-1").executeAsOne()
        assertEquals(null, row.tag_type)
    }

    @Test
    fun clearEvents_leavesUnrelatedTagsIntact() = runTest {
        val database = inMemoryDatabase()
        seedRecord(database, "rec-1", "DEBT", "debt-1")
        val repo = SqlDelightClearDataRepository(database, Dispatchers.Unconfined)

        assertTrue(repo.clearEvents() is Result.Success)

        val row = database.financeRecordQueries.selectRecordById("rec-1").executeAsOne()
        assertEquals("DEBT", row.tag_type)
        assertEquals("debt-1", row.tag_id)
    }

    @Test
    fun clearAll_removesEveryTable() = runTest {
        val database = inMemoryDatabase()
        seedRecord(database, "rec-1", "EVENT", "evt-1")
        val repo = SqlDelightClearDataRepository(database, Dispatchers.Unconfined)

        assertTrue(repo.clearAll() is Result.Success)

        assertTrue(database.financeRecordQueries.selectAllRecords().executeAsList().isEmpty())
    }
}
