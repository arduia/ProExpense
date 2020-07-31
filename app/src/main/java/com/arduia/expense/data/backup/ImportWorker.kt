package com.arduia.expense.data.backup

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.backup.BackupSheet
import com.arduia.backup.ExcelBackup
import com.arduia.backup.generator.BackupNameGenerator
import com.arduia.expense.data.local.ExpenseEnt
import java.io.File

class ImportWorker  @WorkerInject constructor(@Assisted context: Context,
                                              @Assisted param: WorkerParameters,
                                              private val backupSheet: BackupSheet<ExpenseEnt>
): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {

        val savePath = inputData.getString("SAVE_PATH")?: return Result.failure()

        val fileName =  "BackupTest.xls"

        val excelBackup = ExcelBackup.Builder()
            .addBackupSheet(backupSheet)
            .build()

        excelBackup.import("$savePath${File.separator}$fileName")

        return Result.success()
    }
}
