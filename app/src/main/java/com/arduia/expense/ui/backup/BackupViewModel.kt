package com.arduia.expense.ui.backup

import android.app.Application
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.ExpenseRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.Result
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.onSuccess
import com.arduia.expense.ui.vto.BackupVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class BackupViewModel @ViewModelInject constructor(
    app: Application,
    private val mapper: Mapper<BackupEnt, BackupVto>,
    private val backupRepo: BackupRepository,
    private val expenseRepo: ExpenseRepository
) : AndroidViewModel(app) {

    private val _backupList = BaseLiveData<List<BackupVto>>()
    val backupList = _backupList.asLiveData()

    private val _backupFilePath = EventLiveData<Uri>()
    val backupFilePath = _backupFilePath.asLiveData()

    val isEmptyBackupLogs = _backupList.switchMap {
        BaseLiveData(it.isEmpty())
    }

    private val _isEmptyExpenseLogs = BaseLiveData<Boolean>()
    val isEmptyExpenseLogs get() = _isEmptyExpenseLogs.asLiveData()

    private val _onBackupDelete = EventLiveData<Unit>()
    val onBackupDelete get() = _onBackupDelete.asLiveData()

    init {
        observeBackupLists()
        observeExpenseCount()
    }

    private fun observeExpenseCount() {
        expenseRepo.getExpenseTotalCount()
            .flowOn(Dispatchers.IO)
            .onSuccess {
                _isEmptyExpenseLogs post (it == 0)
            }
            .launchIn(viewModelScope)
    }

    private fun observeBackupLists() {
        backupRepo.getBackupAll()
            .flowOn(Dispatchers.IO)
            .onEach {
                if (it is Result.Success) {
                    _backupList post it.data.map(mapper::map)
                    Timber.d("backupList ${it.data}")
                }
            }
            .launchIn(viewModelScope)
    }

    fun onBackupDeleteConfirmed(item: BackupVto) {
        viewModelScope.launch(Dispatchers.IO) {
            backupRepo.deleteBackupByID(item.id)
        }
    }

    fun setImportUri(uri: Uri) {
        _backupFilePath post event(uri)
    }
}
