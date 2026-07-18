package com.arduia.expense.storage.syncpayload

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.arduia.expense.storage.syncpayload.db.SyncPayloadDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Round-trip test for the monthly sync-payload schema (US-SYNC-3) via an in-memory `JdbcSqliteDriver`
 * — same test-driver strategy `ProExpenseDatabase` uses (see `TestDatabase.kt`), exercising the real
 * SQL/generated-mapper path without needing `AndroidSqliteDriver`/Robolectric, which this module's
 * test suite otherwise avoids entirely. `SyncPayloadDatabaseIO` (androidMain) is a thin wrapper over
 * this same schema for a real on-disk temp file — this test covers the schema/mapping risk directly.
 */
class SyncPayloadDatabaseTest {
    private fun inMemoryDatabase(): SyncPayloadDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        SyncPayloadDatabase.Schema.create(driver)
        return SyncPayloadDatabase(driver)
    }

    @Test
    fun insertRecord_thenSelectAll_roundTripsContentFields() {
        val database = inMemoryDatabase()

        database.syncPayloadQueries.insertRecord(
            id = "rec-1",
            updated_at = 1_000L,
            deleted = 0,
            amount_cents = 5_000,
            currency_code = "USD",
            home_amount_cents = null,
            home_currency_code = null,
            category_id = "food",
            type = 0L,
            note = "coffee",
            recorded_at = 900L,
            tag_type = null,
            tag_id = null,
            wallet_id = null,
        )

        val row =
            database.syncPayloadQueries
                .selectAll()
                .executeAsList()
                .single()
        assertEquals("rec-1", row.id)
        assertEquals(0L, row.deleted)
        assertEquals(5_000L, row.amount_cents)
        assertEquals("coffee", row.note)
    }

    // Key invariant: a tombstone row carries no record content — only id + updated_at (reused as
    // the deletion timestamp) + deleted=1, so a deletion never accidentally resurrects stale content.
    @Test
    fun insertTombstone_hasNullContentFields() {
        val database = inMemoryDatabase()

        database.syncPayloadQueries.insertTombstone(id = "rec-1", updated_at = 2_000L)

        val row =
            database.syncPayloadQueries
                .selectAll()
                .executeAsList()
                .single()
        assertEquals("rec-1", row.id)
        assertEquals(1L, row.deleted)
        assertEquals(2_000L, row.updated_at)
        assertNull(row.amount_cents)
        assertNull(row.category_id)
    }

    @Test
    fun insertRecord_replacesExistingRowWithSameId() {
        val database = inMemoryDatabase()
        database.syncPayloadQueries.insertRecord(
            id = "rec-1",
            updated_at = 1_000L,
            deleted = 0,
            amount_cents = 5_000,
            currency_code = "USD",
            home_amount_cents = null,
            home_currency_code = null,
            category_id = "food",
            type = 0L,
            note = null,
            recorded_at = 900L,
            tag_type = null,
            tag_id = null,
            wallet_id = null,
        )

        database.syncPayloadQueries.insertTombstone(id = "rec-1", updated_at = 3_000L)

        val rows = database.syncPayloadQueries.selectAll().executeAsList()
        assertEquals(1, rows.size)
        assertEquals(1L, rows.single().deleted)
    }
}
