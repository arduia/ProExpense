package com.arduia.expense.ui.backup

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.ui.mapping.BackupMapper
import com.arduia.expense.ui.vto.BackupVto
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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


}
