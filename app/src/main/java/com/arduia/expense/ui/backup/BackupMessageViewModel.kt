package com.arduia.expense.ui.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.arduia.expense.data.backup.ImportWorker
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.event
import com.arduia.mvvm.post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BackupMessageViewModel @Inject constructor(private val workManager: WorkManager)
    : ViewModel(){

    private val _finishedEvent = EventLiveData<Int>()
    val finishedEvent get() = _finishedEvent.asLiveData()

    private val taskIdList = mutableListOf<UUID>()

    fun addTaskID(id: UUID){
        taskIdList.add(id)
        notifyTaskIdListChanged()
    }

    fun removeTaskID(id: UUID){
        taskIdList.remove(id)
        notifyTaskIdListChanged()
    }

    private fun notifyTaskIdListChanged(){
       viewModelScope.launch(Dispatchers.IO){
           observeTasks()
       }
    }

    private fun observeTasks(){
        val liveDataList = taskIdList.map {
             workManager.getWorkInfoByIdLiveData(it)
        }

        liveDataList.forEach { liveData ->
            collectTaskMessageFlow(liveData.asFlow())
        }
    }

    private fun collectTaskMessageFlow(flow: Flow<WorkInfo>){
        viewModelScope.launch(Dispatchers.IO){
            flow.collectLatest(workInfoListener)
        }
    }

    private val workInfoListener: suspend (WorkInfo) -> Unit = {

        val isFinished = isWorkFinished(it)
        Timber.d("isFinished $isFinished $it")
        if(isFinished){
            val count = getTotalItemCount(it)
            val isValidCount = (count > -1)
            if(isValidCount){
                _finishedEvent post event(count)
            }
            removeTaskID(it.id)
        }

    }

    private fun getTotalItemCount(info: WorkInfo)
    = info.outputData.getInt(ImportWorker.KEY_IMPORT_COUNT, -1)

    private fun isWorkFinished(info: WorkInfo) =
        (info.state == WorkInfo.State.SUCCEEDED)
}
