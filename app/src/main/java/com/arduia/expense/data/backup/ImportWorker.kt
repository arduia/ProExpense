package com.arduia.expense.data.backup

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arduia.backup.ExcelBackup
import java.io.File

class ImportWorker  @WorkerInject constructor(@Assisted context: Context,
                                              @Assisted param: WorkerParameters,
                                              private val excelBackup: ExcelBackup ): CoroutineWorker(context, param){
    override suspend fun doWork(): Result {

        val filePath = inputData.getString("FILE_PATH")?: return Result.failure()

        excelBackup.import(filePath)

        return Result.success()
    }
}
