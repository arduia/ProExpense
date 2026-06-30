package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId

/** Filters and searches journal records (design plan §JournalViewModel). */
class SearchRecordsUseCase(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(filter: RecordHistoryFilter): Result<List<FinanceRecord>> =
        historyRepository.getRecords(filter)
}

/** Deletes a journal record (design plan §JournalViewModel). */
class DeleteRecordUseCase(
    private val financeRecordRepository: FinanceRecordRepository,
) {
    suspend operator fun invoke(recordId: String) {
        financeRecordRepository.delete(RecordId(recordId))
    }
}

/** Updates the note on an existing journal record, leaving everything else untouched. */
class UpdateRecordNoteUseCase(
    private val financeRecordRepository: FinanceRecordRepository,
) {
    suspend operator fun invoke(recordId: String, note: String) {
        val result = financeRecordRepository.getById(RecordId(recordId))
        val record = (result as? com.arduia.expense.data.Result.Success)?.data ?: return
        financeRecordRepository.upsert(record.copy(note = note.ifBlank { null }))
    }
}
