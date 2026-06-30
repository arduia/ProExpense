package com.arduia.expense.feature.history

import com.arduia.expense.data.FinanceRecordRepository
import com.arduia.expense.domain.RecordId

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
