package com.arduia.expense.ui.backup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.vto.BackupVto
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

class BackupViewModel @ViewModelInject constructor(
    private val mapper: BackupMapper,
    private val backupRepo: BackupRepository
    ): ViewModel(), LifecycleObserver{

    private val _backupList = BaseLiveData<List<BackupVto>>()
    val backupList = _backupList.asLiveData()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        viewModelScope.launch (Dispatchers.IO){
            backupRepo.getBackupAll().collect {
                _backupList post it.map { backupEnt ->  mapper.mapToBackupVto(backupEnt) }
            }
        }
    }

    fun addBackup(){
        viewModelScope.launch(Dispatchers.IO){
            backupRepo.insertBackup(
                BackupEnt(
                    backupID = 1,
                    name = "Backup_some",
                    created_date = Date().time,
                    type = -1,
                    progress_items = 100,
                    total_items = 199,
                    isExported = false,
                    url = "/sdcard/Download/ProExpense.expense"
                )
            )
        }
    }

}