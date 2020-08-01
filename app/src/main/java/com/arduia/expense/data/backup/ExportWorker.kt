package com.arduia.expense.data.backup

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.backup.BackupSheet
import com.arduia.backup.ExcelBackup
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.local.BackupEnt
import com.arduia.expense.data.local.ExpenseEnt
import kotlinx.coroutines.flow.first
import java.io.File
import java.util.*

class ExportWorker @WorkerInject constructor(@Assisted context: Context,
                                             @Assisted param: WorkerParameters,
                                             private val backupRep: BackupRepository,
                                             private val excelBackup: ExcelBackup): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {

        val savePath = inputData.getString("SAVE_PATH")?: return Result.failure()

        val fileName =  "BackupTest.xls"

        val filePath = "$savePath${File.separator}$fileName"

        val taskBackupItem = BackupEnt(0, fileName, filePath, Date().time,0, id.toString(), false )
        backupRep.insertBackup(taskBackupItem)

        val totalCount = excelBackup.export(filePath)

        val backupItemBefore = backupRep.getBackupByWorkerID(id.toString()).first()
        backupItemBefore.isCompleted= true
        backupItemBefore.itemTotal = totalCount

        backupRep.updateBackup(backupItemBefore)

        return Result.success()
    }
}
