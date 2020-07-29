package com.arduia.expense.data.backup

import android.content.Context
import android.os.Environment
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import com.arduia.backup.BackupDto
import com.arduia.backup.EncryptionDto
import com.arduia.backup.FieldDto
import com.arduia.backup.MetadataDto
import com.arduia.backup.generator.BackupDataNameGenerator
import com.arduia.backup.generator.MetadataFileNameGen
import com.arduia.core.isStoragePermissionGrated
import com.arduia.expense.data.AccRepository
import com.arduia.expense.data.BackupRepository
import com.arduia.expense.data.local.BackupEnt
import com.google.gson.GsonBuilder
import de.siegmar.fastcsv.writer.CsvWriter
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

class ExportWorker @WorkerInject constructor(@Assisted app: Context,
                                             @Assisted param: WorkerParameters,
                                             private val backupRepo: BackupRepository,
                                             private val expenseRep: AccRepository) : CoroutineWorker(app, param){

    override suspend fun doWork(): Result {

        val isStoragePmGranted = applicationContext.isStoragePermissionGrated()
        if(!isStoragePmGranted) return Result.failure()

        val backupFileName = BackupDataNameGenerator("expense").generate()
        val metadataFileName = MetadataFileNameGen().generate()
        val totalSize = expenseRep.getExpenseTotalCount().first()
        val savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path

        try {
            val expenseExport = getExpenseBackup(backupFileName, totalSize)
            val backupName = inputData.getString("BACKUP_NAME") ?: metadataFileName

            val metadataDto = MetadataDto(
                backup = listOf(expenseExport),
                name = backupName,
                encryption = EncryptionDto(false),
                created_date = Date().time,
                version_code = 1
            )

            val newBackupEnt = BackupEnt(
                name = backupName,
                backupId = 0,
                filePath = "$savePath${File.pathSeparator}$metadataFileName",
                workerId = id.toString(),
                itemTotal = totalSize,
                isCompleted = false,
                createdDate = Date().time
            )

            backupRepo.insertBackup(newBackupEnt)

            val json = GsonBuilder()
                .setPrettyPrinting()
                .create().toJson(metadataDto)

            Timber.d("file URl -> $savePath")

            with(
                FileWriter("$savePath/$metadataFileName")){
                write(json)
                flush()
                close()
            }

            val csvWriter = CsvWriter()
            val metadataFile = File("$savePath/$backupFileName")
            val csvAppender = csvWriter.append(metadataFile, StandardCharsets.UTF_8)

            expenseExport.fields.forEach {
                csvAppender.appendField(it.name)
            }
            csvAppender.endLine()

            val totalExpenses = expenseRep.getExpenseRange(totalSize, 0 ).first()

            totalExpenses.forEach {
                csvAppender.appendLine(it.name, it.amount.toString(), it.note, it.category.toString(), it.createdDate.toString())
            }

            csvAppender.flush()
            csvAppender.close()

            //Update Completed..
            val currentBackupEnt = backupRepo.getBackupByWorkerID(id.toString()).first()
            currentBackupEnt.isCompleted = true
            backupRepo.updateBackup(currentBackupEnt)

        } catch (e: IOException){
            Timber.d("IOException -> ${e.message}")
            return Result.failure()
        }

        return Result.success()
    }

    private fun getExpenseBackup(fileName: String, totalCount: Int): BackupDto {

        val fields = mutableListOf<FieldDto>().apply {
            add(FieldDto("Name", "string"))
            add(FieldDto("Amount", "long"))
            add(FieldDto("Note", "string"))
            add(FieldDto("Category", "string"))
            add(FieldDto("Date", "long"))
        }

        return BackupDto(
            end_date = Date().time,
            start_date = Date().time,
            file_name = fileName,
            item_total = totalCount,
            fields = fields
        )
    }

}
