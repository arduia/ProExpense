package com.arduia.expense.ui.backup.molecule

import android.net.Uri
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import app.cash.turbine.test
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.Result
import com.arduia.expense.ui.backup.BackupUiModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BackupPresenterTest {

    private lateinit var backupRepo: BackupRepository
    private lateinit var expenseRepo: ExpenseRepository
    private lateinit var mapper: Mapper<BackupEnt, BackupUiModel>

    @Before
    fun setup() {
        backupRepo = mockk(relaxed = true)
        expenseRepo = mockk(relaxed = true)
        mapper = mockk(relaxed = true)

        coEvery { expenseRepo.getExpenseTotalCount() } returns flowOf(Result.Success(0))
        coEvery { backupRepo.getBackupAll() } returns flowOf(Result.Success(emptyList()))
    }

    @Test
    fun `initial state is correct`() = runTest {
        val presenter = BackupPresenter(mapper, backupRepo, expenseRepo)
        val events = MutableSharedFlow<BackupEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            val state = awaitItem()
            
            assertEquals(true, state.isEmptyExpenseLogs)
            assertEquals(true, state.isEmptyBackupLogs)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `export dialog shown on ExportClicked and hidden on Dismissed`() = runTest {
        val presenter = BackupPresenter(mapper, backupRepo, expenseRepo)
        val events = MutableSharedFlow<BackupEvent>()

        moleculeFlow(RecompositionMode.Immediate) {
            presenter.present(events)
        }.test {
            awaitItem() // initial defaults
            
            events.emit(BackupEvent.ExportClicked)
            val showState = awaitItem()
            assertEquals(true, showState.showExportDialog)

            events.emit(BackupEvent.ExportDialogDismissed)
            val hideState = awaitItem()
            assertEquals(false, hideState.showExportDialog)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
