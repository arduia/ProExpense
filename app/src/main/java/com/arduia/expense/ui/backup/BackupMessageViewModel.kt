package com.arduia.expense.ui.backup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.arduia.mvvm.EventLiveData
import com.arduia.mvvm.event
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class BackupMessageViewModel @ViewModelInject constructor(private val workManager: WorkManager)
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
            flow.collect(workInfoListener)
        }
    }

    private val workInfoListener: suspend (WorkInfo) -> Unit = {
        val isValid = isFinishedData(it)
        if(isValid){
            val count = getTotalItemCount(it)
            _finishedEvent post event(count)
        }
    }
    private fun getTotalItemCount(info: WorkInfo)  = 10
    private fun isFinishedData(info: WorkInfo) = true
}
