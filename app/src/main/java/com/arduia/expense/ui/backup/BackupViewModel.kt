package com.arduia.expense.ui.backup

import android.app.Application
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.backup.ExportWorker
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.ui.mapping.BackupMapper
import com.arduia.expense.ui.vto.BackupVto
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class BackupViewModel @ViewModelInject constructor(
    app: Application,
    private val mapper: BackupMapper,
    private val backupRepo: BackupRepository
    ): AndroidViewModel(app), LifecycleObserver{

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

    fun exportBackup(backupName: String){

        val backupData = Data.Builder()
            .putString("BACKUP_NAME",backupName)
            .build()

        val exportRequest = OneTimeWorkRequestBuilder<ExportWorker>()
            .setInputData(backupData)
            .build()

        WorkManager.getInstance(getApplication())
            .enqueue(exportRequest)
    }

}
