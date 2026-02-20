package com.arduia.expense.ui.backup.molecule

import android.net.Uri
import androidx.compose.runtime.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.Result
import com.arduia.expense.ui.backup.BackupUiModel
import com.arduia.expense.ui.common.molecule.Presenter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class BackupPresenter(
    private val mapper: Mapper<BackupEnt, BackupUiModel>,
    private val backupRepo: BackupRepository,
    private val expenseRepo: ExpenseRepository
) : Presenter<BackupEvent, BackupState> {

    @Composable
    override fun present(events: kotlinx.coroutines.flow.Flow<BackupEvent>): BackupState {
        var backupList by remember { mutableStateOf(emptyList<BackupUiModel>()) }
        var isEmptyBackupLogs by remember { mutableStateOf(true) }
        var isEmptyExpenseLogs by remember { mutableStateOf(true) }
        var showDeleteConfirmFor by remember { mutableStateOf<BackupUiModel?>(null) }
        var showExportDialog by remember { mutableStateOf(false) }
        var importUri by remember { mutableStateOf<Uri?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            expenseRepo.getExpenseTotalCount().collectLatest { result ->
                if (result is Result.Success) {
                    isEmptyExpenseLogs = (result.data == 0)
                }
            }
        }

        LaunchedEffect(Unit) {
            backupRepo.getBackupAll().collectLatest { result ->
                if (result is Result.Success) {
                    backupList = result.data.map(mapper::map)
                    isEmptyBackupLogs = backupList.isEmpty()
                    Timber.d("backupList updated via Molecule ${backupList.size}")
                }
            }
        }

        LaunchedEffect(events) {
            events.collect { event ->
                when (event) {
                    is BackupEvent.ExportClicked -> {
                        showExportDialog = true
                    }
                    is BackupEvent.ExportDialogDismissed -> {
                        showExportDialog = false
                    }
                    is BackupEvent.ImportClicked -> {
                        // handled by fragment opening intent, then providing uri back
                    }
                    is BackupEvent.ImportUriReceived -> {
                        importUri = event.uri
                    }
                    is BackupEvent.ImportDialogDismissed -> {
                        importUri = null
                    }
                    is BackupEvent.DeleteIconClicked -> {
                        showDeleteConfirmFor = event.item
                    }
                    is BackupEvent.DeleteConfirmDismissed -> {
                        showDeleteConfirmFor = null
                    }
                    is BackupEvent.DeleteConfirmed -> {
                        scope.launch {
                            backupRepo.deleteBackupByID(event.item.id)
                        }
                        showDeleteConfirmFor = null
                    }
                }
            }
        }

        return BackupState(
            backupList = backupList,
            isEmptyBackupLogs = isEmptyBackupLogs,
            isEmptyExpenseLogs = isEmptyExpenseLogs,
            showDeleteConfirmFor = showDeleteConfirmFor,
            showExportDialog = showExportDialog,
            importUri = importUri
        )
    }
}
