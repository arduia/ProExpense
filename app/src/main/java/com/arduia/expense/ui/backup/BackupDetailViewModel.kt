package com.arduia.expense.ui.backup

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.backup.ImportWorker
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class BackupDetailViewModel @ViewModelInject constructor(private val backupRepo: BackupRepository,
                                                         private val workManger: WorkManager)
    : ViewModel(){

    private val _fileName = BaseLiveData<String>()
    val fileName = _fileName.asLiveData()

    private val _totalCount = BaseLiveData<String>()
    val totalCount = _totalCount.asLiveData()

    private val _filePath = BaseLiveData<String>()
    val filePath = _filePath.asLiveData()

    fun setFile(filePath: String){

        viewModelScope.launch(Dispatchers.IO){

            val file = File(filePath)

            _fileName post file.name
            _filePath post filePath

            val itemCount = backupRepo.getItemCount(filePath).first()

            _totalCount post itemCount.toString()
        }
    }

    fun importData(){
        val filePath = _filePath.value ?: return

        val importRequest = OneTimeWorkRequestBuilder<ImportWorker>()
            .setInputData(Data.Builder().putString("FILE_PATH",filePath).build())
            .build()
        workManger.enqueue(importRequest)
    }
}
