package com.arduia.backup

import jxl.Sheet
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableWorkbook
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException

abstract class BackupSheet<T>(private val source: BackupSource<T>) {

    internal suspend fun import(book: Workbook): Int {
        var count = -1
        val sheet = book.getSheet(sheetName)
        try {
            val sheetData = getDataFromSheet(sheet)
            source.writeAllItem(sheetData)
            count = sheetData.size
        } catch (ie: IllegalArgumentException) {
            throw BackupException("Data Type doesn't match", ie)
        }
        return count
    }

    private fun getDataFromSheet(sheet: Sheet): List<T> {

        val sheetData = mutableListOf<T>()
        val columnNameList = getSheetFieldNames()
        val rowCount = sheet.rows
        val rowDataTmp = mutableMapOf<String, String>()
        val rowNoList = (1 until rowCount)

        rowNoList.forEach { rowNo ->
            columnNameList.forEachIndexed { columnNo, columnName ->
                val cellContent = sheet.getCell(columnNo, rowNo).contents
                rowDataTmp[columnName] = cellContent
            }
            val rowItem = mapToEntityFromSheetData(rowDataTmp)
            sheetData.add(rowItem)
            rowDataTmp.clear()
        }

        return sheetData
    }

    internal suspend fun export(book: WritableWorkbook, index: Int): Int {
        val sheet = book.createSheet(sheetName, index)
        val fieldNameList = getSheetFieldNames()
        var itemCount = 0

        fieldNameList.forEachIndexed { column, label ->
            sheet.addCell(Label(column, 0, label))
        }

        val sheetItemList = source.readAllItem()

        sheetItemList.forEachIndexed { rowNo, data ->
            itemCount++
            val dataRowNo = rowNo + 1
            val rawData = mapToSheetDataFromEntity(data)
            fieldNameList.forEachIndexed { columnNo, columnName ->
                val cellContent = rawData[columnName] ?: ""
                sheet.addCell(Label(columnNo, dataRowNo, cellContent))
            }
        }
        return itemCount
    }

    internal fun itemCounts(book: Workbook): Int {
        val invalidCount = -1
        val headerRowCount = 1

        val sheet = book.getSheet(sheetName)
        val totalRowCount = sheet.rows
        if (totalRowCount == 0) return invalidCount

        val headerRow = sheet.getRow(0)
        val fieldNameList = getSheetFieldNames()
        var isValidSheet = true

        try {
            fieldNameList.forEachIndexed { position, fieldName ->
                val labelCell = headerRow[position]
                val isValidFieldName = (labelCell.contents == fieldName)
                isValidSheet = isValidSheet && isValidFieldName
            }
        } catch (e: IndexOutOfBoundsException) {
            return invalidCount
        }
        if (isValidSheet.not()) return invalidCount
        return (totalRowCount - headerRowCount)
    }

    protected abstract val sheetName: String

    protected abstract fun getSheetFieldNames(): List<String>

    protected abstract fun mapToEntityFromSheetData(row: Map<String, String>): T

    protected abstract fun mapToSheetDataFromEntity(item: T): Map<String, String>

}
