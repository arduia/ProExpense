package com.arduia.expense.ui.backup

import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arduia.backup.FileNameGenerator
import com.arduia.expense.data.backup.ExportWorker
import com.arduia.expense.di.BackupNameGen
import com.arduia.expense.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class ExportUiState(
    val exportFileName: String = ""
)

sealed class ExportUiEffect

@HiltViewModel
class ExportViewModel @Inject constructor(
    @BackupNameGen
    private val fileNameGen: FileNameGenerator,
    private val workManager: WorkManager
) : BaseViewModel<ExportUiState, ExportUiEffect>(ExportUiState()) {

    init {
        setState { copy(exportFileName = fileNameGen.generate()) }
    }

    fun exportDataTo(fileName: String, fileUri: Uri) {
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
