package com.arduia.expense.data.backup

import com.arduia.backup.BackupSheet
import com.arduia.backup.BackupSource
import com.arduia.backup.SheetFieldInfo
import com.arduia.backup.SheetRow
import com.arduia.expense.data.local.ExpenseEnt
import com.arduia.expense.domain.Amount
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class ExpenseBackupSheet @Inject constructor(source: BackupSource<ExpenseEnt>) : BackupSheet<ExpenseEnt>(source) {

    override val sheetName = "expense"

    override fun getFieldInfo(): SheetFieldInfo {
         val mutableMap = mutableMapOf<String, String>()
        mutableMap[FIELD_NAME] = "string"
        mutableMap[FIELD_DATE] = "long"
        mutableMap[FIELD_CATEGORY] = "integer"
        mutableMap[FIELD_AMOUNT] = "long"
        mutableMap[FIELD_NOTE] = "string"
        return SheetFieldInfo.createFromMap(mutableMap)
    }


    override fun mapToEntity(row: SheetRow): ExpenseEnt {

        val name = row[FIELD_NAME]
        val amount = BigDecimal(row[FIELD_AMOUNT] ?: "0.0")
        val category = row[FIELD_CATEGORY]?.toInt() ?: 0
        val note = row[FIELD_NOTE]
        val date = row[FIELD_DATE]?.toLong() ?: 0

        return ExpenseEnt(
            0,
            name,
            Amount.createFromActual(amount),
            category,
            note,
            createdDate = Date().time,
            modifiedDate = date
        )
    }

    override fun mapToSheetRow(item: ExpenseEnt): SheetRow {
        val data=  mutableMapOf<String, String>().apply {
            put(FIELD_NAME, item.name ?: "")
            put(FIELD_AMOUNT, item.amount.getActual().toString())
            put(FIELD_CATEGORY, item.category.toString())
            put(FIELD_NOTE, item.note ?: "")
            put(FIELD_DATE, item.createdDate.toString())
        }

        return SheetRow.createFromMap(data)
    }

    companion object {
        private const val FIELD_NAME = "Name"
        private const val FIELD_AMOUNT = "Amount"
        private const val FIELD_CATEGORY = "Category"
        private const val FIELD_NOTE = "Note"
        private const val FIELD_DATE = "Date"
    }
    
}
