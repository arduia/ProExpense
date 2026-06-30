package com.arduia.expense.feature.importexport

import com.arduia.expense.data.ClearDataRepository
import com.arduia.expense.data.ExportFormat
import com.arduia.expense.data.ImportExportRepository
import com.arduia.expense.data.ImportSummary
import com.arduia.expense.data.Result
import com.arduia.expense.domain.FinanceRecord
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

private class FakeImportExportRepository(
    var exportResult: Result<String> = Result.Success("exported"),
    var importResult: Result<ImportSummary> = Result.Success(ImportSummary(0, 0)),
    var previewResult: Result<List<FinanceRecord>> = Result.Success(emptyList()),
) : ImportExportRepository {
    var lastExportFormat: ExportFormat? = null
    var lastImportFormat: ExportFormat? = null
    var lastPreviewFormat: ExportFormat? = null

    override suspend fun exportAll(format: ExportFormat): Result<String> {
        lastExportFormat = format
        return exportResult
    }

    override suspend fun importFrom(content: String, format: ExportFormat): Result<ImportSummary> {
        lastImportFormat = format
        return importResult
    }

    override suspend fun previewImport(content: String, format: ExportFormat): Result<List<FinanceRecord>> {
        lastPreviewFormat = format
        return previewResult
    }
}

class ExportDataUseCaseTest {

    @Test
    fun invoke_delegatesToRepositoryWithFormat() = runTest {
        val repo = FakeImportExportRepository()
        val useCase = ExportDataUseCase(repo)

        val result = useCase(ExportFormat.JSON)

        assertIs<Result.Success<String>>(result)
        assertEquals(ExportFormat.JSON, repo.lastExportFormat)
    }
}

class ImportDataUseCaseTest {

    @Test
    fun invoke_delegatesToRepositoryWithContentAndFormat() = runTest {
        val repo = FakeImportExportRepository(importResult = Result.Success(ImportSummary(2, 1)))
        val useCase = ImportDataUseCase(repo)

        val result = useCase("csv,content", ExportFormat.CSV)

        assertIs<Result.Success<ImportSummary>>(result)
        assertEquals(ImportSummary(2, 1), result.data)
        assertEquals(ExportFormat.CSV, repo.lastImportFormat)
    }
}

private class FakeClearDataRepository : ClearDataRepository {
    val cleared = mutableListOf<String>()
    var errorOn: String? = null

    override suspend fun clearExpenses(): Result<Unit> = clear("expenses")
    override suspend fun clearEvents(): Result<Unit> = clear("events")
    override suspend fun clearDebts(): Result<Unit> = clear("debts")
    override suspend fun clearSharedCosts(): Result<Unit> = clear("shared")
    override suspend fun clearAll(): Result<Unit> = clear("all")

    private fun clear(label: String): Result<Unit> {
        if (errorOn == label) return Result.Error("$label failed")
        cleared += label
        return Result.Success(Unit)
    }
}

class ClearSelectedDataUseCaseTest {

    @Test
    fun invoke_clearsAllWhenEverythingSelected_ignoringOtherIds() = runTest {
        val repo = FakeClearDataRepository()
        val useCase = ClearSelectedDataUseCase(repo)

        val result = useCase(setOf(ClearDataOptionId.EVERYTHING, ClearDataOptionId.EXPENSES))

        assertIs<Result.Success<Unit>>(result)
        assertEquals(listOf("all"), repo.cleared)
    }

    @Test
    fun invoke_clearsOnlySelectedCategories() = runTest {
        val repo = FakeClearDataRepository()
        val useCase = ClearSelectedDataUseCase(repo)

        val result = useCase(setOf(ClearDataOptionId.DEBTS, ClearDataOptionId.SHARED))

        assertIs<Result.Success<Unit>>(result)
        assertEquals(listOf("debts", "shared"), repo.cleared)
    }

    @Test
    fun invoke_shortCircuitsOnFirstError() = runTest {
        val repo = FakeClearDataRepository().apply { errorOn = "events" }
        val useCase = ClearSelectedDataUseCase(repo)

        val result = useCase(
            setOf(ClearDataOptionId.EXPENSES, ClearDataOptionId.EVENTS, ClearDataOptionId.DEBTS),
        )

        assertIs<Result.Error>(result)
        assertEquals(listOf("expenses"), repo.cleared)
    }

    @Test
    fun invoke_succeedsWithEmptySelection() = runTest {
        val repo = FakeClearDataRepository()
        val useCase = ClearSelectedDataUseCase(repo)

        val result = useCase(emptySet())

        assertIs<Result.Success<Unit>>(result)
        assertEquals(emptyList(), repo.cleared)
    }
}
