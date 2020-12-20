package com.arduia.expense.data.backup

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.model.awaitValueOrError
import com.arduia.expense.model.data
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import java.util.*

class ExportWorker @WorkerInject constructor(@Assisted context: Context,
                                             @Assisted param: WorkerParameters,
                                             private val contentResolver: ContentResolver,
                                             private val backupRepo: BackupRepository,
                                             private val excelBackup: ExcelBackup): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {

        val inputFileUri = inputData.getString(FILE_URI)?: return Result.failure()
        val inputFileName = inputData.getString(FILE_NAME)?: return Result.failure()

        val initialBackupLog = createBackupEntityForExportWork(exportName = inputFileName, exportUri = inputFileUri)
        backupRepo.insertBackup(item = initialBackupLog)

        val exportUri = Uri.parse(inputFileUri)
        val outputStream = contentResolver.openOutputStream(exportUri) ?: return Result.failure()
        val exportedItemCount = excelBackup.export(outputStream)
        updateBackupLogAsCompleted(itemCount = exportedItemCount)
        return Result.success()
    }

    private suspend fun updateBackupLogAsCompleted(itemCount: Int){
        val oldBackupLog = backupRepo.getBackupByWorkerID(id.toString()).awaitValueOrError()
        oldBackupLog.isCompleted= true
        oldBackupLog.itemTotal = itemCount
        backupRepo.updateBackup(oldBackupLog)
    }

    private fun createBackupEntityForExportWork(exportName: String, exportUri: String)=
         BackupEnt(backupId = 0,
             name = exportName,
             filePath =  exportUri,
             createdDate = Date().time,
             itemTotal = 0,
             workerId = id.toString(),
             isCompleted = false)


    companion object{
        const val FILE_URI = "FILE_URI"
        const val FILE_NAME = "FILE_NAME"
    }
}
