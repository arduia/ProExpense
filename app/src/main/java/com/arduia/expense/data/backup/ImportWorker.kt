package com.arduia.expense.data.backup

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.backup.BackupException
import com.arduia.backup.ExcelBackup
import java.lang.Exception

class ImportWorker  @WorkerInject constructor(@Assisted context: Context,
                                              @Assisted param: WorkerParameters,
                                              private val contentResolver: ContentResolver,
                                              private val excelBackup: ExcelBackup ): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {

        val inputFileUri = inputData.getString(FILE_URI) ?: return Result.failure()
        val importUri = Uri.parse(inputFileUri)
        val fileInputStream = contentResolver.openInputStream(importUri)
            ?: throw Exception("Cannot Open InputStream from Content Provider Uri")

        try {
           val count =  excelBackup.import(fileInputStream)
        }catch (e: BackupException){
            return Result.failure()
        }
        return Result.success()
    }

    companion object{
        const val FILE_URI = "FILE_URI"
    }
}
