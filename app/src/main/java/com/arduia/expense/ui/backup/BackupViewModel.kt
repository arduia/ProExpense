package com.arduia.expense.ui.backup

import android.app.Application
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.core.arch.Mapper
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.Result
import com.arduia.expense.model.awaitValueOrError
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
    private val backupRepo: BackupRepository
) : AndroidViewModel(app) {

    private val _backupList = BaseLiveData<List<BackupVto>>()
    val backupList = _backupList.asLiveData()

    private val _backupFilePath = EventLiveData<Uri>()
    val backupFilePath = _backupFilePath.asLiveData()

    init {
        observeBackupLists()
    }

    private fun observeBackupLists() {
        backupRepo.getBackupAll()
            .flowOn(Dispatchers.IO)
            .onEach {
                if(it  is Result.Success){
                    _backupList post it.data.map(mapper::map)
                    Timber.d("backupList ${it.data}")
                }
            }
            .launchIn(viewModelScope)
    }

    fun onBackupItemSelect(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val backupEnt = backupRepo.getBackupByID(id).awaitValueOrError()
            val backupFileUri = Uri.parse(backupEnt.filePath)
            _backupFilePath post event(backupFileUri)
        }
    }

    fun setImportUri(uri: Uri) {
        _backupFilePath post event(uri)
    }
}
