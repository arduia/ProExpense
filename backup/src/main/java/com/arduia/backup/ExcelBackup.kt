package com.arduia.backup

import jxl.Workbook
import java.io.File
import java.io.IOException

class ExcelBackup private constructor(private val sheets: List<BackupSheet<*>>) {

    @Throws(IOException::class, SecurityException::class)
    suspend fun export(filePath: String): Int{
        var totalCount = 0

        val book = Workbook.createWorkbook(File(filePath))

        sheets.forEachIndexed { index, backupSheet ->
            totalCount+= backupSheet.export(book, index)
        }

        book.write()
        book.close()

        return totalCount
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun import(filePath: String) {
        val book = Workbook.getWorkbook(File(filePath))

        sheets.forEach {backupSheet ->
            backupSheet.import(book)
        }

        book.close()
    }

    @Throws(IOException::class, SecurityException::class)
    suspend fun itemCount(filePath: String): Int{

        var itemCount = 0

        val book = Workbook.getWorkbook(File(filePath))

        sheets.forEach {backupSheet ->
            itemCount += backupSheet.itemCounts(book)
        }
        book.close()

        if(itemCount < 0) return  -1

        return itemCount
    }

    class Builder{
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
