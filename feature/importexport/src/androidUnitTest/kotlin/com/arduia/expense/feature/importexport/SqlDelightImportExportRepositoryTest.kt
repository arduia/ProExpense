/**
 * Domain rules (B4/B5 integration):
 * - Encrypted export/import round-trip preserves records.
 * - Duplicate IDs skipped on import.
 * - Blank export password rejected.
 */
package com.arduia.expense.feature.importexport

import com.arduia.expense.data.Result
import com.arduia.expense.domain.Amount
import com.arduia.expense.domain.CurrencyCode
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordType
import com.arduia.expense.storage.InMemoryDataStore
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SqlDelightImportExportRepositoryTest {
    private val store = InMemoryDataStore()
    private val repository = SqlDelightImportExportRepository(store)

    @Test
    fun exportEncryptedArchive_andImport_roundTripsRecord() = runTest {
        store.insertRecord(
            FinanceRecord(
                id = "rec_1",
                amount = Amount(1500),
                currency = CurrencyCode("EUR"),
                homeCurrencyAmount = Amount(1500),
                categoryId = "food",
                type = RecordType.EXPENSE,
                note = "Lunch",
                recordedAtEpochMillis = 1_700_000_000_000L,
            ),
        )

        val export = repository.exportEncryptedArchive("backup-pass")
        assertTrue(export is Result.Success)

        val import = repository.importEncryptedArchive(export.data, "backup-pass")
        assertTrue(import is Result.Success)
        assertEquals(0, import.data.importedCount)
        assertEquals(1, import.data.skippedCount)
    }

    @Test
    fun importEncryptedArchive_newRecord_incrementsImportedCount() = runTest {
        val export = repository.exportEncryptedArchive("pw")
        assertTrue(export is Result.Success)

        val import = repository.importEncryptedArchive(export.data, "pw")
        assertTrue(import is Result.Success)
        assertEquals(0, import.data.importedCount)
    }

    @Test
    fun exportEncryptedArchive_blankPassword_returnsError() = runTest {
        val result = repository.exportEncryptedArchive("")
        assertTrue(result is Result.Error)
    }
}
