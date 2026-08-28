package com.arduia.expense.shell

import com.arduia.expense.domain.CategoryId
import com.arduia.expense.domain.FinanceRecord
import com.arduia.expense.domain.RecordId
import com.arduia.expense.domain.RecordType
import com.arduia.expense.feature.history.DeleteRecordUseCase
import com.arduia.expense.feature.history.UpdateRecordNoteUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Backbone coverage for one record's detail view.
 *
 * Traceability: US-HIS-6 (open a record and see its full detail), US-HIS-7 (edit the note in place),
 * and the delete path in `DeleteRecordUseCase`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class JournalDetailViewModelTest {
    private fun record(
        id: String = "r1",
        cents: Long = 1_250,
        note: String? = "Lunch",
        type: RecordType = RecordType.EXPENSE,
    ) = FinanceRecord(
        id = RecordId(id),
        money = money(cents),
        homeCurrencyMoney = money(cents),
        categoryId = CategoryId("food"),
        type = type,
        note = note,
        recordedAtEpochMillis = 0L,
    )

    private fun TestScope.viewModel(records: FakeRecords): JournalDetailViewModel =
        JournalDetailViewModel(
            financeRecordRepository = records,
            categoryRepository = FakeCategories(),
            currencySettingsRepository = FakeCurrencySettings(),
            deleteRecord = DeleteRecordUseCase(records),
            updateRecordNote = UpdateRecordNoteUseCase(records),
            dispatcher = StandardTestDispatcher(testScheduler),
        )

    @Test
    fun `loading projects the record with its category and amount`() =
        runTest {
            val vm = viewModel(FakeRecords(listOf(record())))

            vm.load(recordId = "r1")
            advanceUntilIdle()

            val row = assertNotNull(vm.uiState.value.row)
            assertEquals("Lunch", row.note)
            assertEquals("$12.50", row.amount)
            assertTrue(row.meta.startsWith("Food"))
            assertFalse(vm.uiState.value.isLoading)
        }

    @Test
    fun `a note-less record falls back to its category label`() =
        runTest {
            val vm = viewModel(FakeRecords(listOf(record(note = null))))

            vm.load(recordId = "r1")
            advanceUntilIdle()

            assertEquals("Food", assertNotNull(vm.uiState.value.row).note)
            assertEquals("", vm.uiState.value.noteDraft)
        }

    @Test
    fun `a missing record resolves to not-found rather than an endless spinner`() =
        runTest {
            val vm = viewModel(FakeRecords(emptyList()))

            vm.load(recordId = "gone")
            advanceUntilIdle()

            assertTrue(vm.uiState.value.notFound)
            assertFalse(vm.uiState.value.isLoading)
        }

    @Test
    fun `saving a note persists it and re-reads the projected row`() =
        runTest {
            val records = FakeRecords(listOf(record()))
            val vm = viewModel(records)
            vm.load(recordId = "r1")
            advanceUntilIdle()

            vm.onNoteDraftChange("  Dinner with Ben  ")
            vm.saveNote()
            advanceUntilIdle()

            assertEquals("Dinner with Ben", records.current.single().note)
            assertEquals("Dinner with Ben", assertNotNull(vm.uiState.value.row).note)
        }

    @Test
    fun `clearing a note stores null rather than an empty string`() =
        runTest {
            val records = FakeRecords(listOf(record()))
            val vm = viewModel(records)
            vm.load(recordId = "r1")
            advanceUntilIdle()

            vm.onNoteDraftChange("")
            vm.saveNote()
            advanceUntilIdle()

            assertNull(records.current.single().note)
        }

    @Test
    fun `deleting removes the record and flags the view to dismiss`() =
        runTest {
            val records = FakeRecords(listOf(record()))
            val vm = viewModel(records)
            vm.load(recordId = "r1")
            advanceUntilIdle()

            vm.delete()
            advanceUntilIdle()

            assertTrue(records.current.isEmpty())
            assertTrue(vm.uiState.value.deleted)
            // A deleted record is dismissed, not reported as "not found".
            assertFalse(vm.uiState.value.notFound)
        }

    @Test
    fun `an income record keeps its income framing`() =
        runTest {
            val vm = viewModel(FakeRecords(listOf(record(type = RecordType.INCOME))))

            vm.load(recordId = "r1")
            advanceUntilIdle()

            assertTrue(assertNotNull(vm.uiState.value.row).isIncome)
        }
}
