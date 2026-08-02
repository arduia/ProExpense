package com.arduia.expense.storage.repository

import com.arduia.expense.data.Result
import com.arduia.expense.data.SyncRecordSnapshot
import com.arduia.expense.data.SyncStateRepository
import com.arduia.expense.data.TombstoneRef
import com.arduia.expense.data.YearMonth
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordIntegrityVerifier
import com.arduia.expense.shared.currentEpochMillis
import com.arduia.expense.storage.catchingResult
import com.arduia.expense.storage.db.FinanceRecordMonthSyncQueries
import com.arduia.expense.storage.db.FinanceRecordQueries
import com.arduia.expense.storage.db.FinanceRecordTombstoneQueries
import com.arduia.expense.storage.mapping.tagId
import com.arduia.expense.storage.mapping.tagType
import com.arduia.expense.storage.mapping.toCode
import com.arduia.expense.storage.mapping.toDomain
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Month-granularity Drive sync bookkeeping (US-SYNC-3/US-SYNC-4) over the same `finance_record`
 * table `SqlDelightFinanceRecordRepository` owns — kept as a separate repository so sync concerns
 * never leak into `FinanceRecordRepository`'s contract.
 */
class SqlDelightSyncStateRepository(
    private val queries: FinanceRecordQueries,
    private val tombstoneQueries: FinanceRecordTombstoneQueries,
    private val monthSyncQueries: FinanceRecordMonthSyncQueries,
    private val integrityVerifier: RecordIntegrityVerifier = RecordIntegrityVerifier(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SyncStateRepository {
    override suspend fun getDirtyMonths(): Result<List<YearMonth>> =
        withContext(dispatcher) {
            catchingResult {
                val months = mutableSetOf<String>()
                queries.selectDirtyMonths().executeAsList().forEach { months += it }
                tombstoneQueries.selectDirtyTombstoneMonths().executeAsList().forEach { months += it }
                monthSyncQueries.selectDirtyMonthSyncMonths().executeAsList().forEach { months += it }
                months.map { YearMonth(it) }
            }
        }

    override suspend fun getRecordsForMonth(yearMonth: YearMonth): Result<List<SyncRecordSnapshot>> =
        withContext(dispatcher) {
            catchingResult {
                queries.selectRecordsByMonth(yearMonth.value).executeAsList().map {
                    SyncRecordSnapshot(record = it.toDomain(), updatedAtEpochMillis = it.updated_at)
                }
            }
        }

    override suspend fun getTombstonesForMonth(yearMonth: YearMonth): Result<List<TombstoneRef>> =
        withContext(dispatcher) {
            catchingResult {
                tombstoneQueries.selectTombstonesByMonth(yearMonth.value).executeAsList().map {
                    TombstoneRef(RecordId(it.id), YearMonth(it.year_month), it.deleted_at)
                }
            }
        }

    override suspend fun markMonthPushed(
        yearMonth: YearMonth,
        remoteFileId: String,
        remoteUpdatedAtEpochMillis: Long,
    ): Result<Unit> =
        withContext(dispatcher) {
            catchingResult {
                queries.transaction {
                    queries.markRecordsSyncedForMonth(currentEpochMillis(), yearMonth.value)
                    tombstoneQueries.markTombstonesSyncedForMonth(yearMonth.value)
                    monthSyncQueries.upsertMonthSync(
                        year_month = yearMonth.value,
                        remote_file_id = remoteFileId,
                        remote_updated_at = remoteUpdatedAtEpochMillis,
                        last_synced_at = currentEpochMillis(),
                    )
                }
                Unit
            }
        }

    override suspend fun applyRemoteMonthPayload(
        yearMonth: YearMonth,
        remoteRecords: List<SyncRecordSnapshot>,
        remoteTombstones: List<TombstoneRef>,
        remoteFileId: String,
        remoteUpdatedAtEpochMillis: Long,
    ): Result<Unit> =
        withContext(dispatcher) {
            catchingResult {
                queries.transaction {
                    remoteRecords.forEach { snapshot -> applyRemoteRecordIfNewer(snapshot) }
                    remoteTombstones.forEach { tombstone -> applyRemoteTombstoneIfNewer(tombstone) }
                    monthSyncQueries.upsertMonthSync(
                        year_month = yearMonth.value,
                        remote_file_id = remoteFileId,
                        remote_updated_at = remoteUpdatedAtEpochMillis,
                        last_synced_at = currentEpochMillis(),
                    )
                }
                Unit
            }
        }

    // Local wins only when it's a dirty (unsynced) edit newer than the incoming remote row —
    // otherwise remote wins, including on an exact tie (US-SYNC-5 deterministic tie-break).
    private fun applyRemoteRecordIfNewer(snapshot: SyncRecordSnapshot) {
        val remote = snapshot.record
        val local = queries.selectRecordById(remote.id.value).executeAsOneOrNull()
        if (local != null && local.dirty != 0L && local.updated_at > snapshot.updatedAtEpochMillis) return
        val checksum = integrityVerifier.checksumFor(remote)
        val sameCurrency = remote.money.currency == remote.homeCurrencyMoney.currency
        queries.upsertRecordFromRemote(
            id = remote.id.value,
            amount_cents = remote.money.amount.valueInCents,
            currency_code = remote.money.currency.code,
            home_amount_cents = if (sameCurrency) null else remote.homeCurrencyMoney.amount.valueInCents,
            category_id = remote.categoryId.value,
            type = remote.type.toCode(),
            note = remote.note,
            recorded_at = remote.recordedAtEpochMillis,
            updated_at = snapshot.updatedAtEpochMillis,
            tag_type = remote.link.tagType(),
            tag_id = remote.link.tagId(),
            integrity_algo = checksum.algorithm,
            integrity_hash = checksum.value,
            home_currency_code = if (sameCurrency) null else remote.homeCurrencyMoney.currency.code,
            wallet_id = remote.walletId?.value?.toLong(),
            last_synced_at = currentEpochMillis(),
        )
    }

    private fun applyRemoteTombstoneIfNewer(tombstone: TombstoneRef) {
        val local = queries.selectRecordById(tombstone.id.value).executeAsOneOrNull()
        if (local != null && local.dirty != 0L && local.updated_at > tombstone.deletedAtEpochMillis) return
        queries.deleteRecord(tombstone.id.value)
        tombstoneQueries.applyRemoteTombstone(
            tombstone.id.value,
            tombstone.yearMonth.value,
            tombstone.deletedAtEpochMillis,
        )
    }

    override suspend fun getSyncCursor(): Result<String?> =
        withContext(dispatcher) {
            catchingResult { cursorHolder }
        }

    override suspend fun setSyncCursor(cursor: String?): Result<Unit> =
        withContext(dispatcher) {
            catchingResult { cursorHolder = cursor }
        }

    // Drive Changes API page token — kept in-memory here; Phase 3 wires this through AppMetaStore
    // (see AppMetaSyncRepository) once pull sync is implemented.
    private var cursorHolder: String? = null
}
