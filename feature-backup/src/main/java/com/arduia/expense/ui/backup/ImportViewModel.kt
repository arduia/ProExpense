package com.arduia.expense.ui.backup

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.backup.ImportWorker
import com.arduia.expense.di.IntegerDecimal
import com.arduia.expense.model.data
import com.arduia.expense.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

data class ImportUiState(
    val fileName: String = "",
    val totalCount: String = "",
    val isLoading: Boolean = false
)

sealed class ImportUiEffect {
    object Close : ImportUiEffect()
    object FileNotFound : ImportUiEffect()
    data class BackupTaskStarted(val taskId: UUID) : ImportUiEffect()
}

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val backupRepo: BackupRepository,
    private val contentResolver: ContentResolver,
    @IntegerDecimal
    private val decimalFormat: DecimalFormat,
    private val workManger: WorkManager
) : BaseViewModel<ImportUiState, ImportUiEffect>(ImportUiState()) {

    private var currentSelectedUri: Uri? = null

    fun setFileUri(uri: Uri) {
        currentSelectedUri = uri

        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(isLoading = true) }
            try {
                val itemCount = backupRepo.getItemCount(uri).first().data ?: throw Exception()
                val isInvalidItem = (itemCount < 0)

                if (isInvalidItem) {
                    sendEffect(ImportUiEffect.FileNotFound)
                    sendEffect(ImportUiEffect.Close)
                    setState { copy(isLoading = false) }
                    return@launch
                }

                val fileName = getFileNameFromUri(uri = uri)

                setState {
                    copy(
                        isLoading = false,
                        fileName = fileName,
                        totalCount = decimalFormat.format(itemCount)
                    )
                }

            } catch (e: SecurityException) {
                setState { copy(isLoading = false) }
                sendEffect(ImportUiEffect.Close)
                sendEffect(ImportUiEffect.FileNotFound)
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        val fileNameColumnIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(fileNameColumnIndex)
    }

    fun startImportData() {
        val fileUri = currentSelectedUri?.toString() ?: return

        val inputData = Data.Builder()
            .putString(ImportWorker.FILE_URI, fileUri).build()

        val importRequest = OneTimeWorkRequestBuilder<ImportWorker>()
            .setInputData(inputData)
            .build()

        workManger.enqueue(importRequest)
        sendEffect(ImportUiEffect.BackupTaskStarted(importRequest.id))
        sendEffect(ImportUiEffect.Close)
    }
}
