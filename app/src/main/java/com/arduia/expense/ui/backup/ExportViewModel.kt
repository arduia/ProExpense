package com.arduia.expense.ui.backup

import android.app.Application
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arduia.backup.FileNameGenerator
import com.arduia.expense.data.backup.ExportWorker
import com.arduia.expense.di.BackupNameGen
import com.arduia.mvvm.BaseLiveData
import com.arduia.mvvm.set

class ExportViewModel @ViewModelInject constructor(app: Application,
                                                   @BackupNameGen
                                                   private val fileNameGen: FileNameGenerator,
                                                   private val workManager: WorkManager)
    : AndroidViewModel(app), LifecycleObserver{

    private val _exportFileName = BaseLiveData<String>()
    val exportFileName = _exportFileName.asLiveData()


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate(){
        _exportFileName set fileNameGen.generate()
    }

    fun exportData(fileName:String, fileUri: Uri){

        val inputUriData = Data.Builder()
            .putString(ExportWorker.FILE_URI, fileUri.toString())
            .putString(ExportWorker.FILE_NAME, fileName)
            .build()

        val exportRequest = OneTimeWorkRequestBuilder<ExportWorker>()
            .setInputData(inputUriData)
            .build()

        workManager.enqueue(exportRequest)
    }

}