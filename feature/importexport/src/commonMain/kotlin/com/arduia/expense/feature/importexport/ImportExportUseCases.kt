package com.arduia.expense.feature.importexport

import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.ExportFormat
import com.arduia.expense.data.ImportExportRepository
import com.arduia.expense.data.ImportSummary
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord

class ExportDataUseCase(private val repository: ImportExportRepository) {
    suspend operator fun invoke(format: ExportFormat = ExportFormat.CSV): Result<String> =
        repository.exportAll(format)
}

class PreviewImportUseCase(private val repository: ImportExportRepository) {
    suspend operator fun invoke(content: String, format: ExportFormat): Result<List<FinanceRecord>> =
        repository.previewImport(content, format)
}

class ImportDataUseCase(private val repository: ImportExportRepository) {
    suspend operator fun invoke(content: String, format: ExportFormat): Result<ImportSummary> =
        repository.importFrom(content, format)
}

object ClearDataOptionId {
    const val EXPENSES = "expenses"
    const val EVENTS = "events"
    const val DEBTS = "debts"
    const val SHARED = "shared"
    const val EVERYTHING = "everything"
}

class ClearSelectedDataUseCase(private val repository: ClearDataRepository) {
    suspend operator fun invoke(selectedIds: Set<String>): Result<Unit> {
        if (ClearDataOptionId.EVERYTHING in selectedIds) {
            return repository.clearAll()
        }
        if (ClearDataOptionId.EXPENSES in selectedIds) {
            val result = repository.clearExpenses()
            if (result is Result.Error) return result
        }
        if (ClearDataOptionId.EVENTS in selectedIds) {
            val result = repository.clearEvents()
            if (result is Result.Error) return result
        }
        if (ClearDataOptionId.DEBTS in selectedIds) {
            val result = repository.clearDebts()
            if (result is Result.Error) return result
        }
        if (ClearDataOptionId.SHARED in selectedIds) {
            val result = repository.clearSharedCosts()
            if (result is Result.Error) return result
        }
        return Result.Success(Unit)
    }
}
