package com.arduia.backup

import com.arduia.backup.task.BackupCountResult
import com.arduia.backup.task.BackupResult
import com.arduia.backup.task.getDataOrError
import jxl.Workbook
import jxl.read.biff.BiffException
import jxl.write.WritableWorkbook
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ExcelBackup private constructor(private val sheets: List<BackupSheet<*>>) {


    @Throws(IOException::class, SecurityException::class)
    suspend fun export(outputStream: OutputStream): BackupResult<Int> {
        val book = Workbook.createWorkbook(outputStream)
        return exportData(book)
    }

    private suspend fun exportData(book: WritableWorkbook): BackupResult<Int> {
        var totalCount = BackupCountResult.empty()
        sheets.forEachIndexed { index, backupSheet ->
            val count =  backupSheet.export(book, index)
            totalCount = totalCount.plus(count)
        }
        book.write()
        book.close()
        return totalCount
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun import(inputStream: InputStream):BackupResult<Int> {
        val count: BackupResult<Int>
        try {
            val book = Workbook.getWorkbook(inputStream)
            count = importExcelData(book)

        } catch (e: BiffException) {
            throw BackupException("File Not Found Exception", e)
        }
        return count
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun import(filePath: String) {
        val book = Workbook.getWorkbook(File(filePath))
        importExcelData(book)
    }

    private suspend fun importExcelData(book: Workbook): BackupResult<Int>{
        var count = BackupCountResult.empty()
        sheets.forEach { backupSheet ->
            count = backupSheet.import(book)
        }
        book.close()
        return count
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun itemCount(inputStream: InputStream): Int {
        try {
            val book = Workbook.getWorkbook(inputStream)
            return itemCount(book)
        } catch (e: BiffException) {
            throw BackupException("File Doesn't Exist", e)
        }
    }

    private suspend fun itemCount(book: Workbook): Int {
        var itemCount = 0
        sheets.forEach { backupSheet ->
            itemCount += backupSheet.getItemCount(book)
        }
        book.close()
        if (itemCount < 0) return -1
        return itemCount
    }

    class Builder {
        private val sheets = mutableListOf<BackupSheet<*>>()

        fun <T> addBackupSheet(sheet: BackupSheet<T>): Builder {
            sheets.add(sheet)
            return this
        }

        fun build(): ExcelBackup {
            return ExcelBackup(sheets)
        }

    }
}
