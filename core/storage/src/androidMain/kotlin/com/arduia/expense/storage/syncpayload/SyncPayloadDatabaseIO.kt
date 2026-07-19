package com.arduia.expense.storage.syncpayload

import android.content.Context
import android.util.Log
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.arduia.expense.data.SyncRecordSnapshot
import com.arduia.expense.data.TombstoneRef
import com.arduia.expense.data.YearMonth
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.Money
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.WalletId
import com.arduia.expense.storage.mapping.tagId
import com.arduia.expense.storage.mapping.tagType
import com.arduia.expense.storage.mapping.toCode
import com.arduia.expense.storage.mapping.toRecordLink
import com.arduia.expense.storage.mapping.toRecordTypeFromCode
import com.arduia.expense.storage.syncpayload.db.SyncPayloadDatabase
import com.arduia.expense.storage.syncpayload.db.SyncPayloadQueries
import com.arduia.expense.storage.syncpayload.db.Sync_record
import java.io.File
import java.util.UUID

/**
 * Builds/reads the SQLite file uploaded to / downloaded from Drive for one calendar month
 * (US-SYNC-3). No SQLCipher passphrase — the remote per-month file relies on Drive's own
 * storage/transport encryption plus the private `appDataFolder` scope, not a second app-level
 * encryption layer (see docs/user_stories/sync/US-SYNC-1.md Non-Functional Requirements).
 */
class SyncPayloadDatabaseIO(
    private val context: Context,
) {
    /** Builds a fresh temp SQLite file containing [records] and [tombstones]; caller uploads then deletes it. */
    fun writeMonthFile(
        records: List<SyncRecordSnapshot>,
        tombstones: List<TombstoneRef>,
    ): File {
        Log.d(
            TAG,
            "writeMonthFile: building temp file for ${records.size} record(s), ${tombstones.size} tombstone(s)",
        )
        val file = newTempFile()
        val driver = AndroidSqliteDriver(SyncPayloadDatabase.Schema, context, file.absolutePath)
        try {
            val database = SyncPayloadDatabase(driver)
            database.syncPayloadQueries.transaction {
                records.forEach { snapshot -> database.syncPayloadQueries.insertRecordSnapshot(snapshot) }
                tombstones.forEach { tombstone ->
                    database.syncPayloadQueries.insertTombstone(tombstone.id.value, tombstone.deletedAtEpochMillis)
                }
            }
        } finally {
            driver.close()
        }
        Log.d(TAG, "writeMonthFile: wrote ${file.name} (${file.length()} bytes)")
        return file
    }

    /** Reads a downloaded month file's bytes; the temp file is deleted before returning. */
    fun readMonthFile(bytes: ByteArray): SyncMonthFileContents {
        Log.d(TAG, "readMonthFile: reading ${bytes.size} downloaded bytes")
        val file = newTempFile()
        file.writeBytes(bytes)
        val driver = AndroidSqliteDriver(SyncPayloadDatabase.Schema, context, file.absolutePath)
        try {
            val database = SyncPayloadDatabase(driver)
            val rows = database.syncPayloadQueries.selectAll().executeAsList()
            val records = mutableListOf<SyncRecordSnapshot>()
            val tombstones = mutableListOf<TombstoneRef>()
            rows.forEach { row ->
                if (row.deleted != 0L) {
                    // Month bucket is already known by the caller (it's the file being read) —
                    // overwritten by feature:sync's pull logic (Phase 3) before use.
                    tombstones += TombstoneRef(RecordId(row.id), placeholderYearMonth, row.updated_at)
                } else {
                    records += row.toSyncRecordSnapshot()
                }
            }
            Log.d(TAG, "readMonthFile: parsed ${records.size} record(s), ${tombstones.size} tombstone(s)")
            return SyncMonthFileContents(records, tombstones)
        } finally {
            driver.close()
            file.delete()
        }
    }

    private fun newTempFile(): File = File.createTempFile("sync-month-${UUID.randomUUID()}", ".sqlite", context.cacheDir)

    // The generated column-order-positional insertRecord(...) call is easy to get wrong across 14
    // params — this extension keeps the field-to-column mapping in one readable place.
    private fun SyncPayloadQueries.insertRecordSnapshot(snapshot: SyncRecordSnapshot) {
        val record = snapshot.record
        val sameCurrency = record.money.currency == record.homeCurrencyMoney.currency
        insertRecord(
            id = record.id.value,
            updated_at = snapshot.updatedAtEpochMillis,
            deleted = 0,
            amount_cents = record.money.amount.valueInCents,
            currency_code = record.money.currency.code,
            home_amount_cents = if (sameCurrency) null else record.homeCurrencyMoney.amount.valueInCents,
            home_currency_code = if (sameCurrency) null else record.homeCurrencyMoney.currency.code,
            category_id = record.categoryId.value,
            type = record.type.toCode(),
            note = record.note,
            recorded_at = record.recordedAtEpochMillis,
            tag_type = record.link.tagType(),
            tag_id = record.link.tagId(),
            wallet_id = record.walletId?.value?.toLong(),
        )
    }

    private fun Sync_record.toSyncRecordSnapshot(): SyncRecordSnapshot {
        val record =
            FinanceRecord(
                id = RecordId(id),
                money = Money(Amount(requireNotNull(amount_cents)), CurrencyCode(requireNotNull(currency_code))),
                homeCurrencyMoney =
                    Money(
                        Amount(home_amount_cents ?: requireNotNull(amount_cents)),
                        CurrencyCode(home_currency_code ?: requireNotNull(currency_code)),
                    ),
                categoryId = CategoryId(requireNotNull(category_id)),
                type = requireNotNull(type).toRecordTypeFromCode(),
                note = note,
                recordedAtEpochMillis = requireNotNull(recorded_at),
                link = toRecordLink(tag_type, tag_id),
                walletId = wallet_id?.let { WalletId(it.toInt()) },
            )
        return SyncRecordSnapshot(record, updated_at)
    }

    private companion object {
        const val TAG = "SyncPayloadDatabaseIO"
        val placeholderYearMonth = YearMonth("1970-01")
    }
}

data class SyncMonthFileContents(
    val records: List<SyncRecordSnapshot>,
    val tombstones: List<TombstoneRef>,
)
