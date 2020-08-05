package com.arduia.expense.ui.backup

import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.backup.ImportWorker
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class BackupDetailViewModel @ViewModelInject constructor(private val backupRepo: BackupRepository,
                                                         private val workManger: WorkManager) : ViewModel(){

    private val _fileName = BaseLiveData<String>()
    val fileName = _fileName.asLiveData()

    private val _totalCount = BaseLiveData<String>()
    val totalCount = _totalCount.asLiveData()

    private var currentSelectedUri: Uri? = null

    fun setFileUri(uri: Uri){

        currentSelectedUri = uri

        _fileName post (uri.host ?:"")

        viewModelScope.launch(Dispatchers.IO){
            val itemCount = backupRepo.getItemCount(uri).first()
            val fileName = (File(uri.path)).name

            _fileName post fileName
            _totalCount post "$itemCount"
        }
    }

    fun importData(){
        val fileUri = currentSelectedUri?.toString()?: return
        val importRequest = OneTimeWorkRequestBuilder<ImportWorker>()
            .setInputData(Data.Builder().putString("FILE_URI",fileUri).build())
            .build()
        workManger.enqueue(importRequest)
        Timber.d("Start Import")
    }
}
