package com.arduia.expense.data.backup

import android.content.Context
import android.net.Uri
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.local.BackupEnt
import kotlinx.coroutines.flow.first
import java.util.*

class ExportWorker @WorkerInject constructor(@Assisted context: Context,
                                             @Assisted param: WorkerParameters,
                                             private val backupRep: BackupRepository,
                                             private val excelBackup: ExcelBackup): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {

        val uriString = inputData.getString(FILE_URI)?: return Result.failure()
        val fileName = inputData.getString(FILE_NAME)?: return Result.failure()

        //Save Progress to Repo
        val taskBackupItem = BackupEnt(0, fileName, uriString, Date().time,0, id.toString(), false )
        backupRep.insertBackup(taskBackupItem)

        val contentResolver =  applicationContext.contentResolver

        val fileUri = Uri.parse(uriString)
        val outputStream = contentResolver.openOutputStream(fileUri) ?: return Result.failure()

        val totalCount = excelBackup.export(outputStream)

        val backupItemBefore = backupRep.getBackupByWorkerID(id.toString()).first()
        backupItemBefore.isCompleted= true
        backupItemBefore.itemTotal = totalCount

        backupRep.updateBackup(backupItemBefore)

        return Result.success()
    }

    companion object{
        const val FILE_URI = "FILE_URI"
        const val FILE_NAME = "FILE_NAME"
    }
}
