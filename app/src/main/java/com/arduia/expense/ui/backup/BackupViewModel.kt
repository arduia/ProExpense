package com.arduia.expense.ui.backup

import android.app.Application
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.ui.mapping.BackupMapper
import com.arduia.expense.ui.vto.BackupVto
import com.arduia.mvvm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

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

            backupRepo.getBackupAll().collect {list ->

                val backupVtoList = list.map {  mapper.mapToBackupVto(it) }

                _backupList post backupVtoList
            }

        }
    }

    fun onBackupItemSelect(id: Int){
        viewModelScope.launch(Dispatchers.IO){
            val backupEnt = backupRepo.getBackupByID(id).first()

            val backupFileUri = Uri.parse(backupEnt.filePath)

            _backupFilePath post event(backupFileUri)
        }
    }

    fun setImportUri(uri: Uri){
        _backupFilePath post event(uri)
    }
}
