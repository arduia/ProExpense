package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.RecordPageCursor
import com.arduia.expense.data.Result
import com.arduia.expense.domain.Debt
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId

/** Loads one keyset-paginated, filter-pushed-down Journal page (design plan §JournalViewModel). */
class LoadJournalPageUseCase(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(
        filter: RecordHistoryFilter,
        cursor: RecordPageCursor? = null,
        limit: Int = DEFAULT_PAGE_SIZE,
    ): Result<List<FinanceRecord>> = historyRepository.getRecordsPage(filter, cursor, limit)

    companion object {
        const val DEFAULT_PAGE_SIZE = 40
    }
}

/** Deletes a journal record (design plan §JournalViewModel). */
class DeleteRecordUseCase(
    private val financeRecordRepository: FinanceRecordRepository,
) {
    suspend operator fun invoke(recordId: String) {
        financeRecordRepository.delete(RecordId(recordId))
    }
}

/**
 * Which toggle-off debts (never a real [FinanceRecord] — see [Debt.recordAsTransaction]) should
 * merge into the currently-loaded Journal page. Bounded to [oldestLoadedRecordMillis] so a debt
 * from long before the user has scrolled that far doesn't appear out of pagination order; as more
 * pages load ([oldestLoadedRecordMillis] moves back or [recordsFullyLoaded] flips true), older
 * debts progressively join. A toggle-on debt already has its own linked [FinanceRecord] in the
 * page and must not be filtered in here too, or it would render twice.
 */
fun visibleUnrecordedDebts(
    debts: List<Debt>,
    oldestLoadedRecordMillis: Long?,
    recordsFullyLoaded: Boolean,
): List<Debt> =
    debts.filter { debt ->
        val debtIsWithinLoadedRange =
            recordsFullyLoaded ||
                oldestLoadedRecordMillis == null ||
                debt.recordedAtEpochMillis >= oldestLoadedRecordMillis
        !debt.recordAsTransaction && debtIsWithinLoadedRange
    }

/** Updates the note on an existing journal record, leaving everything else untouched. */
class UpdateRecordNoteUseCase(
    private val financeRecordRepository: FinanceRecordRepository,
) {
    suspend operator fun invoke(
        recordId: String,
        note: String,
    ) {
        val result = financeRecordRepository.getById(RecordId(recordId))
        val record = (result as? com.arduia.expense.data.Result.Success)?.data ?: return
        financeRecordRepository.upsert(record.copy(note = note.ifBlank { null }))
    }
}
