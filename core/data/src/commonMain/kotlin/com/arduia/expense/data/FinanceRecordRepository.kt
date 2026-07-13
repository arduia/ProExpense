package com.arduia.expense.data

import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordKindFilter
import kotlinx.coroutines.flow.Flow

/**
 * SQL-pushdown filter for [FinanceRecordRepository.getRecordsPage] — every field is a predicate
 * that's skipped when null. [kind], when set, takes over from [categoryId] (Split/Debt aren't real
 * categories) and also suppresses the reciprocal category exclusion described on [categoryId].
 */
data class RecordPageFilter(
    // Ignored when [kind] is set. When set without [kind], also excludes Split/Debt records even
    // though they're bookkeeped under a sentinel category id (see RecordKind) — a "Shopping"
    // filter should only ever return genuine Shopping expenses.
    val categoryId: CategoryId? = null,
    val kind: RecordKindFilter? = null,
    val fromEpochMillis: Long? = null,
    val toEpochMillis: Long? = null,
    val query: String? = null,
)

/** Keyset pagination cursor — the last row of the previous page. `null` requests the first page. */
data class RecordPageCursor(
    val recordedAtEpochMillis: Long,
    val recordId: RecordId,
)

/** Cheap change signal so a paged view can tell "something changed" apart from "load everything again". */
data class RecordChangeSignal(
    val count: Long,
    val lastUpdatedAtEpochMillis: Long,
)

interface FinanceRecordRepository {
    suspend fun getAll(): Result<List<FinanceRecord>>

    suspend fun getById(id: RecordId): Result<FinanceRecord?>

    suspend fun upsert(record: FinanceRecord): Result<Unit>

    suspend fun delete(id: RecordId): Result<Unit>

    /** Live, ordered view of every record — backs auto-updating screens (Home, Journal). */
    fun observeAll(): Flow<List<FinanceRecord>>

    /**
     * Re-derives the stored record's checksum and compares it to the persisted one.
     * `Success(true)` = intact; `Success(false)` = tampered/corrupt or no checksum; `Error` when the
     * record is missing or the read fails.
     */
    suspend fun verifyIntegrity(id: RecordId): Result<Boolean>

    /** Keyset-paginated, filter-pushdown page of records ordered by recorded_at DESC, id DESC. */
    suspend fun getRecordsPage(
        filter: RecordPageFilter = RecordPageFilter(),
        cursor: RecordPageCursor? = null,
        limit: Int,
    ): Result<List<FinanceRecord>>

    /** Whether any record is currently assigned to [categoryId] — backs the Uncategorized filter chip. */
    suspend fun existsByCategory(categoryId: CategoryId): Result<Boolean>

    /** Live (count, lastUpdatedAt) signal — lets a paged view detect mutations without re-fetching everything. */
    fun observeChangeSignal(): Flow<RecordChangeSignal>
}
