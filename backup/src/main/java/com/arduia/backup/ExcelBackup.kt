package com.arduia.backup

import jxl.Workbook
import jxl.read.biff.BiffException
import jxl.write.WritableWorkbook
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class ExcelBackup private constructor(private val sheets: List<BackupSheet<*>>) {


    @Throws(IOException::class, SecurityException::class)
    suspend fun export(outputStream: OutputStream): Int {
        val book = Workbook.createWorkbook(outputStream)
        return exportData(book)
    }

    private suspend fun exportData(book: WritableWorkbook): Int {
        var totalCount = 0
        sheets.forEachIndexed { index, backupSheet ->
            totalCount += backupSheet.export(book, index)
        }
        book.write()
        book.close()
        return totalCount
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun import(inputStream: InputStream) {
        try {
            val book = Workbook.getWorkbook(inputStream)
            importExcelData(book)
        } catch (e: BiffException) {
            throw BackupException("File Not Found Exception", e)
        }
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun import(filePath: String) {
        val book = Workbook.getWorkbook(File(filePath))
        importExcelData(book)
    }

    private suspend fun importExcelData(book: Workbook) {
        sheets.forEach { backupSheet ->
            backupSheet.import(book)
        }
        book.close()
    }

    @Throws(IOException::class, SecurityException::class)
    fun itemCount(inputStream: InputStream): Int {
        try {
            val book = Workbook.getWorkbook(inputStream)
            return itemCount(book)
        } catch (e: BiffException) {
            throw BackupException("File Doesn't Exist", e)
        }
    }

    private fun itemCount(book: Workbook): Int {
        var itemCount = 0
        sheets.forEach { backupSheet ->
            itemCount += backupSheet.itemCounts(book)
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
