package com.arduia.backup

import com.arduia.backup.task.BackupCountResult
import com.arduia.backup.task.BackupResult
import jxl.Sheet
import jxl.Workbook
import jxl.write.Label
import jxl.write.WritableWorkbook
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException

abstract class BackupSheet<Entity>(private val source: BackupSource<Entity>) :
    AbstractBackupSheet<Workbook, WritableWorkbook, Int> {

    protected var isExportCountEnable = true

    override suspend fun import(input: Workbook): BackupResult<Int> {
        val count: Int
        val sheet = input.getSheet(sheetName)
        try {
            val sheetData = getDataFromSheet(sheet)
            this.source.writeAll(sheetData)
            count = sheetData.size
        } catch (ie: IllegalArgumentException) {
            throw BackupException("Data Type doesn't match", ie)
        }
        return BackupCountResult(count)
    }

    override suspend fun export(out: WritableWorkbook, index: Int): BackupResult<Int> {
        val sheet = out.createSheet(sheetName, index)
        val fieldNameList = getFieldInfo().keys
        var itemCount = 0

        fieldNameList.forEachIndexed { column, label ->
            sheet.addCell(Label(column, 0, label))
        }

        val sheetItemList = source.readAll()

        sheetItemList.forEachIndexed { rowNo, data ->
            itemCount++
            val dataRowNo = rowNo + 1
            val rawData = mapToSheetRow(data)
            fieldNameList.forEachIndexed { columnNo, columnName ->
                val cellContent = rawData[columnName] ?: ""
                sheet.addCell(Label(columnNo, dataRowNo, cellContent))
            }
        }
        if(isExportCountEnable.not()){
            return BackupCountResult(0)
        }
        return BackupCountResult(itemCount)
    }


    private fun getDataFromSheet(sheet: Sheet): List<Entity> {

        val sheetData = mutableListOf<Entity>()
        val columnNameList = getFieldInfo().keys
        val rowCount = sheet.rows
        val rowDataTmp = mutableMapOf<String, String>()
        val rowNoList = (1 until rowCount)

        rowNoList.forEach { rowNo ->
            columnNameList.forEachIndexed { columnNo, columnName ->
                val cellContent = sheet.getCell(columnNo, rowNo).contents
                rowDataTmp[columnName] = cellContent
            }
            val rowItem = mapToEntity(SheetRow.createFromMap(rowDataTmp))
            sheetData.add(rowItem)
            rowDataTmp.clear()
        }

        return sheetData
    }

    override suspend fun getItemCount(input: Workbook): Int {
        val invalidCount = -1
        val headerRowCount = 1

        val sheet = input.getSheet(sheetName)
        val totalRowCount = sheet.rows
        if (totalRowCount == 0) return invalidCount

        val headerRow = sheet.getRow(0)
        val fieldNameList = getFieldInfo().keys
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

    protected abstract fun getFieldInfo(): SheetFieldInfo

    protected abstract fun mapToEntity(row: SheetRow): Entity

    protected abstract fun mapToSheetRow(item: Entity): SheetRow

}
