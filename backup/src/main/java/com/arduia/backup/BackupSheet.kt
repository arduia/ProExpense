package com.arduia.backup

import jxl.Sheet
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableWorkbook
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException

abstract class BackupSheet<T>(private val source: BackupSource<T>) {

    internal suspend fun import(book: Workbook) {

        val sheet = book.getSheet(sheetName)

        try {
            val sheetData = getDataFromSheet(sheet)
            source.writeAll(sheetData)
        } catch (ie: IllegalArgumentException) {
            throw BackupException("Data Type doesn't match", ie)
        }
    }

    private fun getDataFromSheet(sheet: Sheet): List<T> {

        val sheetData = mutableListOf<T>()

        //Header
        val columnNames = getFieldNames()
        //Total Item
        val rowCount = sheet.rows

        //EveryRow UseCase
        val tmpRowMap = mutableMapOf<String, String>()
        //Jump Header Row. start from 1
        (1 until rowCount).forEach { rowNo ->
            //Read every Column and map
            columnNames.forEachIndexed { columnNo, columnName ->

                val cellData = sheet.getCell(columnNo, rowNo).contents
                tmpRowMap[columnName] = cellData
            }
            //create entity from map
            val data = create(tmpRowMap)
            //add map to data list
            sheetData.add(data)
            //recycle map instance
            tmpRowMap.clear()
        }

        return sheetData
    }

    internal suspend fun export(book: WritableWorkbook, index: Int): Int {
        val sheet = book.createSheet(sheetName, index)
        val fieldNames = getFieldNames()
        var itemCount = 0

        //Add Headers
        fieldNames.forEachIndexed { column, label ->
            sheet.addCell(Label(column, 0, label))
        }

        source.readAll().forEachIndexed { itemPosition, data ->
            itemCount++
            //Shift 1 of Header Row
            val rowNo = itemPosition + 1

            val map = map(data)

            fieldNames.forEachIndexed { column, name ->
                val cellValue = map[name] ?: ""
                sheet.addCell(Label(column, rowNo, cellValue))
            }
        }

        return itemCount
    }

    internal fun itemCounts(book: Workbook): Int {
        val sheet = book.getSheet(sheetName)

        val rowCount = sheet.rows
        //No Header
        if (rowCount == 0) return -1

        val headerRow = sheet.getRow(0)
        val fieldNames = getFieldNames()
        //assume valid first
        var isValidSheet = true

        try {

            fieldNames.forEachIndexed { index, name ->
                //Get Header Row Cells * can cause index out of error *
                val labelCell = headerRow[index]
                isValidSheet = isValidSheet && (labelCell.contents == name)
            }

            if (isValidSheet.not()) return -1

        } catch (e: IndexOutOfBoundsException) {
            return -1
        }

        //Remove first header row
        return (rowCount - 1)
    }

    protected abstract val sheetName: String

    protected abstract fun getFieldNames(): List<String>

    protected abstract fun create(row: Map<String, String>): T

    protected abstract fun map(item: T): Map<String, String>

}
