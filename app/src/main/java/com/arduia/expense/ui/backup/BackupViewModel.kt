package com.arduia.expense.ui.backup

import android.app.Application
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.ui.mapping.BackupMapper
import com.arduia.expense.ui.vto.BackupVto
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.event
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BackupViewModel @ViewModelInject constructor(
    app: Application,
    private val mapper: BackupMapper,
    private val backupRepo: BackupRepository
    ): AndroidViewModel(app), LifecycleObserver{

    private val _backupList = BaseLiveData<List<BackupVto>>()
    val backupList = _backupList.asLiveData()

    private val _backupFilePath = EventLiveData<Uri>()
    val backupFilePath = _backupFilePath.asLiveData()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        viewModelScope.launch (Dispatchers.IO){
            backupRepo.getBackupAll().collect {
                _backupList post it.map { backupEnt ->  mapper.mapToBackupVto(backupEnt) }
            }
        }
    }

    fun selectBackupItem(id: Int){
        viewModelScope.launch(Dispatchers.IO){
            val item = backupRepo.getBackupByID(id).first()

            _backupFilePath post event(Uri.parse(item.filePath))
        }
    }

    fun selectImportUri(uri: Uri){
        _backupFilePath post event(uri)
    }


}
