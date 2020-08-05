package com.arduia.expense.data.backup

import android.content.Context
import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.backup.ExcelBackup
import java.lang.Exception

class ImportWorker  @WorkerInject constructor(@Assisted context: Context,
                                              @Assisted param: WorkerParameters,
                                              private val excelBackup: ExcelBackup ): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {

        val fileUriString = inputData.getString("FILE_URI")
        fileUriString?.let {
            importFromUri(it)
            return Result.success()
        }

        val filePathString = inputData.getString("FILE_PATH")
        filePathString?.let {
            importFromFile(it)
        } ?: return Result.failure()

        return Result.success()
    }

    private suspend fun importFromUri(uri: String){

        val fileUri = Uri.parse(uri)

        val contentResolver = applicationContext.contentResolver

        val fileInputStream = contentResolver.openInputStream(fileUri)
            ?: throw Exception("Cannot Open InputStream from Content Provider Uri")

        excelBackup.import(fileInputStream)
    }

    private suspend fun importFromFile(filePath: String){
        excelBackup.import(filePath)
    }

}
