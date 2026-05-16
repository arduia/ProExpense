package com.arduia.expense.ui.backup

import android.net.Uri
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.Result
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class BackupUiState(
    val backupList: List<BackupUiModel> = emptyList(),
    val isEmptyBackupLogs: Boolean = true,
    val isEmptyExpenseLogs: Boolean = false
)

sealed class BackupUiEffect {
    data class ShowImportDialog(val uri: Uri) : BackupUiEffect()
}

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val mapper: Mapper<BackupEnt, BackupUiModel>,
    private val backupRepo: BackupRepository,
    private val expenseRepo: ExpenseRepository
) : BaseViewModel<BackupUiState, BackupUiEffect>(BackupUiState()) {

    init {
        observeBackupLists()
        observeExpenseCount()
    }

    private fun observeExpenseCount() {
        expenseRepo.getExpenseTotalCount()
            .flowOn(Dispatchers.IO)
            .onSuccess { count ->
                setState { copy(isEmptyExpenseLogs = count == 0) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeBackupLists() {
        backupRepo.getBackupAll()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it is Result.Success) {
                    val list = it.data.map(mapper::map)
                    Timber.d("backupList ${it.data}")
                    setState { copy(backupList = list, isEmptyBackupLogs = list.isEmpty()) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onBackupDeleteConfirmed(item: BackupUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            backupRepo.deleteBackupByID(item.id)
        }
    }

    fun setImportUri(uri: Uri) {
        sendEffect(BackupUiEffect.ShowImportDialog(uri))
    }

    fun onImportClick() {}
    fun onExportClick() {}
    fun onExportRangeSelect(range: String) {}
    fun onExportConfirm() {}
    fun onExportDialogDismiss() {}
}
