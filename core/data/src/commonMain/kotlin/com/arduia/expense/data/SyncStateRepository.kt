package com.arduia.expense.data

import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import kotlin.jvm.JvmInline

/**
 * Calendar-month bucket (UTC), e.g. "2026-07" — the unit Google Drive sync uploads/downloads as one
 * file, so many edits within a month collapse into a single API call.
 */
@JvmInline
value class YearMonth(
    val value: String,
) {
    init {
        require(YEAR_MONTH_REGEX.matches(value)) { "YearMonth must be yyyy-MM, was: $value" }
    }

    companion object {
        private val YEAR_MONTH_REGEX = Regex("""\d{4}-\d{2}""")
    }
}

/** A locally-deleted record not yet reflected in its month's remote file. */
data class TombstoneRef(
    val id: RecordId,
    val yearMonth: YearMonth,
    val deletedAtEpochMillis: Long,
)

/**
 * A [FinanceRecord] paired with its storage-only `updated_at` ("last modified") timestamp — the
 * shared domain model doesn't carry this field (only [FinanceRecord.recordedAtEpochMillis], the
 * user-chosen date), but sync needs it for last-write-wins conflict resolution (US-SYNC-5), both
 * when building a month's push payload and when merging a pulled one.
 */
data class SyncRecordSnapshot(
    val record: FinanceRecord,
    val updatedAtEpochMillis: Long,
)

/**
 * Month-granularity sync bookkeeping for [FinanceRecord]s — kept separate from
 * [FinanceRecordRepository] so sync concerns never leak into the shared domain model. v1 scope is
 * `finance_record` only (see docs/user_stories/sync/).
 */
interface SyncStateRepository {
    /** Months with at least one dirty (unsynced) record or tombstone. */
    suspend fun getDirtyMonths(): Result<List<YearMonth>>

    /** Every record in [yearMonth] — the payload is self-contained per month, not just the dirty subset. */
    suspend fun getRecordsForMonth(yearMonth: YearMonth): Result<List<SyncRecordSnapshot>>

    suspend fun getTombstonesForMonth(yearMonth: YearMonth): Result<List<TombstoneRef>>

    /** Clears the dirty flag for every record/tombstone in [yearMonth] after a successful push. */
    suspend fun markMonthPushed(
        yearMonth: YearMonth,
        remoteFileId: String,
        remoteUpdatedAtEpochMillis: Long,
    ): Result<Unit>

    /**
     * Merges a downloaded month's remote rows into local storage — per-record last-write-wins
     * (local wins only if locally dirty and newer; remote wins ties), remote deletes applied as
     * tombstones.
     */
    suspend fun applyRemoteMonthPayload(
        yearMonth: YearMonth,
        remoteRecords: List<SyncRecordSnapshot>,
        remoteTombstones: List<TombstoneRef>,
        remoteFileId: String,
        remoteUpdatedAtEpochMillis: Long,
    ): Result<Unit>

    /** Drive Changes API page token, so pull only scans for what changed since last time. */
    suspend fun getSyncCursor(): Result<String?>

    suspend fun setSyncCursor(cursor: String?): Result<Unit>
}
